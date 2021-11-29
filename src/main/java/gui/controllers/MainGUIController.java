package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import mongodb.Connection;
import gui.tree_node.CollectionNode;
import gui.tree_node.DBNode;
import gui.tree_node.SimpleTreeNode;

import java.net.URL;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem miConnect;

    @FXML
    private TextField tfInput;

    @FXML
    private TextArea taOutput;

    @FXML
    private TreeView<SimpleTreeNode> mongoTreeView;
    private TreeItem<SimpleTreeNode> mongoTreeRoot;

    // todo: make these fields private. At the moment I'm not sure if there is no a better approach
    @FXML
    public CollectionViewController collectionViewController;
    @FXML
    public DatabaseViewController databaseViewController;


    @FXML
    private void onExitButtonPressed(ActionEvent event) {
        System.exit(1);
    }

    @FXML
    private void onConnectButtonPressed(ActionEvent event) {
        Connection.getInstance().connect("mongodb://127.0.0.1:27017");

        if (!Connection.getInstance().readyToWork()) {
            return;
        }

        for (String dbName : Connection.getInstance().getMongoClient().listDatabaseNames()) {
            mongoTreeRoot.getChildren().add(new TreeItem<>(new DBNode(dbName, mongoTreeRoot)));
        }

        for (TreeItem<SimpleTreeNode> dbTreeItem : mongoTreeRoot.getChildren()) {
            for (
                    String dbCollectionName :
                    Connection.getInstance()
                            .getMongoClient()
                            .getDatabase(dbTreeItem.getValue().getName())
                            .listCollectionNames()
            ) {
                dbTreeItem.getChildren().add(new TreeItem<>(new CollectionNode(dbCollectionName, dbTreeItem)));
            }
        }
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void onMongoTreeViewClicked(MouseEvent event) {
        TreeItem<SimpleTreeNode> selectedItem = mongoTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        if (event.getClickCount() < 2) {
            return;
        }

        selectedItem.getValue().onSelected(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mongoTreeRoot = new TreeItem<>(null);
        mongoTreeView.setRoot(mongoTreeRoot);
        // I'm just too lazy to switch visibility in SceneBuilder each time I want to edit those fxmls,
        // so they're visible by default, but shouldn't. Let's fix it (a bit crutchy but effective!)
        databaseViewController.onDisable();
        collectionViewController.onDisable();
    }
}
