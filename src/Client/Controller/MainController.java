package Client.Controller;


import Client.Model.ChatSession;
import Client.Model.Client;
import Client.Model.Packet;
import Client.Model.UserStatus;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class MainController {

    @FXML TextField user1;
    @FXML PasswordField pass1;

    @FXML Button butteng;
    @FXML Button Register;
    @FXML Button connect;
    @FXML ListView<UserStatus> userList;
    @FXML TextArea chatbox;
    @FXML TextField textField;

    String  superdupercode="§§§¤";
    Client c;
    ChatSession cSession;

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
            case "CHAT":
                System.out.print("Mottok en chat reuqest");
                chatConfirmation(data);
                break;
            case "CHATMESSAGE":
               System.out.println("Har motatt chat melding!");
                break;
            case "BADREQUEST":
                System.out.println("skjedde en feil");
                break;
            case "CONNECTIONINFORMATION":
                String[] cinfo = data.split(":");
                System.out.println(Arrays.toString(cinfo));
                startChatConnect(cinfo[0], Integer.parseInt(cinfo[1]));
                System.out.println("Mottok kontakt informasjon!");
    break;
            case "MESSAGE":

                chatbox.appendText("Chatter med:"+data+'\n');
                break;

        }
    }

    private void updateUserList(String data) {
        String[] users = data.split("\n");
        ObservableList<UserStatus> newList = FXCollections.observableList(new ArrayList<>());
        for (String user: users)
        {
            char statusid = user.charAt(0);
            UserStatus.Status status = statusid == '1' ? UserStatus.Status.ONLINE :
                    (statusid == '2' ? UserStatus.Status.BUSY : UserStatus.Status.OFFLINE);
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

            final Image onlineImage = new Image(getClass().getResourceAsStream("../View/online.png"));
            final Image offlineImage = new Image(getClass().getResourceAsStream("../View/offline.png"));
            final Image busyImage = new Image(getClass().getResourceAsStream("../View/busy.png"));

            @Override
            public ListCell<UserStatus> call(ListView<UserStatus> param)
            {
                ListCell<UserStatus> cell = new ListCell<UserStatus>()
                {

                    final ImageView statusIcon;

                    {
                        statusIcon = new ImageView();
                    }

                    @Override
                    protected void updateItem(UserStatus userStatus,  boolean empty)
                    {
                        super.updateItem(userStatus, empty);
                        if (userStatus != null)
                        {
                            switch (userStatus.getStatus())
                            {
                                case ONLINE: statusIcon.setImage(onlineImage); break;
                                case OFFLINE: statusIcon.setImage(offlineImage); break;
                                case BUSY: statusIcon.setImage(busyImage); break;
                                default: statusIcon.setImage(null);
                            }

                            setText(userStatus.getUserName());
                            setGraphic(statusIcon);
                        }
                        else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };

                return cell;
            }
        });

        try
        {
            c.sendData(new Packet(Packet.Packetid.USERLISTREQUEST, null));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  static Optional<ButtonType> showMessageToClient(AlertType type,String title, String text)
    {
        Alert alert= new Alert(type);
        alert.setTitle(title);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return result;

    }

    public void login()  {



        try {



            System.out.println("lag pakket");

            String sd=getTextFieldData(user1)+superdupercode+getTextFieldData(pass1);
            System.out.println(sd);

            Packet pa= new Packet(Packet.Packetid.LOGIN,sd);

            //System.out.println(pa.getMessage() +"       "+pa.getPacketid());
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

    public void connectionRequest()
    {
        UserStatus statusU = userList.getSelectionModel().getSelectedItem();
        if(statusU != null && statusU.getStatus() == UserStatus.Status.ONLINE) {

            String s = statusU.getUserName();
            System.out.println(s);
                try {
                    c.sendData(new Packet(Packet.Packetid.CONNECTIONREQUEST, s));
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    public void chatConfirmation(String userName)
    {
        Optional<ButtonType> result = showMessageToClient(AlertType.CONFIRMATION,
                "Accept?","Chat request from " + userName + "!");

        if(result.get() == ButtonType.OK)
        {
            try {

                cSession = new ChatSession();
                cSession.start();
                cSession.messageProperty().addListener(((observable, oldValue, newValue) -> {
                    onServerMessage(newValue.substring(1 + newValue.indexOf('@'), newValue.indexOf('!')),
                            newValue.substring(1 + newValue.indexOf('!')));
                }));
                String connectionInfo = "127.0.0.1" + ":" + cSession.getServerPort() + ":" + userName;
                System.out.println(connectionInfo);
                c.sendData(new Packet(Packet.Packetid.CONNECTIONACCEPTED, connectionInfo));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else
        {
            try {
                c.sendData(new Packet(Packet.Packetid.CONNECTIONREFUSED, userName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startChatConnect(String ip, int port)
    {
        cSession = new ChatSession(ip, port);
        cSession.start();
        cSession.messageProperty().addListener(((observable, oldValue, newValue) -> {
            onServerMessage(newValue.substring(1 + newValue.indexOf('@'), newValue.indexOf('!')),
                    newValue.substring(1 + newValue.indexOf('!')));
        }));
        System.out.println("Client 2 og Client 1 har connected med hverandre");

    }



  public void getSendMessage()
  {

      String s= textField.getText();
      chatbox.appendText("Meg:"+s+'\n');
    System.out.println("Sender Dette"+s);
        cSession.sendMessage(s);
  }

}
