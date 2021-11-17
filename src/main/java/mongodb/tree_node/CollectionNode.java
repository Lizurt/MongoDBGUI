package mongodb.tree_node;

import javafx.scene.control.TreeItem;

public class CollectionNode extends TreeNode {
    public CollectionNode(String name, TreeItem<TreeNode> container) {
        super(name, container);
    }

    @Override
    public void onSelected() {
        super.onSelected();
    }
}
