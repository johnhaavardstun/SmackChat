package Server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * This class launches the SmackChat server.
 *
 */
public class launcher extends Application {

    private final String FXMLFILE="./View/Server.fxml";

    public void start(Stage server) throws IOException {
        URL serverLoad= getClass().getResource(FXMLFILE);

        Parent root= FXMLLoader.load(serverLoad);
        Scene scene=new Scene(root,600,400);
        server.getIcons().add(new Image("./Server/View/server.png"));

        server.setTitle("Server");
        server.setScene(scene);
        server.show();
    }

    public static void main(String[] args) throws IOException {

        launch(args);
    }
}
