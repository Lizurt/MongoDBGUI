package gui.tree_node;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

public class DocumentFieldNode extends TreeNode {
    private SimpleStringProperty sspKey;
    private SimpleStringProperty sspValue;
    private SimpleStringProperty sspType;

    // just for a quick access to documents (no need to search for a primary key through foreach()).
    // Primary keys-field-nodes have a null link
    private DocumentFieldNode dbKeyNode;

    public DocumentFieldNode(
            TreeItem<? extends TreeNode> container,
            String sspKey,
            String sspValue,
            String sspType,
            DocumentFieldNode dbKeyNode
    ) {
        super(container);
        this.sspKey = new SimpleStringProperty(sspKey);
        this.sspValue = new SimpleStringProperty(sspValue);
        this.sspType = new SimpleStringProperty(sspType);
        this.dbKeyNode = dbKeyNode;
    }

    public SimpleStringProperty getSspKey() {
        return sspKey;
    }

    public SimpleStringProperty getSspValue() {
        return sspValue;
    }

    public SimpleStringProperty getSspType() {
        return sspType;
    }

    public DocumentFieldNode getDbKeyNode() {
        return dbKeyNode;
    }
}
