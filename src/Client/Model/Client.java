package Client.Model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class creates a client socket and if client is successfully logged into SmackChat,
 * it will establish a connection with the server.
 * Then this class will start a task loop where it handles and
 * manages all the packets received from the server.
 *
 */
public class Client extends Task<Void> {

    private ServerSocket sc;
    private Socket s;

    private ObjectOutputStream oos;
    private Packet p=null;
    private String clientUser;

    private final int SERVER_PORT = 8000;
//    private final String SERVERIP = "127.0.0.1";
    private String serverIP;
    Timer timer;

    public Client(String serverIP)
    {
        this.serverIP = serverIP;
    }

    /**
     * This method sends data/packet to the server from client where the packet
     * is specified in the parameter.
     *
     * @param packet the packet id the server receives
     * @throws IOException if an errors occurs during the input or output.
     */
    public void sendData(Packet packet) throws IOException {
        oos.writeObject(packet);
        oos.flush();
    }

    /**
     * This method handles the packet data sent from the server and
     * sends the correct packet back to the server.
     *
     * <p>Each packet this method receives and sends has a packet id, by
     * getting the packet id to the packet sent from the server, this
     * method will use switch case to handle all the different cases
     * of packet id. Each case, based on the case it will send the
     * correct result back to the server.</p>
     *
     * @param packet the packet this method receives from the server
     * @throws IOException if an errors occurs during the input or output.
     */
    public void handleData(Packet packet)
    {
        switch (packet.getPacketId()) {
            case USERNAME_TAKEN:
                this.updateMessage(System.currentTimeMillis() + "@USERNAME_TAKEN!");
                break;
            case LOGIN_OK:
                timer= new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            sendData(new Packet(Packet.PacketId.USER_LIST_REQUEST,"ListRequest"));
                        } catch (IOException e) {


                        }
                    }
                }, 1000, 5000);
                clientUser = packet.getMessage();
                this.updateMessage(System.currentTimeMillis() + "@LOGIN_OK!");
                break;
            case REGISTER_OK:
                this.updateMessage(System.currentTimeMillis() + "@REGISTER_OK!");
                break;
            case WRONG_LOGIN:
                this.updateMessage(System.currentTimeMillis() + "@WRONG_LOGIN!");
                break;
            case ALREADY_LOGGED_IN:
                this.updateMessage(System.currentTimeMillis() + "@ALREADY_LOGGED_IN!");
                break;
            case BAD_REQUEST:
                this.updateMessage(System.currentTimeMillis() + "@BAD_REQUEST!");
                System.out.print("feil");
                break;
            case USER_LIST:
                this.updateMessage(System.currentTimeMillis() + "@USER_LIST!" + packet.getMessage());
                break;
            case CHAT_CONNECTION_REQUEST_CLIENT:
                this.updateMessage(System.currentTimeMillis() +"@CHAT!"+packet.getMessage());
                break;
            case CHAT_CONNECTION_INFORMATION:
                this.updateMessage(System.currentTimeMillis() +"@CHAT_CONNECTION_INFORMATION!" + packet.getMessage());
                break;
            default:
                this.updateMessage(System.currentTimeMillis() + "@BAD_REQUEST!");
        }

    }

    /**
     * This method returns the user of this client.
     *
     * @return the user of this client
     */
    public String getUser()
{
    return  clientUser;
}

    @Override
    protected Void call() throws Exception {
        s = new Socket(serverIP, SERVER_PORT);
        System.out.println("connected to server @ " + s.getInetAddress().getHostAddress() + ":" + s.getPort());
        if (!s.isConnected())
            throw new IOException("Could not connect to server!");
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();

        ClientListener cl = new ClientListener(s);
        cl.start();

        return null;
    }

    /**
     * This method starts a new thread for this client, which is used to send requests to the server and handle the response.
     */
    public void start()
    {
        Thread t = new Thread(this);
        t.start();
        t.setUncaughtExceptionHandler((thr, e) -> {
            this.setException(e);
        });

    }

    /**
     * This method closes down the connection to the server.
     */
    public void stop()
    {
        try {
            timer.cancel();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When established connection with the server. This class is used to read
     * the packets received from the server. It will start a task loop and read
     * the ObjectInputStream received from the socket and handle the data/packet.
     * @see ObjectInputStream
     *
     */
    private class ClientListener extends Service<Void>
    {
        Socket socket;
        Packet packet=null;

        private ClientListener(Socket socket)
        {
            this.socket=socket;
        }

        @Override
        protected Task<Void> createTask() {
            Task<Void> task= new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    try
                    {
                       ObjectInputStream oin= new ObjectInputStream(socket.getInputStream());

                        while (true)
                        {
                            if((packet=(Packet)oin.readObject())!=null)
                            {
                                handleData(packet);
                            }
                        }

                    }
                    catch (IOException e)
                    {
                        this.updateMessage(System.currentTimeMillis() + "@SERVER_LOST!");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    return null;

                }

            };

            return task;
        }
    }



}



