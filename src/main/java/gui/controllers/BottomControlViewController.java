package gui.controllers;

import gui.panes.AdvancedCodeArea;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import mongodb.ShelledConnection;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

public class BottomControlViewController implements Initializable {

    @FXML
    private AnchorPane bottomControlView;

    @FXML
    private Button buttonRun;

    @FXML
    private AdvancedCodeArea acaInput;

    @FXML
    private AdvancedCodeArea acaOutput;

    private ShelledConnection shelledConnection;

    @FXML
    void onButtonRunPressed(ActionEvent event) {
       Platform.runLater(() -> {
            acaOutput.appendText(acaInput.getText());
            acaOutput.appendText("\n");
            String output = "Something went wrong!";
            try {
                output = shelledConnection.doQuery(acaInput.getText());
            } catch (IOException e) {
                // todo: warning for a user
                e.printStackTrace();
            }
            acaOutput.appendText("< ");
            acaOutput.appendText(output);
        });
    }

    private void prepareForInput() {
        acaOutput.appendText("> ");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeInputArea();
        initializeOutputArea();
    }

    private void initializeInputArea() {
        acaInput.setOnKeyTyped(keyEvent -> {
            if (keyEvent.getCharacter().equals("\"")) {
                int currCaretPos = acaInput.getCaretPosition();
                acaInput.insertText(currCaretPos, "\"");
                acaInput.setStyle(
                        acaInput.getCaretColumn() - 2,
                        acaInput.getCaretColumn(),
                        // fixme: its not fucking working
                        // todo: dont forget to remove this fixme tag ^ before committing
                        Collections.singleton("quotes")
                );

                System.out.println(Arrays.toString(acaInput.getStylesheets().toArray()));
                System.out.println(acaInput.getStyleAtPosition(currCaretPos));

                acaInput.displaceCaret(currCaretPos);
            }
        });
    }

    private void initializeOutputArea() {
        try {
            shelledConnection = ShelledConnection.createDefault(
                    // todo: add this to configs
                    "C:\\Program Files\\MongoDB\\Server\\5.0\\bin\\mongo.exe"
            );
            prepareForInput();
        } catch (IOException e) {
            // todo: warning for a user
            e.printStackTrace();
        }
    }
}
