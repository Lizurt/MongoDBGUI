package custom_input;

import gui.panes.AdvancedCodeArea;
import gui.panes.HBoxIOSection;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import mongodb.ShelledConnection;

public class TerminalIOHandler {
    private VBox vBoxIOList;
    private ShelledConnection shelledConnection;
    private long lastTimeBusyWithOutputting = -1;
    // time need to pass since last output from stdout to append text
    private int timeBetweenCheckingOutput = 500;
    private AdvancedCodeArea lastFreshOutputSection;
    private AdvancedCodeArea lastFreshInputSection;

    // we don't wanna create race conditions. This string builder is a good way to prevent it.
    // We use wrapper because of threads
    private final StringBuilderWrapper stringBuilderWrapper = new StringBuilderWrapper(new StringBuilder());
    private Thread waitingForOutputToFinishThread;

    public TerminalIOHandler(VBox vBoxIOList, ShelledConnection shelledConnection) {
        this.vBoxIOList = vBoxIOList;
        this.shelledConnection = shelledConnection;
        prepareForOutput();
    }

    public void handlePressed(KeyEvent keyEvent) {
        if (lastFreshInputSection != keyEvent.getSource()) {
            // we could just remove this handler but meh...
            return;
        }

        if (keyEvent.getCode() == KeyCode.ENTER) {
            finishInput();
        }
    }

    private void startInput() {
        if (((HBoxIOSection) vBoxIOList.getChildren().get(vBoxIOList.getChildren().size() - 1)).isInputSection()) {
            return;
        }

        HBoxIOSection newInputSection = new HBoxIOSection(true);
        newInputSection.getAdvancedCodeArea().addEventHandler(KeyEvent.KEY_PRESSED, this::handlePressed);
        lastFreshInputSection = newInputSection.getAdvancedCodeArea();

        vBoxIOList.getParent().onScrollProperty().bind(lastFreshInputSection.onScrollProperty());
        vBoxIOList.getChildren().add(newInputSection);
        newInputSection.getAdvancedCodeArea().requestFocus();
    }

    private void finishInput() {
        lastFreshInputSection.setEditable(false);
        prepareForOutput();
        // using concatenation for making sure that nothing will be written between 2 input commands.
        // Yes we can use synchronized tag for input method but i'm not sure it's worth it
        shelledConnection.input(lastFreshInputSection.getText() + "\r\n");
    }

    private void prepareForOutput() {
        HBoxIOSection newOutputSection = new HBoxIOSection(false);
        lastFreshOutputSection = newOutputSection.getAdvancedCodeArea();
        vBoxIOList.getParent().onScrollProperty().bind(lastFreshOutputSection.onScrollProperty());
        vBoxIOList.getChildren().add(newOutputSection);
        startWaitingOutputFinishedThread();
    }

    public void startStreamReader() {
        shelledConnection.blockThreadUntilReadiness();
        try {
            int amtOfCharsToReadFromBuffer;
            final char[] buffer = new char[1024];

            // stderr is already in stdout so no need to read it somewhere else too
            while (
                    (amtOfCharsToReadFromBuffer = shelledConnection.getStdout().read(buffer, 0, buffer.length)) >= 0
            ) {
                lastTimeBusyWithOutputting = System.currentTimeMillis();
                if (!waitingForOutputToFinishThread.isAlive()) {
                    // oops we were outputting one command too long and our thread is dead. Have to start a new one
                    startWaitingOutputFinishedThread();
                }
                synchronized (stringBuilderWrapper) {
                    stringBuilderWrapper.getStringBuilder().append(buffer, 0, amtOfCharsToReadFromBuffer);
                }
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }

    private void startWaitingOutputFinishedThread() {
        if (waitingForOutputToFinishThread != null && waitingForOutputToFinishThread.isAlive()) {
            // it's already started we don't need a new one
            return;
        }

        waitingForOutputToFinishThread = new Thread(() -> {
            // making sure we'll wait at least N ms before allowing input
            lastTimeBusyWithOutputting = System.currentTimeMillis();
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
            Platform.runLater(() -> {
                lastFreshOutputSection.appendText(stringBuilderWrapper.getStringBuilder().toString());
                startInput();
                synchronized (stringBuilderWrapper) {
                    stringBuilderWrapper.setStringBuilder(new StringBuilder());
                }
            });
        });
        waitingForOutputToFinishThread.start();
    }
}
