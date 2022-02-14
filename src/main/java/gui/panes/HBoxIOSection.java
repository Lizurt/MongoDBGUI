package gui.panes;

import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class HBoxIOSection extends HBox {
    private boolean isInputSection;
    private Label labelIOMark;
    private TerminalInputArea terminalInputArea;

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

        terminalInputArea = new TerminalInputArea();
        terminalInputArea.setStyle(acaStyle);
        terminalInputArea.setPrefHeight(0);
        terminalInputArea.setWrapText(true);

        textHolderCrutch.textProperty().bind(terminalInputArea.textProperty());
        textHolderCrutch.layoutBoundsProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                terminalInputArea.setPrefHeight(textHolderCrutch.getLayoutBounds().getHeight());
                terminalInputArea.setMinHeight(textHolderCrutch.getLayoutBounds().getHeight());
                terminalInputArea.setMaxHeight(textHolderCrutch.getLayoutBounds().getHeight());
                oldHeight = newValue.getHeight();
                textHolderCrutch.setWrappingWidth(terminalInputArea.getWidth() - 10);
            }
        });

        if (!isInputSection) {
            terminalInputArea.setEditable(false);
        }

        terminalInputArea.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            // Refiring events is a bad practice, but still the only solution I see
            // we already made code area auto extensible, so why do we need even to scroll it? Let outer pane scroll
            getParent().fireEvent(scrollEvent);
            // yeah and block code area's scrolling
            scrollEvent.consume();
        });

        getChildren().addAll(labelIOMark, terminalInputArea);
        setHgrow(terminalInputArea, Priority.ALWAYS);
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

    public TerminalInputArea getTerminalInputArea() {
        return terminalInputArea;
    }
}
