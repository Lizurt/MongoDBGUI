package gui;

import com.mongodb.client.MongoCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import mongodb.Connection;
import mongodb.Util;
import mongodb.tree_node.CollectionNode;
import mongodb.tree_node.TreeNode;
import org.bson.Document;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class CollectionViewController implements Initializable {
    @FXML
    private VBox collectionView;

    @FXML
    private TreeView<TreeNode> collectionTreeView;
    private TreeItem<TreeNode> collectionTreeRoot;

    public void onEnable(CollectionNode collectionNode) {
        collectionView.setVisible(true);

        // clear the tree from old values, if any
        collectionTreeRoot.getChildren().clear();

        // and then add new items
        MongoCollection<Document> dbCollection =
                Connection
                        .getInstance()
                        .getMongoClient()
                        .getDatabase(collectionNode.getContainer().getValue().getName())
                        .getCollection(collectionNode.getName());
        TreeItem<TreeNode> lastTreeItem;
        for (Document dbRow : dbCollection.find()) {
            collectionTreeRoot.getChildren().add(
                    new TreeItem<>(
                            new CollectionNode(dbRow.getObjectId(Util.MONGO_ID_KEY).toString(), collectionTreeRoot)
                    )
            );
            for (Map.Entry<String, Object> dbCell : dbRow.entrySet()) {
                lastTreeItem = collectionTreeRoot.getChildren().get(collectionTreeRoot.getChildren().size() - 1);
                lastTreeItem.getChildren().add(
                        new TreeItem<>(
                                new CollectionNode(dbCell.getKey() + ": " + dbCell.getValue(), lastTreeItem)
                        )
                );
            }
        }
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
