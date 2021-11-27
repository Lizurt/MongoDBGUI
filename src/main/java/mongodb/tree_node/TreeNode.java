package mongodb.tree_node;

import gui.MainGUIController;
import javafx.scene.control.TreeItem;

public abstract class TreeNode {
    private String name;
    private TreeItem<TreeNode> container;

    public TreeNode(String name, TreeItem<TreeNode> container) {
        setName(name);
        setContainer(container);
    }

    public abstract void onSelected(MainGUIController mainGUIController);

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeItem<TreeNode> getContainer() {
        return container;
    }

    public void setContainer(TreeItem<TreeNode> container) {
        this.container = container;
    }
}
