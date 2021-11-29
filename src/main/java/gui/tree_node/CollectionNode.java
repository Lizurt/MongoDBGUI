package gui.tree_node;

import gui.controllers.MainGUIController;
import javafx.scene.control.TreeItem;

public class CollectionNode extends SimpleTreeNode {
    public CollectionNode(String name, TreeItem<? extends TreeNode> container) {
        super(name, container);
    }

    @Override
    public void onSelected(MainGUIController mainGUIController) {
        mainGUIController.databaseViewController.onDisable();
        mainGUIController.collectionViewController.onEnable(this);
    }
}
