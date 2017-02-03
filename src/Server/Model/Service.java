package Server.Model;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import javafx.concurrent.Task;
import Client.Model.Packet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Ali on 02.02.2017.
 */
public class Service extends javafx.concurrent.Service<Void> {

    Socket socket;
    private String clientinfo;
    Packet packet=null;
    ObjectOutputStream oot;
    ObjectInputStream oon;

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

                    oot = new ObjectOutputStream(socket.getOutputStream());
                    oot.flush();
                    oon = new ObjectInputStream(socket.getInputStream());

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
        String data=packet.getMessage();
        String[] info=data.split("§§§¤");

        switch (packet.getPacketid()){
            case LOGIN:
                if(userMangement.checkIfLoginCorrect(info[0],info[1]))
                System.out.println("ok");

                break;
            case REGISTER:

                try {
                    if(userMangement.userExistTest(info[0]))
                    {
                        userMangement.addUserToFile(info[0],info[1]);
                        System.out.println("Bruker er registret");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case BADREQUEST:
        }

    }


    public   void  sendData(Packet packet) throws IOException {

        System.out.println("data bir sendt");
        oot.writeObject(packet);
        oot.flush();
    }
}
