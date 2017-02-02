package Client.Controller;


import Client.Model.Client;
import Client.Model.Packet;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ConnectException;

public class MainController {

    @FXML TextField user1;
    @FXML PasswordField pass1;

    @FXML Button butteng;
    @FXML Button Register;

   String  superdupercode="§§§¤";







    public  static void showMessageToClient(AlertType type,String title, String text)
    {
        Alert alert= new Alert(type);
        alert.setTitle(title);
        alert.setContentText(text);

        alert.showAndWait();

    }

    public void login()  {

        String sd=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
        System.out.println(sd);

        Packet pa= new Packet(Packet.Packetid.LOGIN,sd);

        System.out.println(pa.getMessage() +"       "+pa.getPacketid());

        try {
            Client c= new Client();

            System.out.println("connecter til server");
            c.startClient();

            System.out.println("ferdig connected");

            String s=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
            System.out.println("lag pakket");

            Packet p= new Packet(Packet.Packetid.LOGIN,s);
            c.sendData(p);


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR,"Connection Refused","Server is down please try again later!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            showMessageToClient(AlertType.ERROR, "Packet class is missing", "please contact the administrator");
        }
        }






    public void register()
    {

        try {
            Client c= new Client();
            c.startClient();

            String s=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
            System.out.println(s);
            Packet p= new Packet(Packet.Packetid.REGISTER,s);
            c.sendData(p);


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR,"Input exception","Please contact the administrator");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            showMessageToClient(AlertType.ERROR,"Packet class is missing","please contact the administrator");
        }

    }


    public String getTextFieldData(TextField tx)
    {
        String s=tx.getText();
        return s;

    }
}
