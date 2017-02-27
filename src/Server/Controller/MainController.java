package Server.Controller;

import Server.Model.Server;
import Server.Model.Service;
import Server.Model.User;
import Server.Model.UserManagement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class is the main controller of the server which launches the server and handles all GUI functionality
 * to SmackChat.
 *
 */
public class MainController {

    @FXML ListView<User> list;
    @FXML Label serverInfo;
    @FXML Label userIP;
    @FXML Label userPort;

    /**
     * This method initializes the Server. By doing so it displays
     * and updates all the registered users in SmackChat, where the server
     * can get information, kick or delete a user.
     */
    public void initialize()
    {

        try {
            UserManagement.readFile();
            list.setItems(FXCollections.observableList(UserManagement.getUsers()));
            UserManagement.addStatusListener(user -> {
                Platform.runLater(() -> {
                    User u = list.getSelectionModel().getSelectedItem();
                    list.setItems(null);
                    list.setItems(FXCollections.observableList(UserManagement.getUsers()));
                    list.getSelectionModel().select(u);
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Server serv = new Server();
        serv.start();

        try {
            serverInfo.setText("Server running on " + InetAddress.getLocalHost().getHostAddress() + ":" + serv.PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        list.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {

            final Image onlineImage = new Image(getClass().getResourceAsStream("../View/online.png"));
            final Image offlineImage = new Image(getClass().getResourceAsStream("../View/offline.png"));
            final Image busyImage = new Image(getClass().getResourceAsStream("../View/busy.png"));

            @Override
            public ListCell<User> call(ListView<User> param)
            {
                ListCell<User> cell = new ListCell<User>()
                {

                    final ImageView statusIcon;

                    {
                        statusIcon = new ImageView();
                    }

                    @Override
                    protected void updateItem(User user, boolean empty)
                    {
                        super.updateItem(user, empty);
                        if (user != null)
                        {
                           // System.out.println(user.getUsername() + ":" + user.getStatus());
                            switch (user.getStatus())
                            {
                                case ONLINE: statusIcon.setImage(onlineImage); break;
                                case OFFLINE: statusIcon.setImage(offlineImage); break;
                                case BUSY: statusIcon.setImage(busyImage); break;
                                default: statusIcon.setImage(null);
                            }

                            setText(user.getUsername());
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

        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue != null)
            {
                Service s = Server.map.get(newValue.getUsername());
                if (s != null)
                {
                    userIP.setText("User IP: " + s.getConnectedIPAddress());
                    userPort.setText("User port: " + s.getConnectedPort());
                }
                else
                {
                    userIP.setText("User IP: ---");
                    userPort.setText("User port: ---");
                }
            }
            else
            {
                userIP.setText("User IP: ---");
                userPort.setText("User port: ---");
            }
        });

    }


}
