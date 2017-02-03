package Server.Model;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import javafx.concurrent.Task;
import Client.Model.Packet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.*;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Ali on 02.02.2017.
 */
public class Service extends javafx.concurrent.Service<Void> {

    Socket socket;
    private String clientinfo;
    Packet packet=null;

    public Service(Socket s, String info) {
        this.socket = s;
        this.clientinfo = info;

    }

    @Override
    protected Task<Void> createTask() {

        Task<Void> task= new Task<Void>() {


            @Override
            protected Void call() throws Exception {


                try {

                    System.out.println("Lager info");

                    ObjectOutputStream oot = new ObjectOutputStream(socket.getOutputStream());
                    oot.flush();
                    ObjectInputStream oon = new ObjectInputStream(socket.getInputStream());

                    System.out.println("Starter loop");

                    while ((true)) {

                        System.out.println("leser objekt");

                        if((packet=(Packet)oon.readObject())!=null)
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
