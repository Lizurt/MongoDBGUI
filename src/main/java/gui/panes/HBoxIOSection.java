package gui.panes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class HBoxIOSection extends HBox {
    private boolean isInputSection;
    private Label labelIOMark;
    private AdvancedCodeArea advancedCodeArea;

    // some crutches for vertical auto expanding
    private Text textHolderCrutch = new Text();
    private double oldHeight = 0;

    public static final String DEFAULT_INPUT_MARK = "> ";
    public static final String DEFAULT_OUTPUT_MARK = "< ";

    public HBoxIOSection(boolean isInputSection, String ioMark, String ioMarkStyle, String acaStyle) {
        super();

        this.isInputSection = isInputSection;

        labelIOMark = new Label(ioMark);
        labelIOMark.setStyle(ioMarkStyle);

        advancedCodeArea = new AdvancedCodeArea();
        advancedCodeArea.setStyle(acaStyle);
        advancedCodeArea.setPrefHeight(0);
        advancedCodeArea.setWrapText(true);

        textHolderCrutch.textProperty().bind(advancedCodeArea.textProperty());
        textHolderCrutch.layoutBoundsProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                advancedCodeArea.setPrefHeight(textHolderCrutch.getLayoutBounds().getHeight());
                advancedCodeArea.setMinHeight(textHolderCrutch.getLayoutBounds().getHeight());
                advancedCodeArea.setMaxHeight(textHolderCrutch.getLayoutBounds().getHeight());
                oldHeight = newValue.getHeight();
                textHolderCrutch.setWrappingWidth(advancedCodeArea.getWidth() - 10);
            }
        });

        if (!isInputSection) {
            advancedCodeArea.setEditable(false);
        }

        getChildren().addAll(labelIOMark, advancedCodeArea);
        setHgrow(advancedCodeArea, Priority.ALWAYS);
    }

    public HBoxIOSection(boolean isInputSection, String ioMark) {
        this(isInputSection, ioMark, "", "");
    }

    public HBoxIOSection(boolean isInputSection) {
        this(isInputSection, isInputSection ? DEFAULT_INPUT_MARK : DEFAULT_OUTPUT_MARK);
    }

    public boolean isInputSection() {
        return isInputSection;
    }

    public Label getLabelIOMark() {
        return labelIOMark;
    }

    public AdvancedCodeArea getAdvancedCodeArea() {
        return advancedCodeArea;
    }
}
