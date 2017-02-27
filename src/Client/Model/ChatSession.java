package Client.Model;

import javafx.concurrent.Task;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class establishes a peer-to-peer connection between two clients. If connection is established, this class
 * manages the chat session between the two clients. Once the two clients are connected to each other:a  new thread
 * will start to listen for messages from the other client through packets.
 */
public class ChatSession extends Task<Void> {

    private Socket sc;
    private String ip;
    private int port;
    private ServerSocket ssc;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String chattingWith;

    //To contact
    /**
     * This constructs a chat session between two clients where ip and port is specified.
     * It constructs a new socket and this socket is the socket where the  client can
     * send and receive data from each other.
     *
     * @param ip the ip of the client you want to chat with
     * @param port the port of the client you want to chat with
     */
    public ChatSession(String ip, int port) {

        this.ip = ip;
        this.port = port;
        try {
            System.out.println("connecter");
            this.sc = new Socket(ip, port);
            System.out.println("ferdig connected");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //to make yourself connectable
    /**
     * This constructs the server socket for this client (yourself). This will enable an another client
     * to connect and create a chat-session.
     */
    public ChatSession() {

        try {
            this.ssc = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected Void call() throws Exception {
        if (sc == null)
            sc = ssc.accept();

        oos = new ObjectOutputStream(sc.getOutputStream());
        ois = new ObjectInputStream((sc.getInputStream()));
        ChatListener chatListener = new ChatListener(this);
        chatListener.start();

        return null;

    }

    /**
     * This method starts a new thread for this client and the author client where the chat
     * session will be proceeded.
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
        t.setUncaughtExceptionHandler((thr, e) -> {
            this.setException(e);
        });

    }

    /**
     * This method sends data/packet to the author client you are having a chat session
     * with, where the packet is specified in the parameter.
     *
     * @param packet the packet id the author client receives
     * @throws IOException throws NullPointerException
     */
    public void sendData(Packet packet) throws IOException {
        if (!sc.isOutputShutdown())
        {
            System.out.println("Sending data");
            oos.writeObject(packet);
            oos.flush();
        }
    }

    /**
     * This method gets the server ip from this client.
     *
     * @return the server ip of this client
     */
    public String getServerIP() {
        String s = null;
        try {
            s = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * This method gets the  port number from this client
     *
     * @return the server port from this client
     */
    public int getServerPort() {
        int i = ssc.getLocalPort();
        return i;
    }

    /**
     * This method sets the user name of who you are chatting with a specified user name.
     *
     * <p>This method is used to set the chat window to be set to this client
     * and the other client you are having a chat session with.</p>
     *
     * @param s the username of who you are chatting with
     */
    public void setChatter(String s) {
        chattingWith = s;
    }

    /** This method returns the user name of who you are chatting with.
     *
     * @return the username of who you are chatting with
     */
    public String getChattingWith() {
        return chattingWith;
    }

    public void endChat() {
        try {
            sendData(new Packet(Packet.PacketId.CHAT_STOP, null));
            sc.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This class manages the chat session between this client and the other client
     * you are chatting with.
     * <p>It starts a task loop where it reads the packet message received from the author client.
     * The chat will then be updated with the chat message received from the other client.</p>
     *
     */
    private class ChatListener extends javafx.concurrent.Service<Void> {

        private ChatSession chat;
        private ChatListener(ChatSession chatSession) {
            this.chat = chatSession;
        }

        @Override
        protected Task<Void> createTask() {
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    try {
                        System.out.println(sc.getLocalAddress().getHostAddress() + ":" + sc.getLocalPort() + " --> " + sc.getInetAddress().getHostAddress() + ":" + sc.getPort());

                        theCycle: while (true) {
                            System.out.println("reading packet");
                            Packet packet;
                            if ((packet = (Packet) ois.readObject()) != null) {
                                switch (packet.getPacketId())
                                {
                                    case CHAT_MESSAGE:
                                        chat.updateMessage(System.currentTimeMillis() + "@CHAT_MESSAGE!" + packet.getMessage());
                                        break;
                                    case CHAT_SMACK:
                                        chat.updateMessage(System.currentTimeMillis() + "@SMACK_RECEIVED!");
                                        break;
                                    case CHAT_STOP:
                                        sendData(new Packet(Packet.PacketId.CHAT_STOP_ACKNOWLEDGED, null));
                                    case CHAT_STOP_ACKNOWLEDGED:
                                        sc.close();
                                        break theCycle;
                                    default:
                                        System.err.println("Received unexpected packet type: " + packet);
                                }
                            }
                        }

                        chat.updateMessage(System.currentTimeMillis() + "@CHAT_CLOSED!");
                    }
                    catch (IOException e)
                    {
                        chat.updateMessage(System.currentTimeMillis() + "@CHAT_CLOSED!");
                    }
                    catch (Exception e)
                    {
                        chat.setException(e);
                        e.printStackTrace();
                    }

                    return null;
                } // call

            };

            return task;
        }

    } // class ChatListener

}
