package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import mongodb.tree_node.CollectionNode;
import mongodb.tree_node.TreeNode;

import java.net.URL;
import java.util.ResourceBundle;

public class CollectionViewController implements Initializable {
    @FXML
    private VBox collectionView;

    @FXML
    private TreeView<TreeNode> collectionTreeView;
    private TreeItem<TreeNode> collectionTreeRoot;

    public void onEnable(CollectionNode collectionNode) {
        collectionView.setVisible(true);
/* todo
        DBCollection dbCollection =
                Connection
                        .getInstance()
                        .getMongoClient()
                        .getDB(collectionNode.getContainer().getValue().getName())
                        .getCollection(collectionNode.getName());
        Map.Entry<String, String> dbCell;
        for (DBObject dbRow : dbCollection.find()) {

            collectionTreeRoot.getChildren().add(new TreeItem<>());
            for (Object dbCellO : dbRow.toMap().entrySet()) {
                // no generics in bsonobject????? how is this possible in 2021? Have to use this stoopid cast sadly.
                // IJIDEA blaming me with its yellow color so much...
                dbCell = (Map.Entry<String, String>) dbCellO;
                System.out.println();
            }
        }*/
    }

    public void onDisable() {
        collectionView.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        collectionTreeRoot = new TreeItem<>(null);
        collectionTreeView.setRoot(collectionTreeRoot);
    }
}
