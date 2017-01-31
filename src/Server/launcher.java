package Server;

import Server.Model.userMangement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Ali on 31.01.2017.
 */
public class launcher extends Application {

    private final String FXMLFILE="./View/Server.fxml";

    public void start(Stage Server) throws IOException {

        URL serverLoad= getClass().getResource(FXMLFILE);


        Parent root= FXMLLoader.load(serverLoad);
        Parent shamil= FXMLLoader.load(serverLoad);
        Scene scene=new Scene(root,600,400);
        Server.setTitle("Server");
        Server.setScene(scene);
        Server.show();

    }

    public static void main(String[] args) throws IOException {

        userMangement s = new userMangement();
        s.readFile();
        s.toString();

        launch(args);
    }
}





