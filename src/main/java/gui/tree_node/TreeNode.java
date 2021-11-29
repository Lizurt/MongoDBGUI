package gui.tree_node;

import javafx.scene.control.TreeItem;

public abstract class TreeNode {
    private TreeItem<? extends TreeNode> container;

    public TreeNode(TreeItem<? extends TreeNode> container) {
        setContainer(container);
    }

    public TreeItem<? extends TreeNode> getContainer() {
        return container;
    }

    public void setContainer(TreeItem<? extends TreeNode> container) {
        this.container = container;
    }
}
