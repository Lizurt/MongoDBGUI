package gui.tree_node;

import gui.controllers.MainGUIController;
import javafx.scene.control.TreeItem;

public class DBNode extends SimpleTreeNode {
    public DBNode(String name, TreeItem<? extends TreeNode> container) {
        super(name, container);
    }

    @Override
    public void onSelected(MainGUIController mainGUIController) {
        mainGUIController.collectionViewController.onDisable();
        mainGUIController.databaseViewController.onEnable();
    }
}
