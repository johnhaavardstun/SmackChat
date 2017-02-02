package Client.Model;

import Client.Controller.MainController;
import com.sun.corba.se.impl.io.InputStreamHook;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ali on 30.01.2017.
 */
public class Client {

    ServerSocket sc;
      Socket s;

    ObjectOutputStream oos;
     ObjectInputStream ois;

    private final static int SERVERTPORT=5000;
    private final static String SERVERIP="127.0.0.1";


    public  void  startClient() throws IOException, ClassNotFoundException {
        System.out.println("Skjer dette:");
        s= new Socket(SERVERIP,SERVERTPORT);
        oos= new ObjectOutputStream(s.getOutputStream());
        ois= new ObjectInputStream(s.getInputStream());
        System.out.println("Skjer det over");


        while(true)
        {
          Packet read=(Packet)ois.readObject();
            if(read!=null)
            {
             handleData(read);
            }
        }


    }

    public  void sendData(Packet packet) throws IOException {

        System.out.println("data bir sendt");

        oos.writeObject(packet);
    }

public static void handleData(Packet packet)
{

    switch (packet.getPacketid()){
        case LOGIN:
            MainController.showMessageToClient(Alert.AlertType.ERROR,"Bad Request","Bad request from server");
            break;
        case REGISTER:
            MainController.showMessageToClient(Alert.AlertType.ERROR,"Bad Request","Bad request from server");
            break;
        case WRONGLOGIN:
            MainController.showMessageToClient(Alert.AlertType.ERROR,"Wrong username or password","You have entered the wrong username or passowrd");

        case BADREQUEST:
            MainController.showMessageToClient(Alert.AlertType.ERROR,"Bad Request","Bad Reuest from Server");
    }

}


}
