package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mongodb.Connection;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class MainGUIController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem miExec;

    @FXML
    private MenuItem miConnect;

    @FXML
    private TextField tfInput;

    @FXML
    private TextArea taOutput;

    @FXML
    void onExitButtonPressed(ActionEvent event) {
        System.exit(1);
    }

    @FXML
    void onExecButtonPressed(ActionEvent event) {
        taOutput.setText(Connection.getInstance().testCmd());
    }

    @FXML
    void onConnectButtonPressed(ActionEvent event) {
        try {
            Connection.getInstance().connect("mongodb://localhost:27017");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {

    }
}
