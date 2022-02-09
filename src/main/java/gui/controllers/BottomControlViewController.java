package gui.controllers;

import javafx.scene.control.ScrollPane;
import custom_input.TerminalIOHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import mongodb.ShelledConnection;

import java.net.URL;
import java.util.ResourceBundle;

public class BottomControlViewController implements Initializable {
    @FXML
    private VBox vBoxIOList;

    @FXML
    private AnchorPane bottomControlView;

    @FXML
    private ScrollPane scrollPane;

    private TerminalIOHandler terminalIOHandler;

    private ShelledConnection shelledConnection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTerminal();
    }

    private void initializeTerminal() {
        shelledConnection = new ShelledConnection(
                // todo: add this to configs
                "C:\\Program Files\\MongoDB\\Server\\5.0\\bin\\mongo.exe"
        );

        terminalIOHandler = new TerminalIOHandler(vBoxIOList, shelledConnection);
        // auto scrolling down
        vBoxIOList.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        new Thread(terminalIOHandler::startStreamReader).start();
    }
}
