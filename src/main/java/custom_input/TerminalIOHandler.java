package custom_input;

import gui.panes.ShelledTerminal;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import mongodb.ShelledConnection;

public class TerminalIOHandler {
    private ShelledTerminal shelledTerminal;
    private ShelledConnection shelledConnection;
    private long lastTimeBusyWithOutputting = -1;
    private boolean isBusyWithOutputting = false;
    // time need to pass since last output from stdout to append text
    private int timeBetweenCheckingOutput = 500;

    public TerminalIOHandler(ShelledTerminal shelledTerminal, ShelledConnection shelledConnection) {
        this.shelledTerminal = shelledTerminal;
        this.shelledConnection = shelledConnection;
    }

    public void handleInput(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            // don't worry mrs text area we'll handle the enter key ourselves
            keyEvent.consume();
            shelledTerminal.onCommandWritingFinished();
            // and that's why we can't let a text area append enters by itself - they'll be appended
            // after the method below will be executed. And i don't wanna risk with threads, yes
            sendCommandToShell();
            return;
        }
    }

    private void sendCommandToShell() {
        // using concatenation for making sure that nothing will be written between 2 input commands.
        // Yes we can use synchronized tag for input method but i'm not sure it's worth it
        shelledConnection.input(shelledTerminal.getNewCommand() + "\r\n");
        shelledTerminal.onInputFinished();
    }

    public void startStreamReader() {
        shelledConnection.blockThreadUntilReadiness();
        try {
            int amtOfCharsToReadFromBuffer;
            final char[] buffer = new char[1024];
            // we don't wanna create race conditions. This string builder is a good way to prevent it.
            // We use wrapper because of threads
            var stringBuilderWrapper = new Object() {
                StringBuilder builder = new StringBuilder();
            };

            // stderr is already in stdout so no need to read it somewhere else too
            while (
                    (amtOfCharsToReadFromBuffer = shelledConnection.getStdout().read(buffer, 0, buffer.length)) >= 0
            ) {
                lastTimeBusyWithOutputting = System.currentTimeMillis();
                if (!isBusyWithOutputting) {
                    isBusyWithOutputting = true;
                    new Thread(() -> {
                        // while at least N ms haven't passed since last time we got output...
                        while (System.currentTimeMillis() - lastTimeBusyWithOutputting < timeBetweenCheckingOutput) {
                            // ...we're sleeping and waiting the output to finish
                            try {
                                Thread.sleep(timeBetweenCheckingOutput);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // seems output is finished, so probably it's finally safe to let a user to input
                        isBusyWithOutputting = false;
                        Platform.runLater(() -> {
                            shelledTerminal.appendText(stringBuilderWrapper.builder.toString());
                            synchronized (stringBuilderWrapper) {
                                stringBuilderWrapper.builder = new StringBuilder();
                            }
                            shelledTerminal.onOutputFinished();
                        });
                    }).start();
                }
                synchronized (stringBuilderWrapper) {
                    stringBuilderWrapper.builder.append(buffer, 0, amtOfCharsToReadFromBuffer);
                }
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
}
