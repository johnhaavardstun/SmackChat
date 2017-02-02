package Server.Model;

import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Ali on 02.02.2017.
 */
public class Service extends javafx.concurrent.Service<Void> {

    Socket s;
    private String clientinfo;

    public Service(Socket s, String info) {
        this.s = s;
        this.clientinfo = info;

    }

    @Override
    protected Task<Void> createTask() {

        Task<Void> task= new Task<Void>() {


            @Override
            protected Void call() throws Exception {

                Packet o;

                try {

                    System.out.println("Lager info");

                    ObjectOutputStream oot = new ObjectOutputStream(s.getOutputStream());
                    ObjectInputStream oon = new ObjectInputStream(s.getInputStream());

                    while ((true)){

                        System.out.println("Leser objekt...");

                       // handleData(o);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } /*catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }*/

                return  null;

            }

        };
        return task;


        }

    public  void handleData(Packet packet)
    {

        System.out.println(packet.getPacketid());
        switch (packet.getPacketid()){
            case LOGIN:

                System.out.println("bankainanana");

                break;
            case REGISTER:
                break;

            case BADREQUEST:
        }

    }
}
