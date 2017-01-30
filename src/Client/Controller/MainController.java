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

public class MainController {

    @FXML TextField user1;
    @FXML PasswordField pass1;

    @FXML Button butteng;
    @FXML Button Register;

   String  superdupercode="§§§¤";




    public void noServerConnection()
    {

        Alert alert= new Alert(AlertType.ERROR);

        alert.setTitle("SmackChat Could not connect to server");
        alert.setContentText("Could Not connect to the server, please  check your internett connection or firewall settings!");

        alert.showAndWait();

    }


    public  static void showMessageToClient(AlertType type,String title, String text)
    {
        Alert alert= new Alert(type);
        alert.setTitle(title);
        alert.setContentText(text);

        alert.showAndWait();

    }

    public void login()  {

        try {
            Client.startClient();
            String s=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
            Packet p= new Packet(Packet.Packetid.LOGIN,s);
            Client.sendData(p);


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR,"Input exception","Please contact the administrator");
        } catch (ClassNotFoundException e) {
            showMessageToClient(AlertType.ERROR,"Packet class is missing","please contact the administrator");
        }

    }

    public void register()
    {

        try {
            Client.startClient();
            String s=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
            System.out.println(s);
            Packet p= new Packet(Packet.Packetid.LOGIN,s);
            Client.sendData(p);


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR,"Input exception","Please contact the administrator");
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
