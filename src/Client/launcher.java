package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;

/**This class launches the client.
 *
 * Created by Ali on 31.01.2017.
 * @version IntelliJ IDEA 2016.3.4
 */
public class launcher extends Application {

    private final String FXMLFILE="./View/ClientChat.fxml";



    public void start(Stage chat) throws IOException {

        URL chatload= getClass().getResource(FXMLFILE);


        chat.getIcons().add(new Image("./Client/View/chat.png"));
        Parent root= FXMLLoader.load(chatload);
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



