package Client.Controller;


import Client.Model.ChatSession;
import Client.Model.Client;
import Client.Model.Packet;
import Client.Model.UserStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the main controller of the client which gets the events from the view and deals with user input.
 *
 */
public class MainController {

    // in ClientChat.fxml
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button login;
    @FXML private Button register;

    // in startToChat.fxml
    @FXML private Button connect;
    @FXML private Button send;
    @FXML private Button disconnect;
    @FXML private ListView<UserStatus> userList;
    @FXML private TextArea chatbox;
    @FXML private TextField textField;
    @FXML private Label userinfo;

    private    String  superdupercode="§§§¤";
    private String serverIP = "127.0.0.1";
    private Client c;
    private ChatSession cSession;
    private boolean firstRun = false;
    private SimpleBooleanProperty isChatting = new SimpleBooleanProperty(false);

    /**
     * This method initializes this client, by doing so it starts a new thread for this client.
     *
     */
    public void initialize()
    {
        if (!firstRun) {
            firstRun = true;

            login.disableProperty().bind(Bindings.and(
                    Bindings.length(username.textProperty()).greaterThan(1),
                    Bindings.length(password.textProperty()).greaterThan(1)
                ).not());
            register.disableProperty().bind(Bindings.and(
                    Bindings.length(username.textProperty()).greaterThan(1),
                    Bindings.length(password.textProperty()).greaterThan(1)
            ).not());
        }
    }

