package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class launcher extends Application {

    private final String FXMLFILE="./View/ClientChat.fxml";



    public void start(Stage chat) throws IOException {

        URL chatload= getClass().getResource(FXMLFILE);


        Parent root= FXMLLoader.load(chatload);
        Parent shamil= FXMLLoader.load(chatload);
        Scene scene=new Scene(root,600,400);
        chat.setTitle("SmackChat!");
        chat.setScene(scene);
        chat.show();

    }

    public static void main(String[] args)
    {
        launch(args);
    }
}


