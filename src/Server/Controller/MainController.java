package Server.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.List;

/**
 * Created by Ali on 31.01.2017.
 */
public class MainController {

    @FXML Button kick;
    @FXML Button delete;
    @FXML ListView list;


    ObservableList<String> ali= FXCollections.observableArrayList("Ali","shamil","Jøde Erik","Balle Erik");



    public void loadUsers()
    {
        setItems(ali);

    }

    private void setItems(ObservableList e)
    {

    list.setItems(e);

    }








}
