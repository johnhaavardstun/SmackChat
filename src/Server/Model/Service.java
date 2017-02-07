package Server.Model;

import javafx.concurrent.Task;
import Client.Model.Packet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

                System.out.println("Lager Task");

                oot = new ObjectOutputStream(socket.getOutputStream());
                oot.flush();
                oon = new ObjectInputStream(socket.getInputStream());

                System.out.println("Starter Task loop");

                while ((true)) {

                    System.out.println("leser objekt/packet");

                    if((packet=(Packet)oon.readObject())!=null)
                    {
                        System.out.println(packet.getMessage()+"   "+packet.getPacketid());
                        handleData(packet);
                    }

                    System.out.println("Har lest objekt/packet");

                }

            } catch (IOException e) {
                System.out.println("Connection lost - bye!");
                //e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

            }

        };

        return task;


        }


    public  void handleData(Packet packet) throws IOException {

        System.out.println(packet.getPacketid());
        String data=packet.getMessage();

        switch (packet.getPacketid()){
            case LOGIN:
                String[] info=data.split("§§§¤");
                if(UserManagement.checkIfLoginCorrect(info[0],info[1]))
                {
                    sendData(new Packet(Packet.Packetid.LOGINOK, "Congrats!"));
                    System.out.println("Log in: OK");
                }
                else
                {
                    sendData( new Packet(Packet.Packetid.WRONGLOGIN,"Pakke Mottatt"));
                    System.out.println("Auth fail - possible break-in attemp?!?!?!?!?!?!11");
                    System.out.println(info[0] + ":" + info[1]);
                }

                break;
            case REGISTER:
                info=data.split("§§§¤");

                try {
                    if(UserManagement.userExistTest(info[0]))
                    {
                        UserManagement.addUserToFile(info[0],info[1]);
                        System.out.println("Bruker er registret");
                        sendData(new Packet(Packet.Packetid.REGISTEROK, "Welcome!"));
                    }
                    else
                    {
                        sendData(new Packet(Packet.Packetid.USERNAMETAKEN, "Have some originality"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case USERLISTREQUEST:
                String users = UserManagement.getUserStatusList();
                System.out.println(users);
                sendData(new Packet(Packet.Packetid.USERLIST, users));

                break;

            case BADREQUEST:
        }

    }


    public void sendData(Packet packet) throws IOException {

        System.out.println("Data/pakke bir sendt til client");
        oot.writeObject(packet);
        oot.flush();
    }
}
