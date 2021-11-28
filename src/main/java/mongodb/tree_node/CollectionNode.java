package mongodb.tree_node;

import gui.MainGUIController;
import javafx.scene.control.TreeItem;

public class CollectionNode extends TreeNode {
    public CollectionNode(String name, TreeItem<TreeNode> container) {
        super(name, container);
    }

    @Override
    public void onSelected(MainGUIController mainGUIController) {
        mainGUIController.databaseViewController.onDisable();
        mainGUIController.collectionViewController.onEnable(this);
    }
}
