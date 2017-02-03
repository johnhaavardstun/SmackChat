package Client.Model;

import Client.Controller.MainController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ali on 30.01.2017.
 */
public class Client extends Task<Void> {

    ServerSocket sc;
      static Socket s;

    static ObjectOutputStream oos;
     Packet p=null;

    private final static int SERVERTPORT=8000;
    private final static String SERVERIP="127.0.0.1";




    public  static void  sendData(Packet packet) throws IOException {

        System.out.println("data bir sendt");
        oos.writeObject(packet);
        oos.flush();
    }

public static void handleData(Packet packet)
{

    switch (packet.getPacketid()){
        case LOGIN:
            System.out.print("feil");
            break;
        case REGISTER:
            System.out.print("feil");
            break;
        case WRONGLOGIN:
            System.out.print("Dette er feil ifno");
        case BADREQUEST:
                System.out.print("feil");
            break;
    }

}


    @Override
    protected Void call() throws Exception {
        System.out.println("Skjer dette:");
        s = new Socket(SERVERIP, SERVERTPORT);
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();

        readinfo rd= new readinfo(s);
        rd.start();

        System.out.println("Skjer det over");
return null;
    }


    public void start()
    {
        Thread t= new Thread(this);
        t.start();

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

                            System.out.println("leser objekt");

                            if((packet=(Packet)oin.readObject())!=null)
                            {
                                System.out.println(packet.getMessage()+"   "+packet.getPacketid());
                                handleData(packet);
                            }



                            System.out.println("Har lest objekt");


                        }

                    } catch (IOException e) {
                        e.printStackTrace();
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

