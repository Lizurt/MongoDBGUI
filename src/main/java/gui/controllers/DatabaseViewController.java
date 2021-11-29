package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class DatabaseViewController {
    @FXML
    private AnchorPane databaseView;

    public void onEnable() {
        databaseView.setVisible(true);
    }

    public void onDisable() {
        databaseView.setVisible(false);
    }
}
