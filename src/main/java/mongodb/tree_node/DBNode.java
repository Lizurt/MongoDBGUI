package mongodb.tree_node;

import gui.MainGUIController;
import javafx.scene.control.TreeItem;

public class DBNode extends TreeNode {
    public DBNode(String name, TreeItem<TreeNode> container) {
        super(name, container);
    }

    @Override
    public void onSelected(MainGUIController mainGUIController) {
        mainGUIController.collectionViewController.onDisable();
        mainGUIController.databaseViewController.onEnable();
    }
}
