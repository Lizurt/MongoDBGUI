package gui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class CollectionViewController {
    @FXML
    private VBox collectionView;

    public void onEnable() {
        collectionView.setVisible(true);
    }

    public void onDisable() {
        collectionView.setVisible(false);
    }
}
