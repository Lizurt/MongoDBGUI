package gui.controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import gui.tree_node.DocumentFieldNode;
import gui.tree_node.TreeNode;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.AnchorPane;
import mongodb.Connection;
import mongodb.Util;
import gui.tree_node.CollectionNode;
import org.bson.Document;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.*;

public class CollectionViewController implements Initializable {
    @FXML
    private AnchorPane collectionView;

    @FXML
    private TreeTableView<DocumentFieldNode> collectionTreeTableView;
    private TreeItem<DocumentFieldNode> collectionTreeTableRoot;

    @FXML
    private TreeTableColumn<DocumentFieldNode, String> keyTTC;
    @FXML
    private TreeTableColumn<DocumentFieldNode, String> typeTTC;
    @FXML
    private TreeTableColumn<DocumentFieldNode, String> valueTTC;

    private MongoDatabase currentDatabase;

    private MongoCollection<Document> currentCollection;

    public void onEnable(CollectionNode collectionNode) {
        collectionView.setVisible(true);

        // clear the tree from old values, if any
        collectionTreeTableRoot.getChildren().clear();

        currentDatabase = Connection.getInstance().getMongoClient().getDatabase(
                collectionNode.getContainer().getValue().toString()
        );
        currentCollection = currentDatabase.getCollection(collectionNode.getName());

        for (Document dbRow : currentCollection.find(eq("status", "D"))) {
            DocumentFieldNode dbKeyDocumentFieldNode = new DocumentFieldNode(
                    collectionTreeTableRoot,
                    dbRow.getObjectId(Util.MONGO_ID_KEY).toString(),
                    "{ " + dbRow.values().size() + " field(s) }",
                    dbRow.getClass().getSimpleName(),
                    null
            );
            TreeItem<DocumentFieldNode> lastTreeItemDocument = new TreeItem<>(dbKeyDocumentFieldNode);
            collectionTreeTableRoot.getChildren().add(lastTreeItemDocument);
            for (Map.Entry<String, Object> dbCell : dbRow.entrySet()) {
                TreeItem<DocumentFieldNode> lastTreeItemField = new TreeItem<>(
                        new DocumentFieldNode(
                                lastTreeItemDocument,
                                dbCell.getKey(),
                                dbCell.getValue().toString(),
                                dbCell.getValue().getClass().getSimpleName(),
                                dbKeyDocumentFieldNode
                        )
                );
                lastTreeItemDocument.getChildren().add(lastTreeItemField);

            }
        }
    }

    public void onDisable() {
        collectionView.setVisible(false);
        // just in case
        currentDatabase = null;
        currentCollection = null;
    }

    @FXML
    public void onKeyEditFinished(TreeTableColumn.CellEditEvent<DocumentFieldNode, String> event) {

    }

    @FXML
    public void onValueEditFinished(TreeTableColumn.CellEditEvent<DocumentFieldNode, String> event) {
        BasicDBObject dbObjectWhere = new BasicDBObject(
                Util.MONGO_ID_KEY,
                new ObjectId(event.getRowValue().getValue().getDbKeyNode().getSspKey().getValue())
        );
        BasicDBObject dbObjectWhat = new BasicDBObject(
                Util.MONGO_SET_COMMAND,
                new BasicDBObject(
                        event.getRowValue().getValue().getSspKey().getValue(),
                        // todo: cast params to matching classes. The current solution casts all vals to strings
                        event.getNewValue()
                )
        );
        currentCollection.updateOne(dbObjectWhere, dbObjectWhat);
    }

    @FXML
    public void onTypeEditFinished(TreeTableColumn.CellEditEvent<DocumentFieldNode, String> event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize the root with fields since we don't wanna complicate those factories below with null checks
        collectionTreeTableRoot = new TreeItem<>(
                new DocumentFieldNode(null, "", "", "", null)
        );
        collectionTreeTableView.setRoot(collectionTreeTableRoot);

        keyTTC.setCellValueFactory(
                fieldNodeStringCellDataFeatures -> fieldNodeStringCellDataFeatures.getValue().getValue().getSspKey()
        );
        valueTTC.setCellValueFactory(
                fieldNodeStringCellDataFeatures -> fieldNodeStringCellDataFeatures.getValue().getValue().getSspValue()
        );
        typeTTC.setCellValueFactory(
                fieldNodeStringCellDataFeatures -> fieldNodeStringCellDataFeatures.getValue().getValue().getSspType()
        );

        keyTTC.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        valueTTC.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        typeTTC.setCellFactory(
                ChoiceBoxTreeTableCell.forTreeTableColumn(
                        FXCollections.observableArrayList(Util.MONGO_DATA_TYPES_STRINGED)
                )
        );
    }
}
