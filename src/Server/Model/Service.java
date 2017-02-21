package Server.Model;

import javafx.concurrent.Task;
import Client.Model.Packet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

/** This class creates a (connection)socket and if client is logged in,
 * it will establish a connection with the client socket.
 * Then this class will start a task loop where it handles and
 * manages all the packets received from the client, therefore this class is a
 * service for the client.
 *
 * Created by Ali on 02.02.2017.
 * @version IntelliJ IDEA 2016.3.4
 */
public class Service<V> extends javafx.concurrent.Service<Void> {

    Socket socket;
    //private String clientinfo;
    Packet packet=null;
    ObjectOutputStream oot;
    ObjectInputStream oon;
    User user;

    /** This constructs a service for the client where socket
     *  and client information is specified.
     *
     * @param s the socket created for the client
     * @param info the client information
     */
    public Service(Socket s, String info) {
        this.socket = s;
        //this.clientinfo = info;
        System.out.println(">>>>>> Service created: " + s.getInetAddress().getHostAddress() + ":" + s.getPort() + " <<<<<<");
    }

    @Override
    protected Task<Void> createTask() {
        System.out.println(">>>>>> Task created! " + this + " <<<<<<");

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

        task.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == State.CANCELLED || newValue == State.FAILED || newValue == State.SUCCEEDED)
            {
                // klienten har disconnected?
                if (user != null)
                    UserManagement.setUserStatus(user, User.Status.OFFLINE);
            }
        });

        return task;
    }

    /** This method handles the packet data sent from the client and
     * sends the correct packet back to the client.
     *
     * <p>Each packet this method receives and sends has a packet id, by
     * getting the packet id to the packet sent from the client, this
     * method will use switch case to handle all the different cases
     * of packet id. Each case, based on the case it will send the
     * correct result back to the client.</p>
     *
     * @param packet the packet this method receives from the client
     * @throws IOException throws NullPointerException
     */
    public synchronized void handleData(Packet packet) throws IOException {

        System.out.println(packet.getPacketid());
        String data=packet.getMessage();
        String brukernavn="";
        switch (packet.getPacketid()){
            case LOGIN:
                String[] info=data.split("§§§¤");
                if(UserManagement.checkIfLoginCorrect(info[0],info[1]))
                {
                    brukernavn=info[0];
                    // oppdater user status
                    user = UserManagement.getUser(info[0]);
                    System.out.println(Thread.currentThread().getName() + " >>> " + user);
                    UserManagement.setUserStatus(user, User.Status.ONLINE);
                        Server.map.put(info[0],this);
                    sendData(new Packet(Packet.Packetid.LOGINOK, brukernavn));
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
                //System.out.println(users);
                sendData(new Packet(Packet.Packetid.USERLIST, users));
                System.out.println(Thread.currentThread().getName() + " >>> " + user);
                break;

            case CONNECTIONREQUEST:
                System.out.println("Conn req: " + data + " ønsker å chatte med " + "..."+data );
                System.out.println(Thread.currentThread().getName() + " >>> " + user);
                Server.map.get(data).sendData(new Packet(Packet.Packetid.INCOMINGCONNECTION, user.getUsername()));
            break;
            case CONNECTIONACCEPTED:
                String[] connectInfo = data.split(":"); // [0] = ip, [1] = port, [2] = userName
                System.out.println(Arrays.toString(connectInfo));
                System.out.println(Thread.currentThread().getName() + " >>> " + user);
                Server.map.get(connectInfo[2]).sendData(new Packet(Packet.Packetid.CONNECTIONINFORMATION,
                                                        connectInfo[0] + ":" + connectInfo[1]+":"+user.getUsername()));
                break;
            case CHANGEBUSY:
                UserManagement.setUserStatus(user,User.Status.BUSY);
                break;
                case CHANGEONLINE:
                    UserManagement.setUserStatus(user,User.Status.ONLINE);
                    break;
            case BADREQUEST:
        }

    }

    /** This method sends data/packet to the client from server where the packet
     * is specified in the parameter.
     *
     * @param packet the packet id the client receives
     * @throws IOException throws NullPointerException
     */
    public void sendData(Packet packet) throws IOException {

        System.out.println("Data/pakke bir sendt til client");
        oot.writeObject(packet);
        oot.flush();

    }
}
