package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class BottomControlViewController implements Initializable {

    @FXML
    private AnchorPane bottomControlView;

    @FXML
    private Button buttonRun;

    @FXML
    private TextArea taInput;

    @FXML
    private TextArea taOutput;

    @FXML
    void onButtonRunPressed(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
