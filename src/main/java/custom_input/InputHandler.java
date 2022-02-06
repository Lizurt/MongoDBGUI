package custom_input;

import gui.panes.ShelledTerminal;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import mongodb.ShelledConnection;

public class InputHandler {
    private ShelledTerminal terminal;
    private ShelledConnection shelledConnection;

    public InputHandler(ShelledTerminal terminal, ShelledConnection shelledConnection) {
        this.terminal = terminal;
        this.shelledConnection = shelledConnection;
    }

    public void handleInput(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            // don't worry mrs text area we'll handle the enter key ourselves
            keyEvent.consume();
            terminal.onCommandWritingFinished();
            // and that's why we can't let a text area append enters by itself - they'll be appended
            // after the method below will be executed. And i don't wanna risk with threads, yes
            sendCommandToShell();
            return;
        }
    }

    public void handleInput(String input) {
        // todo: paste event?
    }

    private void sendCommandToShell() {
        // using concatenation for making sure that nothing will be written between 2 input commands.
        // Yes we can use synchronized tag for input method but i'm not sure it's worth it
        System.out.println("execing " + terminal.getNewCommand());
        shelledConnection.input(terminal.getNewCommand() + "\r\n");
        terminal.onCommandSentToShell();
    }
}
