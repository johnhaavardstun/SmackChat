package Client.Controller;


import Client.Model.Client;
import Client.Model.Packet;
import Client.Model.UserStatus;
import Server.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import sun.plugin.javascript.navig.Anchor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainController {

    @FXML TextField user1;
    @FXML PasswordField pass1;

    @FXML Button butteng;
    @FXML Button Register;

    @FXML ListView<UserStatus> userList;

    String  superdupercode="§§§¤";
    Client c;

    public void initialize()
    {
        c = new Client();
        c.start();

        c.messageProperty().addListener(((observable, oldValue, newValue) -> {
            onServerMessage(newValue.substring(1 + newValue.indexOf('@'), newValue.indexOf('!')),
                            newValue.substring(1 + newValue.indexOf('!')));
        }));

        c.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            showMessageToClient(AlertType.ERROR, "Server error!", newValue.getMessage());
            System.exit(876);
        });
    }


    private void onServerMessage(String message, String data) {
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
                break;
            case "USERLIST":
                updateUserList(data);
//                System.out.println(data);
                break;
            case "BADREQUEST":
                System.out.println("skjedde en feil");

        }
    }

    private void updateUserList(String data) {
        String[] users = data.split("\n");
        ObservableList<UserStatus> newList = FXCollections.observableList(new ArrayList<>());
        for (String user: users)
        {
            int statusid = user.charAt(0);
            UserStatus.Status status = statusid == 1 ? UserStatus.Status.ONLINE :
                    (statusid == 2 ? UserStatus.Status.BUSY : UserStatus.Status.OFFLINE);
            newList.add(new UserStatus(user.substring(1), status));
        }
        userList.setItems(newList);
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

        if (userList == null)
            userList = (ListView) root.lookup("#userList");

        userList.setCellFactory(new Callback<ListView<UserStatus>, ListCell<UserStatus>>() {

            @Override
            public ListCell<UserStatus> call(ListView<UserStatus> param) {
                ListCell<UserStatus> cell = new ListCell<UserStatus>() {

                    @Override
                    protected void updateItem(UserStatus userStatus,  boolean empty) {
                        super.updateItem(userStatus, empty);
                        if (userStatus != null) {
                            setText(userStatus.getUserName());
                            String status = userStatus.getStatus().toString().toLowerCase();

                            Image image= new Image(getClass().getResourceAsStream("../View/"+status+".png"));
                            ImageView iw= new ImageView();
                            iw.setImage(image);

                            setGraphic(iw);
                                    // "file:./View/" + "online" + ".png"

                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };

                return cell;
            }
        });
// TODO: LOTS A STUFF
//        ((AnchorPane) userList.getScene().lookup("#pain")).getChildren().add(new ImageView("file:./View/" + "online" + ".png"));
        ((ImageView) userList.getScene().lookup("#imgview")).setImage(new Image("file:./View/" + "online" + ".png"));

        try {
            c.sendData(new Packet(Packet.Packetid.USERLISTREQUEST, null));
        } catch (IOException e) {
            e.printStackTrace();
//            System.exit(9876);
        }
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
