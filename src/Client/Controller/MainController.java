package Client.Controller;


import Client.Model.Client;
import Client.Model.Packet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML TextField user1;
    @FXML PasswordField pass1;

    @FXML Button butteng;
    @FXML Button Register;

    String  superdupercode="§§§¤";
    Client c;

    public void initialize()
    {
        c = new Client();
        c.start();

        c.messageProperty().addListener(((observable, oldValue, newValue) -> {
            onServerMessage(newValue.substring(1 + newValue.indexOf('@')));
        }));

        c.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            showMessageToClient(AlertType.ERROR, "Server error!", newValue.getMessage());
            System.exit(876);
        });
    }


    private void onServerMessage(String message) {
        switch (message) {
            case "USERNAMETAKEN":
                showMessageToClient(AlertType.ERROR, "Username taken!",
                        "Please choose another username.");
                break;
            case "LOGINOK":
                System.out.println("login ok!");
                switchToLoginScene();
                break;
            case "REGISTEROK":
                showMessageToClient(AlertType.CONFIRMATION, "Successfully registered!",
                        "Welcome to SmackChat - enjoy your stay!");
                break;
            case "WRONGLOGIN":
                showMessageToClient(AlertType.ERROR, "Login failed!",
                        "The username and/or password is incorrect, scrub!");
                break;
            case "SERVERLOST":
                showMessageToClient(AlertType.ERROR, "Server lost!",
                        "The connection to the server was lost, please try again later.");
            case "BADREQUEST":
                System.out.println("skjedde en feil");

        }
    }

    private void switchToLoginScene() {

        URL chatload= getClass().getResource("../View/startToChat.fxml");
        Parent root = null;
        try {
            root= FXMLLoader.load(chatload);
        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR, "Srz errror", "PANIC");
            System.exit(9876543);
        }

        ((Stage) butteng.getScene().getWindow()).setScene(new Scene(root, 600, 400));
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



            System.out.println("lag pakket");

            String sd=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
            System.out.println(sd);

            Packet pa= new Packet(Packet.Packetid.LOGIN,sd);

            System.out.println(pa.getMessage() +"       "+pa.getPacketid());
            c.sendData(pa);


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR,"Connection Refused","Server is down please try again later!");
            e.printStackTrace();
        }
        }






    public void register() throws IOException {



         try {

            String s=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
            System.out.println(s);
            Packet p= new Packet(Packet.Packetid.REGISTER,s);
            c.sendData(p);


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR,"Input exception","Please contact the administrator");
            e.printStackTrace();
        }

    }


    public String getTextFieldData(TextField tx)
    {
        String s=tx.getText();
        return s;

    }
}
