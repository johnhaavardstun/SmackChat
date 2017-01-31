package Server.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali on 31.01.2017.
 */
public class MainController {

    @FXML Button kick;
    @FXML Button delete;
    @FXML ListView list;


    public void initialize()
    {
        setItems(ali);

        list.setOnMouseClicked((MouseEvent event) ->{
            if(event.getClickCount()==2 && (list.getSelectionModel().getSelectedItems()!=null))

                System.out.println(list.getSelectionModel().getSelectedItem());

        });
    }

    ArrayList<String> haha= new ArrayList<>();
    ObservableList<String> ali= FXCollections.observableArrayList(haha);





    private void setItems(ObservableList e)
    {

        haha.add("balle");

    list.setItems(e);

    }










}