    /**
     * This method handles the packet data sent from the server and
     * executes the correct case based on the packet data and message
     * received from the server.
     *
     * <p>Each packet this method receives has a packet id, by
     * getting the packet id to the packet sent from the server, this
     * method will use switch case to handle all the different cases
     * of packet id. Based on the case this method decides which view and
     * data to display/modify next.</p>
     *
     * @param message the message received from the server
     * @param data the data received form the server
     */
    private void onServerMessage(String message, String data)
    {
        switch (message)
        {
            case "USERNAME_TAKEN":
                showMessageToClient(AlertType.ERROR, "Username taken!",
                        "Please choose another username.");
                break;
            case "LOGIN_OK":
                System.out.println("login ok!");
                switchToLoginScene();
                break;
            case "REGISTER_OK":
                showMessageToClient(AlertType.CONFIRMATION, "Successfully registered!",
                        "Welcome to SmackChat - enjoy your stay!");
                break;
            case "WRONG_LOGIN":
                showMessageToClient(AlertType.ERROR, "Login failed!",
                        "The username and/or password is incorrect, scrub!");
                break;
            case "SERVER_LOST":
                showMessageToClient(AlertType.ERROR, "Server lost!",
                        "The connection to the server was lost, please try again later.");
                break;
            case "USER_LIST":
                updateUserList(data);
//                System.out.println(data);
                break;
            case "CHAT":
                System.out.println("Mottok en chat reuqest");
                chatConfirmation(data);
                break;
            case "CHAT_MESSAGE":
                System.out.println("Har motatt chat melding!");
                incomingChatMessage(data);
                break;
            case "BAD_REQUEST":
                System.out.println("skjedde en feil");
                break;
            case "CHAT_CONNECTION_INFORMATION":
                String[] cinfo = data.split(":");
                System.out.println(Arrays.toString(cinfo));
                startChatConnect(cinfo[0], Integer.parseInt(cinfo[1]),cinfo[2]);
                System.out.println("Mottok kontakt informasjon!");
                break;
            case "CHAT_CLOSED":
                try
                {
                    isChatting.set(false);
                    cSession = null;
                    textField.setText(null);
                    chatbox.appendText("System: You have been disconnected from the chat.\n");
                    c.sendData(new Packet(Packet.PacketId.CHANGE_STATUS_ONLINE,"Changing status to online"));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * This method updates the user list visually specified by data, which contains a string of all
     * registered users and their current status.
     *
     * @param data contains all registered users and their current status
     */
    private void updateUserList(String data)
    {
        UserStatus selectedUser = userList.getSelectionModel().getSelectedItem();

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

        if (selectedUser != null)
        {
            for (UserStatus userStatus : newList)
            {
                if (userStatus.getUserName().equals(selectedUser.getUserName()))
                {
                    userList.getSelectionModel().select(userStatus);
                    break;
                }
            }
        }
    }

    /**
     * When a user is successfully logged in, this method switches from the log in scene
     * to the SmackChat scene. The SmackChat scene is where the user can chat with author
     * clients that are currently online.
     *
     */
    private void switchToLoginScene()
    {

        URL chatload = getClass().getResource("../View/startToChat.fxml");
        Parent root = null;
        try {
            FXMLLoader fxml = new FXMLLoader(chatload);
            fxml.setController(this);
            root = fxml.load();
            chatbox.setEditable(false);
            userinfo.setText("Logged in as: "+c.getUser());


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR, "Could not log in", "A unexpected error has occured, please restart");
            e.printStackTrace();
            System.exit(9876543);
        }

        ((Stage) login.getScene().getWindow()).setScene(new Scene(root, 600, 400));

        send.setDefaultButton(true);
        send.disableProperty().bind(isChatting.not());
        textField.disableProperty().bind(isChatting.not());
        connect.disableProperty().bind(isChatting);
        disconnect.disableProperty().bind(isChatting.not());
//
//        if (userList == null)
//            userList = (ListView) root.lookup("#userList");

        userList.setCellFactory(new Callback<ListView<UserStatus>, ListCell<UserStatus>>()
        {

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
                    protected void updateItem(UserStatus userStatus, boolean empty)
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
            c.sendData(new Packet(Packet.PacketId.USER_LIST_REQUEST, null));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method displays a message to this client specified by the type of the alert.
     *
     * The message is displayed to this client when this client presses a button.
     *
     * @param type the type of the alert
     * @param title the title of the alert
     * @param text the content text of the alert
     * @return true if the button clicked is OK; false otherwise
     */
    public static Optional<ButtonType> showMessageToClient(AlertType type, String title, String text)
    {
        Alert alert= new Alert(type);
        alert.setTitle(title);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return result;

    }

    @FXML
    public void changeServerIP()
    {
        TextInputDialog dialog = new TextInputDialog(serverIP);
        dialog.setTitle("Change server IP address");
        dialog.setContentText("Server IP");
        dialog.setHeaderText(null);
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(s -> serverIP = s);
    }

    /**
     * This method sends a LOGIN packet to the server, where the packet contains
     * the username and password specified in the text fields by the client.
     * This method will display a error message to the client if the server is down.
     */
    public void login()
    {
        c = new Client(serverIP);
        c.start();

        c.messageProperty().addListener(((observable, oldValue, newValue) -> {
            onServerMessage(newValue.substring(1 + newValue.indexOf('@'), newValue.indexOf('!')),
                    newValue.substring(1 + newValue.indexOf('!')));
        }));

        c.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            showMessageToClient(AlertType.ERROR, "Server error!", newValue.getMessage());
//                System.exit(876);
        });

        try
        {
            Thread.sleep(200);
            String sd=getTextFieldData(username)+superdupercode+getTextFieldData(password);
            System.out.println(sd);

            Packet pa= new Packet(Packet.PacketId.LOGIN,sd);

            c.sendData(pa);
        }
        catch (IOException e)
        {
            showMessageToClient(AlertType.ERROR,"Connection Refused","Server is down please try again later!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method sends a REGISTER packet to the server, where the packet contains
     * the username and password specified in the text fields by the client.
     * If the username or password specified in the text fields contain special
     * characters, the client will receive a error message and register packet will
     * not be send to the server.
     *
     * @throws IOException Input exception
     */
    public void register() throws IOException {

         try {
             String regx = "([a-zA-Z0-9\\.\\_\\-])+";
             Pattern pattern=Pattern.compile(regx);

             String username=getTextFieldData(this.username);
             String password=getTextFieldData(this.password);
             Matcher user= pattern.matcher(username);
             Matcher pass= pattern.matcher(password);

             if(user.matches() && pass.matches()) {
                 String s = username + superdupercode + password;

                 Packet p = new Packet(Packet.PacketId.REGISTER, s);
                 c.sendData(p);
             }
           else{
                 showMessageToClient(AlertType.ERROR,"No special charachters allowed", "Your username or password have special characters");
             }


        } catch (IOException e) {
            showMessageToClient(AlertType.ERROR,"Input exception","Please contact the administrator");
            e.printStackTrace();
        }

    }

    /**
     * This method returns a string which contains the text specified in the text field
     * by the client.
     *
     * @param tx the text field where the text is written into
     * @return the text specified in the specified text field
     */
    public String getTextFieldData(TextField tx)
    {
        String s=tx.getText();
        return s;
    }

    /**
     * This method will send a connection request packet to a user displayed in the user list.
     * The connection request can only be sent to a user which is currently online and not yourself.
     */
    @FXML
    public void connectionRequest()
    {
        UserStatus statusU = userList.getSelectionModel().getSelectedItem();
        String s = statusU.getUserName();
        if(statusU != null && statusU.getStatus() == UserStatus.Status.ONLINE &&(!s.equals(c.getUser()))) {

            System.out.println(s);
                try {
                    c.sendData(new Packet(Packet.PacketId.CHAT_CONNECTION_REQUEST_SERVER, s));
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    /**
     * This method sends a chat request message to the specified client.
     * If the specified client answers positive to the chat request, this method will start
     * a new chat session between you and the specified client by getting the ip and port number
     * of the specified client.
     * If the specified client has accepted the chat request, both clients status will be set to busy.
     *
     * @param userName the user name of the client you want to chat with
     */
    public void chatConfirmation(String userName)
    {
        Optional<ButtonType> result = showMessageToClient(AlertType.CONFIRMATION,
                "Accept?","Chat request from " + userName + "!");

        if(result.isPresent() && result.get() == ButtonType.OK)
        {
            try {
                cSession = new ChatSession();
                cSession.start();
                cSession.messageProperty().addListener(((observable, oldValue, newValue) -> {
                    onServerMessage(newValue.substring(1 + newValue.indexOf('@'), newValue.indexOf('!')),
                            newValue.substring(1 + newValue.indexOf('!')));
                }));

                // TODO: fix this shit
//                String connectionInfo = "127.0.0.1" + ":" + cSession.getServerPort() + ":" + userName;
                String connectionInfo = cSession.getServerIP() + ":" + cSession.getServerPort() + ":" + userName;
                System.out.println(connectionInfo);
                cSession.setChatter(userName);
                c.sendData(new Packet(Packet.PacketId.CHAT_CONNECTION_ACCEPTED, connectionInfo));

                c.sendData(new Packet(Packet.PacketId.CHANGE_STATUS_BUSY,"Changing status to busy!"));
                isChatting.set(true);
                chatbox.appendText("System: You are now chatting with: " + cSession.getChattingWith() + '\n');
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                c.sendData(new Packet(Packet.PacketId.CHAT_CONNECTION_REFUSED, userName));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method launches the chat session between yourself and the client you want to chat with. It does this by
     * specifying the author client name, port and ip number.
     * This method enables each client to send messages to each other and display/updates the messages on the chat box.
     *
     * @param ip the ip number of the client you are chatting with
     * @param port the port number of the client you are chatting with
     * @param name the user name of the client you are chatting with
     */
    public void startChatConnect(String ip, int port, String name)
    {
        cSession = new ChatSession(ip, port);
        cSession.start();
        cSession.messageProperty().addListener(((observable, oldValue, newValue) -> {
            onServerMessage(newValue.substring(1 + newValue.indexOf('@'), newValue.indexOf('!')),
                    newValue.substring(1 + newValue.indexOf('!')));
        }));

        cSession.setChatter(name);
        chatbox.appendText("System: You are now chatting with: " + cSession.getChattingWith() + '\n');
        isChatting.set(true);
        try {
            c.sendData(new Packet(Packet.PacketId.CHANGE_STATUS_BUSY,"Changing status to busy!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client 2 og Client 1 har connected med hverandre");

    }


    /**
     * When there is created a chat session between you and another client,
     * this method sends and displays the message, you have specified in the text field, into the chat box.
     *
     */
    public void getSendMessage() {
        if (isChatting.get())
        {
            String s = textField.getText();
            if (!(s.trim().equals("")))
            {
                chatbox.appendText("Me: " + s + '\n');
                textField.clear();
                try
                {
                    cSession.sendData(new Packet(Packet.PacketId.CHAT_MESSAGE, s));
                }
                catch (IOException e)
                {
                    showMessageToClient(AlertType.ERROR, "Error!", "Unexpected error sending message!");
                }
            }
        }
        else
        {
            textField.clear();
            chatbox.appendText("System: You are not chatting with anyone!"+'\n');
        }
    }

    /**
     * This method displays the incoming chat message, you have received from the other client, into
     * the chat box.
     * @param message
     */
    public void incomingChatMessage(String message)
    {
        chatbox.appendText(cSession.getChattingWith() + ": " + message + "\n");
    }


    /**
     * This method disconnects from the current chat session, so that you can chat with someone else.
     */
    @FXML
    public void disconnectFromChat()
    {
        if (isChatting.get())
        {
            cSession.endChat();
        }
    }

}
