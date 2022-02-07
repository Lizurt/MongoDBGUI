package custom_input;

import gui.panes.AdvancedCodeArea;
import gui.panes.HBoxIOSection;
import javafx.scene.control.ScrollPane;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import mongodb.ShelledConnection;

public class TerminalIOHandler {
    private VBox vBoxIOList;
    private ShelledConnection shelledConnection;
    private long lastTimeBusyWithOutputting = -1;
    private boolean isBusyWithOutputting = false;
    // time need to pass since last output from stdout to append text
    private int timeBetweenCheckingOutput = 500;
    private AdvancedCodeArea lastFreshOutputSection;
    private AdvancedCodeArea lastFreshInputSection;
    private ScrollPane scrollPane;

    public TerminalIOHandler(ScrollPane scrollPane, VBox vBoxIOList, ShelledConnection shelledConnection) {
        this.vBoxIOList = vBoxIOList;
        this.shelledConnection = shelledConnection;
        this.scrollPane = scrollPane;
        prepareForOutput();
    }

    public void handleInput(KeyEvent keyEvent) {
        if (lastFreshInputSection != keyEvent.getSource()) {
            // we could just remove this handler but meh...
            return;
        }

        if (keyEvent.getCode() == KeyCode.ENTER) {
            // don't worry mrs text area we'll handle the enter key ourselves
            keyEvent.consume();
            // and that's why we can't let a text area append enters by itself - they'll be appended
            // after the method below will be executed. And i don't wanna risk with threads, yes
            finishInput();
        }
    }

    private void startInput() {
        if (((HBoxIOSection) vBoxIOList.getChildren().get(vBoxIOList.getChildren().size() - 1)).isInputSection()) {
            return;
        }

        HBoxIOSection newInputSection = new HBoxIOSection(true);
        newInputSection.getAdvancedCodeArea().setOnKeyPressed(this::handleInput);
        lastFreshInputSection = newInputSection.getAdvancedCodeArea();

        vBoxIOList.getParent().onScrollProperty().bind(lastFreshInputSection.onScrollProperty());
        vBoxIOList.getChildren().add(newInputSection);
        newInputSection.getAdvancedCodeArea().requestFocus();
        scrollPane.setVvalue(1.0);
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
                            lastFreshOutputSection.appendText(stringBuilderWrapper.builder.toString());
                            startInput();
                            synchronized (stringBuilderWrapper) {
                                stringBuilderWrapper.builder = new StringBuilder();
                            }
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
