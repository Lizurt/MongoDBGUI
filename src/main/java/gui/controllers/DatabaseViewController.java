package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class DatabaseViewController {
    @FXML
    private VBox databaseView;

    public void onEnable() {
        databaseView.setVisible(true);
    }

    public void onDisable() {
        databaseView.setVisible(false);
    }
}
