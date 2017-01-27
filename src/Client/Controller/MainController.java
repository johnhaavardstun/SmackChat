package Client.Controller;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
public class MainController {

    @FXML TextField user1;
    @FXML PasswordField pass1;

    @FXML Button butteng;
    @FXML Button Register;


    public MainController() {
        // TODO Auto-generated constructor stub
    }


    public void noServerConnection()
    {


        Alert alert= new Alert(AlertType.ERROR);

        alert.setTitle("SmackChat Could not connect to server");
        alert.setContentText("Could Not connect to the server, please  check your internett connection or firewall settings!");

        alert.showAndWait();

    }


    public void wrongUsernameOrPassword()
    {


        Alert alert= new Alert(AlertType.WARNING);

        alert.setTitle("Wrong information");
        alert.setContentText("Wrong username or password");

        alert.showAndWait();

    }


}
