package gui.panes;

import com.kodedu.terminalfx.Terminal;
import org.fxmisc.richtext.CodeArea;

public class ShelledTerminal extends CodeArea {
    private int newCommandStart;
    // make sure nobody'll try to get a command that's still being written.
    // Punching with an exception is an effective way to make it
    private int newCommandEnd = -1;

    public String getNewCommand() {

        return getText(newCommandStart, newCommandEnd);
    }

    public void onCommandWritingFinished() {
        newCommandEnd = getLength() - 1;
    }

    public void onOutputFinished() {
        // hey bro, nice crutch, awesome bugs.
        // fixme: a very very temp "soltuion" just to test and finally commit this awful version of terminal emulator
        onCommandSentToShell();
    }

    public void onCommandSentToShell() {
        // sent and ready for a new command, so reset newCommandStart
        newCommandStart = getLength();
        // make sure nobody'll try to get a command that's still being written.
        // Punching with an exception is an effective way to make it
        newCommandEnd = -1;
    }
}
