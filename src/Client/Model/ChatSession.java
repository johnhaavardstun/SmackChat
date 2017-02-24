package Client.Model;

import Server.Model.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class establishes a peer-to-peer connection between two clients. If connection is established, this class
 * manages the chat session between the two clients. Once the two clients are connected to each other, a new thread
 * will start, where the two clients can send each other messages through packets.
 * Created by Ali on 13.02.2017.
 */
public class ChatSession extends Task<Void> {

    Socket sc;
    String ip;
    int port;
    ServerSocket ssc;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String chattingWith;

    //To contact
    /**
     * This constructs a chat session between two clients where ip and port is specified.
     * It constructs a new socket and this socket is the socket where the two clients can
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
     * This constructs the server socket for this client (yourself). This will enable the author client
     * to establish a connection with yourself.
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

        System.out.println("Doing stuff");
        oos = new ObjectOutputStream(sc.getOutputStream());
        ois = new ObjectInputStream((sc.getInputStream()));
        messageIn min = new messageIn(this);
        min.start();
        System.out.println("Done!");

        return null;

    }

    /**This method starts a new thread for this client and the author client where the chat
     * session will be proceeded.
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
        t.setUncaughtExceptionHandler((thr, e) -> {
            this.setException(e);
        });

    }

    /** This method sends data/packet to the author client you are having a chat session
     * with, where the packet is specified in the parameter.
     *
     * @param packet the packet id the author client receives
     * @throws IOException throws NullPointerException
     */
    public void sendData(Packet packet) throws IOException {
        System.out.println("Sending data");
        oos.writeObject(packet);
        oos.flush();
    }

    /** This method gets the server ip from this client.
     *
     * @return the server ip of this client
     */
    public String getServerIP() {
        String s = null;
        s = ssc.getInetAddress().getHostAddress();
        return s;
    }

    /** This method gets the server port number from this client
     *
     * @return the server port from this client
     */
    public int getServerPort() {
        int i = ssc.getLocalPort();
        return i;
    }

    /** This method sets the user name of who you are chatting with a specified user name.
     *
     * <p>This method is used to set the chat window to be set to this client
     * and the author client you are having a chat session with.</p>
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

    /** This class manages the chat session between this client and the author client
     * you are chatting with.
     * <p>It starts a task loop where it reads the packet message received from the author client.
     * The chat will then be updated with the chat message received from the author client.</p>
     *
     */
    private class messageIn extends javafx.concurrent.Service<Void> {
        ChatSession chat;


        private messageIn(ChatSession chatSession) {
            this.chat = chatSession;
        }

        @Override
        protected Task<Void> createTask() {
            Task<Void> task = new Task<Void>() {


                @Override
                protected Void call() throws Exception {


                    try {
                        System.out.println(sc.getLocalAddress() + ":" + sc.getLocalPort() + " --> " + sc.getInetAddress().getHostAddress() + ":" + sc.getPort());


                        while ((true)) {
                            Packet packet;
                            if ((packet = (Packet) ois.readObject()) != null) {
                                //   System.out.println(packet.getMessage()+"   "+packet.getPacketid());
                                System.out.println("Recived: " + packet.getPacketid() + ":" + packet.getMessage());
                                chat.updateMessage(System.currentTimeMillis() + "@CHATMESSAGE!" + packet.getMessage());

                            }
                        }

                    } catch (IOException e) {
                        chat.updateMessage(System.currentTimeMillis() + "@CHATCLOSED!");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;

                }

            };


            return task;
        }
    }


}





