package gui.treenode;

import gui.controllers.MainGUIController;
import javafx.scene.control.TreeItem;

public class SimpleTreeNode extends TreeNode {
    private String name;

    public SimpleTreeNode(String name, TreeItem<? extends TreeNode> container) {
        super(container);
        setName(name);
    }

    public void onSelected(MainGUIController mainGUIController) {

    }

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
}
