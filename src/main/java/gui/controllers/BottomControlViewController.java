package gui.controllers;

import custominput.Parser;
import gui.panes.AdvancedCodeArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BottomControlViewController implements Initializable {
    @FXML
    private AnchorPane bottomControlView;

    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // todo: ability to add new tabs
        addNewTab();
    }

    public void addNewTab() {
        Tab tab = new Tab("main");

        AnchorPane anchorPane = new AnchorPane();
        tab.setContent(anchorPane);

        HBox hBox = new HBox();
        VBox vBox = new VBox();
        SplitPane splitPane = new SplitPane();
        hBox.getChildren().addAll(vBox, splitPane);

        AdvancedCodeArea acaInput = new AdvancedCodeArea();
        AdvancedCodeArea acaOutput = new AdvancedCodeArea();
        acaInput.initAutocomplete();
        acaOutput.setEditable(false);
        splitPane.getItems().addAll(acaInput, acaOutput);

        Button buttonRun = new Button("Run");
        buttonRun.setPrefWidth(50);
        buttonRun.setMinWidth(50);
        buttonRun.setMaxWidth(50);
        buttonRun.setPrefHeight(50);
        buttonRun.setMinHeight(50);
        buttonRun.setMaxHeight(50);
        buttonRun.setOnAction(event -> {
            acaOutput.clear();
            String result = Parser.parseCommand(acaInput.getText());
            acaOutput.appendText(result);
        });
        vBox.getChildren().add(buttonRun);

        HBox.setHgrow(splitPane, Priority.ALWAYS);

        anchorPane.getChildren().add(hBox);
        AnchorPane.setTopAnchor(hBox, 0d);
        AnchorPane.setRightAnchor(hBox, 0d);
        AnchorPane.setBottomAnchor(hBox, 0d);
        AnchorPane.setLeftAnchor(hBox, 0d);

        tabPane.getTabs().add(tab);
    }
}
