package gui.panes;

import org.fxmisc.richtext.CodeArea;

public class ShelledTerminal extends CodeArea {
    private int currentCommandStart;
    // make sure nobody'll try to get a command that's still being written.
    // Punching with an exception is an effective way to do it
    private int currentCommandEnd = -1;

    private String inputMark = "> ";
    private String outputMark = "< ";

    public String getNewCommand() {
        return getText(currentCommandStart, currentCommandEnd);
    }

    public void onCommandWritingFinished() {
        if (currentCommandEnd >= 0) {
            // firstly need to call onOutputFinished() and then write new commands.
            // We'll repeat an old command if needed
            return;
        }
        currentCommandEnd = getLength() - 1;
    }

    public void onOutputFinished() {
        appendText(inputMark);
        resetCurrentCommandPosition();
        setEditable(true);
    }

    public void onInputFinished() {
        setEditable(false);
        appendText(outputMark);
    }

    private void resetCurrentCommandPosition() {
        currentCommandStart = getLength();
        currentCommandEnd = -1;
    }
}
