package gui.controllers;

import com.mongodb.client.MongoCollection;
import gui.tree_node.FieldNode;
import gui.tree_node.TreeNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import mongodb.Connection;
import mongodb.Util;
import gui.tree_node.CollectionNode;
import org.bson.Document;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class CollectionViewController implements Initializable {
    @FXML
    private AnchorPane collectionView;

    @FXML
    private TreeTableView<TreeNode> collectionTreeTableView;
    private TreeItem<TreeNode> collectionTreeTableRoot;

    @FXML
    private TreeTableColumn<FieldNode, String> keyTTC;
    @FXML
    private TreeTableColumn<FieldNode, String> typeTTC;
    @FXML
    private TreeTableColumn<FieldNode, String> valueTTC;

    public void onEnable(CollectionNode collectionNode) {
        collectionView.setVisible(true);

        // clear the tree from old values, if any
        collectionTreeTableRoot.getChildren().clear();

        // and then add new items
        MongoCollection<Document> dbCollection =
                Connection
                        .getInstance()
                        .getMongoClient()
                        .getDatabase(collectionNode.getContainer().getValue().toString())
                        .getCollection(collectionNode.getName());
        TreeItem<TreeNode> lastTreeItem;
        for (Document dbRow : dbCollection.find()) {
            collectionTreeTableRoot.getChildren().add(
                    new TreeItem<>(
                            new FieldNode(
                                    collectionTreeTableRoot,
                                    dbRow.getObjectId(Util.MONGO_ID_KEY).toString(),
                                    "{ " + dbRow.values().size() + " field(s) }",
                                    dbRow.getClass().getSimpleName()
                            )
                    )
            );
            for (Map.Entry<String, Object> dbCell : dbRow.entrySet()) {
                lastTreeItem = collectionTreeTableRoot.getChildren().get(
                        collectionTreeTableRoot.getChildren().size() - 1
                );
                lastTreeItem.getChildren().add(
                        new TreeItem<>(
                                new FieldNode(
                                        lastTreeItem,
                                        dbCell.getKey(),
                                        dbCell.getValue().toString(),
                                        dbCell.getValue().getClass().getSimpleName()
                                )
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
        // initialize the root with fields since we don't wanna complicate those factories below with null checks
        collectionTreeTableRoot = new TreeItem<>(
                new FieldNode(null, "", "", "")
        );
        collectionTreeTableView.setRoot(collectionTreeTableRoot);
        keyTTC.setCellValueFactory(fieldNodeStringCellDataFeatures -> fieldNodeStringCellDataFeatures.getValue().getValue().getSspKey());
        valueTTC.setCellValueFactory(fieldNodeStringCellDataFeatures -> fieldNodeStringCellDataFeatures.getValue().getValue().getSspValue());
        typeTTC.setCellValueFactory(fieldNodeStringCellDataFeatures -> fieldNodeStringCellDataFeatures.getValue().getValue().getSspType());
    }
}
