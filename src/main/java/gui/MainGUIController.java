package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import mongodb.Connection;
import mongodb.tree_node.CollectionNode;
import mongodb.tree_node.DBNode;
import mongodb.tree_node.TreeNode;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable {
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
    private TreeView<TreeNode> mongoTreeView;
    private TreeItem<TreeNode> mongoTreeRoot;

    @FXML
    private void onExitButtonPressed(ActionEvent event) {
        System.exit(1);
    }

    @FXML
    private void onExecButtonPressed(ActionEvent event) {
        taOutput.setText(Connection.getInstance().testCmd());
    }

    @FXML
    private void onConnectButtonPressed(ActionEvent event) {
        try {
            Connection.getInstance().connect("mongodb://127.0.0.1:27017");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (!Connection.getInstance().readyToWork()) {
            return;
        }

        for (String dbName : Connection.getInstance().getMongoClient().getDatabaseNames()) {
            mongoTreeRoot.getChildren().add(new TreeItem<>(new DBNode(dbName, mongoTreeRoot)));
        }

        for (TreeItem<TreeNode> dbTreeItem : mongoTreeRoot.getChildren()) {
            for (
                    String dbCollectionName :
                    Connection.getInstance()
                            .getMongoClient()
                            .getDB(dbTreeItem.getValue().getName())
                            .getCollectionNames()
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
        TreeItem<TreeNode> selectedItem = mongoTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        if (event.getClickCount() < 2) {
            return;
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mongoTreeRoot = new TreeItem<>(null);
        mongoTreeView.setRoot(mongoTreeRoot);
    }
}
