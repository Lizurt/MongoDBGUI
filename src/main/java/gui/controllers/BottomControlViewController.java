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
        initializeInputArea();
        initializeTerminal();
    }

    private void initializeInputArea() {
        /* fixme: intellisense, syntax highlighting etc
        acaInput.setOnKeyTyped(keyEvent -> {
            if (keyEvent.getCharacter().equals("\"")) {
                int currCaretPos = acaInput.getCaretPosition();
                acaInput.insertText(currCaretPos, "\"");
                acaInput.setStyle(
                        acaInput.getCaretColumn() - 2,
                        acaInput.getCaretColumn(),
                        // fixme: its not flipping working i already spent 2 hours on making quotes green AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        Collections.singleton("quotes")
                );

                System.out.println(Arrays.toString(acaInput.getStylesheets().toArray()));
                System.out.println(acaInput.getStyleAtPosition(currCaretPos));

                acaInput.displaceCaret(currCaretPos);
            }
        });*/
    }

    private void initializeTerminal() {
        shelledConnection = new ShelledConnection(
                // todo: add this to configs
                "C:\\Program Files\\MongoDB\\Server\\5.0\\bin\\mongo.exe"
        );

        terminalIOHandler = new TerminalIOHandler(scrollPane, vBoxIOList, shelledConnection);
        new Thread(terminalIOHandler::startStreamReader).start();
    }
}
