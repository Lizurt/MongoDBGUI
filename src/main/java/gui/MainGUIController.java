package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mongodb.Connection;

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
    private TreeView<String> mongoTreeView;
    private TreeItem<String> mongoTreeRoot;

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
            mongoTreeRoot.getChildren().add(new TreeItem<>(dbName));
        }

        for (TreeItem<String> dbTreeItem : mongoTreeRoot.getChildren()) {
            for (
                    String dbCollectionName
                    : Connection.getInstance().getMongoClient().getDB(dbTreeItem.getValue()).getCollectionNames()
            ) {
                dbTreeItem.getChildren().add(new TreeItem<>(dbCollectionName));
            }
        }
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void onMongoTreeViewClicked() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mongoTreeRoot = new TreeItem<>("ROOT");
        mongoTreeView.setRoot(mongoTreeRoot);
    }
}
