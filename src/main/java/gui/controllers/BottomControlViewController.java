package gui.controllers;

import custom_input.InputHandler;
import gui.panes.ShelledTerminal;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import mongodb.ShelledConnection;

import java.net.URL;
import java.util.ResourceBundle;

public class BottomControlViewController implements Initializable {

    @FXML
    private AnchorPane bottomControlView;

    @FXML
    private Button buttonRun;

    private InputHandler inputHandler;

    @FXML
    private ShelledTerminal terminal;

    private ShelledConnection shelledConnection;

    @FXML
    void onButtonRunPressed(ActionEvent event) {
        //Platform.runLater(() -> shelledConnection.doQuery(terminal.getText()));
    }

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

        inputHandler = new InputHandler(terminal, shelledConnection);
        terminal.setOnKeyPressed(inputHandler::handleInput);

        new Thread(() -> {
            shelledConnection.blockThreadUntilReadiness();
            try {
                int nRead;
                final char[] buffer = new char[1024];

                // stderr is already in stdout so no need to read it somewhere else too
                while ((nRead = shelledConnection.getStdout().read(buffer, 0, buffer.length)) != -1) {
                    // we don't wanna make race conditions. This string builder is a good way to prevent it
                    final StringBuilder builder = new StringBuilder(nRead);
                    builder.append(buffer, 0, nRead);
                    Platform.runLater(() -> {
                        terminal.appendText(builder.toString());
                        terminal.onOutputFinished();
                    });
                }
            } catch(final Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
