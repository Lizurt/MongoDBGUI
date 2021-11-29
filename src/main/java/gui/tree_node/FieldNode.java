package gui.tree_node;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

public class FieldNode extends TreeNode {
    private SimpleStringProperty sspKey;
    private SimpleStringProperty sspValue;
    private SimpleStringProperty sspType;

    public FieldNode(TreeItem<TreeNode> container, String sspKey, String sspValue, String sspType) {
        super(container);
        this.sspKey = new SimpleStringProperty(sspKey);
        this.sspValue = new SimpleStringProperty(sspValue);
        this.sspType = new SimpleStringProperty(sspType);
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
}
