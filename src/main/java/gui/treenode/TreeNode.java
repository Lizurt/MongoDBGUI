package gui.treenode;

import javafx.scene.control.TreeItem;

public abstract class TreeNode {
    private TreeItem<? extends TreeNode> container;

    public TreeNode(TreeItem<? extends TreeNode> container) {
        this.container = container;
    }

    public TreeItem<? extends TreeNode> getContainer() {
        return container;
    }
}
