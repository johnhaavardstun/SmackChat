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
 * When this client has established a connection to a author client in SmackChat, it will start a new thread,
 * where both clients can chat to each other in SmackChat.
 *
 */
public class Client extends Task<Void> {

    private ServerSocket sc;
    private Socket s;

    private ObjectOutputStream oos;
    private Packet p=null;
    private String clientUser;

    private final int SERVERTPORT=8000;
    private final String SERVERIP="127.0.0.1";
    private String serverIP;

    public Client(String serverIP)
    {
        this.serverIP = serverIP;
    }

    /**
     * This method sends data/packet to the server from client where the packet
     * is specified in the parameter.
     *
     * @param packet the packet id the server receives
     * @throws IOException throws NullPointerException
     */
    public void sendData(Packet packet) throws IOException {
//        System.out.println("data bir sendt");
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
     * @throws IOException throws NullPointerException
     */
    public void handleData(Packet packet)
    {
        switch (packet.getPacketId()) {
            case USERNAME_TAKEN:
                this.updateMessage(System.currentTimeMillis() + "@USERNAME_TAKEN!");
                System.out.println("opptatt brukernavn");
                break;
            case LOGIN_OK:
                Timer timer= new Timer();
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
                System.out.println("Login - yay!");
                break;
            case REGISTER_OK:
                this.updateMessage(System.currentTimeMillis() + "@REGISTER_OK!");
                System.out.println("Suksessfull registrering");
                break;
            case WRONG_LOGIN:
                this.updateMessage(System.currentTimeMillis() + "@WRONG_LOGIN!");
                System.out.print("Dette er feil ifno");
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
                System.out.println("Mottok en request.");
                break;
            case CHAT_CONNECTION_INFORMATION:
                this.updateMessage(System.currentTimeMillis() +"@CHAT_CONNECTION_INFORMATION!" + packet.getMessage());
                System.out.println("Mottok kontakt informasjon");
                break;
            default:
                this.updateMessage(System.currentTimeMillis() + "@BAD_REQUEST!");
                System.out.println("ukjent pakke");
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
        System.out.println("Metode Call(): lager socket");
        s = new Socket(serverIP, SERVERTPORT);
        System.out.println("connected to server @ " + s.getInetAddress().getHostAddress() + ":" + s.getPort());
        if (!s.isConnected())
            throw new IOException("Could not connect to server!");
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();

        readinfo rd= new readinfo(s);
        rd.start();



        System.out.println("readinfo (tastatur) startet");
        return null;
    }

    /**
     * This method starts a new thread for this client and a other different client
     * which has accepted the connection chat request from this client. In this thread
     * both clients can exchange messages to eachother as long as their both online,
     * otherwise the thread will close.
     */
    public void start()
    {
        System.out.println("|>|>|> Client created! <|<|<|");
        Thread t = new Thread(this);
        t.start();
        System.out.println("Client thread: " + t.getName());
        t.setUncaughtExceptionHandler((thr, e) -> {
            this.setException(e);
        });

    }

    /**
     * When established connection with the server. This class is used to read
     * the packets received from the server. It will start a task loop and read
     * the ObjectInputStream received from the socket and handle the data/packet.
     */
    private class readinfo extends Service<Void>
    {
        Socket socket;
        Packet packet=null;

        private readinfo(Socket socket)
        {
            this.socket=socket;
        }

        @Override
        protected Task<Void> createTask() {
            Task<Void> task= new Task<Void>() {


                @Override
                protected Void call() throws Exception {


                    try {

                       ObjectInputStream oin= new ObjectInputStream(socket.getInputStream());

                        while ((true)) {

                    //   System.out.println("leser Objekt input stream");

                            if((packet=(Packet)oin.readObject())!=null)
                            {
                             //   System.out.println(packet.getMessage()+"   "+packet.getPacketId());
                                handleData(packet);
                            }



                           // System.out.println("Objektet er ferdig lest");


                        }

                    } catch (IOException e) {
                        this.updateMessage(System.currentTimeMillis() + "@SERVER_LOST!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return  null;

                }

            };



            return task;
        }
    }



}



