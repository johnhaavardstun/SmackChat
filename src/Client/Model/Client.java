package Client.Model;

import Client.Controller.MainController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import javax.rmi.CORBA.Util;
import javax.tools.JavaCompiler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/** This class creates a client socket and if client is successfully logged into SmackChat,
 * it will establish a connection with the server.
 * Then this class will start a task loop where it handles and
 * manages all the packets received from the server.
 * When this client has established a connection to a author client in SmackChat, it will start a new thread,
 * where both clients can chat to each other in SmackChat.
 *
 * Created by Ali on 30.01.2017.
 * @version IntelliJ IDEA 2016.3.4
 */
public class Client extends Task<Void> {

    private ServerSocket sc;
    private Socket s;

    private ObjectOutputStream oos;
    private Packet p=null;
    private String clientUser;

    private final int SERVERTPORT=8000;
    private final String SERVERIP="127.0.0.1";


    /**
     * This method sends data/packet to the server from client where the packet
     * is specified in the parameter.
     *
     * @param packet the packet id the server receives
     * @throws IOException throws NullPointerException
     */
    public void  sendData(Packet packet) throws IOException {
        System.out.println("data bir sendt");
        oos.writeObject(packet);
        oos.flush();
    }

    /**This method handles the packet data sent from the server and
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
        switch (packet.getPacketid()) {
            case USERNAMETAKEN:
                this.updateMessage(System.currentTimeMillis() + "@USERNAMETAKEN!");
                System.out.println("opptatt brukernavn");
                break;
            case LOGINOK:
                Timer timer= new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            sendData(new Packet(Packet.Packetid.USERLISTREQUEST,"ListRequest"));
                        } catch (IOException e) {


                        }
                    }
                }, 1000, 5000);
                    clientUser=packet.getMessage();
                this.updateMessage(System.currentTimeMillis() + "@LOGINOK!");
                System.out.println("Login - yay!");
                break;
            case REGISTEROK:
                this.updateMessage(System.currentTimeMillis() + "@REGISTEROK!");
                System.out.println("Suksessfull registrering");
                break;
            case WRONGLOGIN:
                this.updateMessage(System.currentTimeMillis() + "@WRONGLOGIN!");
                System.out.print("Dette er feil ifno");
                break;
            case BADREQUEST:
                this.updateMessage(System.currentTimeMillis() + "@BADREQUEST!");
                System.out.print("feil");
                break;
            case USERLIST:
                this.updateMessage(System.currentTimeMillis() + "@USERLIST!" + packet.getMessage());
                break;
            case INCOMINGCONNECTION:
                this.updateMessage(System.currentTimeMillis() +"@CHAT!"+packet.getMessage());
                System.out.print("Mottok en request.");
                break;
            case CONNECTIONINFORMATION:
                this.updateMessage(System.currentTimeMillis() +"@CONNECTIONINFORMATION!" + packet.getMessage());
                System.out.println("Mottok kontakt informasjon");
                break;
            default:
                this.updateMessage(System.currentTimeMillis() + "@BADREQUEST!");
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
        s = new Socket(SERVERIP, SERVERTPORT);
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
        Thread t= new Thread(this);
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
                             //   System.out.println(packet.getMessage()+"   "+packet.getPacketid());
                                handleData(packet);
                            }



                           // System.out.println("Objektet er ferdig lest");


                        }

                    } catch (IOException e) {
                        this.updateMessage("SERVERLOST");
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



