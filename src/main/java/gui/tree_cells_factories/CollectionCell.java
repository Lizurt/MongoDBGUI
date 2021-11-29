package gui.tree_cells_factories;

import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;

public class CollectionCell extends TreeCell<String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (isEmpty()) {
            setGraphic(null);
            setText(null);
            return;
        }

        if (this.getTreeItem().isLeaf()) {
            HBox cellBox = new HBox();
           // cellBox.getChildren().addAll(checkBox, label, button);
            setGraphic(cellBox);
            setText(null);
        } else {
            // If this is the root we just display the text.
            setGraphic(null);
            setText(item);
        }
    }
}
