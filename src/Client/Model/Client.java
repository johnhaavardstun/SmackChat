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

/**
 * Created by Ali on 30.01.2017.
 */
public class Client extends Task<Void> {

    ServerSocket sc;
    Socket s;

    ObjectOutputStream oos;
    Packet p=null;

    private final int SERVERTPORT=8000;
    private final String SERVERIP="127.0.0.1";




    public void  sendData(Packet packet) throws IOException {
        System.out.println("data bir sendt");
        oos.writeObject(packet);
        oos.flush();
    }

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
                            sendData(new Packet(Packet.Packetid.USERLISTREQUEST,"Moren din"));
                        } catch (IOException e) {


                        }
                    }
                }, 1000, 5000);
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
                //System.out.println("fikk user liste: " + packet.getMessage());
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


    public void start()
    {
        Thread t= new Thread(this);
        t.start();
        t.setUncaughtExceptionHandler((thr, e) -> {
            this.setException(e);
        });

    }

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
//                        e.printStackTrace();
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



