package custom_input;

import gui.panes.AutocompleteSuggestionField;
import gui.panes.TerminalInputArea;
import gui.panes.HBoxIOSection;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Pair;
import mongodb.ShelledConnection;

public class TerminalIOHandler {
    private VBox vBoxIOList;
    private ShelledConnection shelledConnection;

    private long lastTimeBusyWithOutputting = -1;
    // time need to pass since last output from stdout to append text
    private int timeBetweenCheckingOutput = 500;

    private TerminalInputArea lastFreshOutputSection;
    private TerminalInputArea lastFreshInputSection;

    // we don't wanna create race conditions. This string builder is a good way to prevent it.
    // We use wrapper because of threads
    private final StringBuilderWrapper stringBuilderWrapper = new StringBuilderWrapper(new StringBuilder());
    private Thread waitingForOutputToFinishThread;

    private Popup popupAutocomplete = new Popup();
    private ListView<AutocompleteSuggestionField> listViewAutocomplete = new ListView<>();

    {
        popupAutocomplete.setAutoHide(true);
        popupAutocomplete.setHideOnEscape(true);
        popupAutocomplete.getContent().add(listViewAutocomplete);
    }

    public TerminalIOHandler(VBox vBoxIOList, ShelledConnection shelledConnection) {
        this.vBoxIOList = vBoxIOList;
        this.shelledConnection = shelledConnection;
        prepareForOutput();
    }

    private void handlePressed(KeyEvent keyEvent) {
        if (lastFreshInputSection != keyEvent.getSource()) {
            // we could just remove this handler but meh...
            return;
        }

        if (keyEvent.getCode() == KeyCode.ENTER) {
            keyEvent.consume();
            finishInput();
            return;
        }
    }

    private void handleTyped(KeyEvent keyEvent) {

    }

    private void handleReleased(KeyEvent keyEvent) {

    }

    private void startInput() {
        if (((HBoxIOSection) vBoxIOList.getChildren().get(vBoxIOList.getChildren().size() - 1)).isInputSection()) {
            return;
        }

        HBoxIOSection newInputSection = new HBoxIOSection(true);
        newInputSection.getTerminalInputArea().addEventFilter(KeyEvent.KEY_PRESSED, this::handlePressed);
        newInputSection.getTerminalInputArea().addEventFilter(KeyEvent.KEY_TYPED, this::handleTyped);
        newInputSection.getTerminalInputArea().addEventFilter(KeyEvent.KEY_RELEASED, this::handleReleased);
        lastFreshInputSection = newInputSection.getTerminalInputArea();
        addAutocomplete(lastFreshInputSection);

        vBoxIOList.getParent().onScrollProperty().bind(lastFreshInputSection.onScrollProperty());
        vBoxIOList.getChildren().add(newInputSection);
        newInputSection.getTerminalInputArea().requestFocus();
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
        lastFreshOutputSection = newOutputSection.getTerminalInputArea();
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
        } catch (final Exception e) {
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

    private void addAutocomplete(TerminalInputArea terminalInputArea) {
        terminalInputArea.textProperty().addListener((observableVal, oldVal, newVal) -> {
            if (newVal.length() < oldVal.length()) {
                // we're erasing. No need to handle keywords
                hideAndClearPopup();
                return;
            }
            int indexCurrWordEnd = terminalInputArea.getAnchor();
            int indexCurrWordStart = indexCurrWordEnd;
            for (int i = indexCurrWordEnd; i >= 0; i--) {
                if (newVal.charAt(i) == '\n' || newVal.charAt(i) == ' ') {
                    break;
                }
                indexCurrWordStart--;
            }

            String currWord = newVal.substring(indexCurrWordStart + 1, indexCurrWordEnd + 1);

            if (currWord.isEmpty() || currWord.isBlank()) {
                hideAndClearPopup();
                return;
            }

            hideAndClearPopup();
            for (String keyword : TerminalInputArea.KEYWORDS) {
                if (keyword.length() < currWord.length()) {
                    continue;
                }
                Pair<Integer, Integer> matchingKeywordIndexes = getMatchingKeywordIndexes(currWord, keyword);
                if (matchingKeywordIndexes == null) {
                    continue;
                }
                AutocompleteSuggestionField autocompleteSuggestionField = new AutocompleteSuggestionField(
                        keyword,
                        matchingKeywordIndexes.getKey(),
                        matchingKeywordIndexes.getValue()
                );

                listViewAutocomplete.getItems().add(autocompleteSuggestionField);
                listViewAutocomplete.setOnKeyPressed(keyEvent -> {
                    if (listViewAutocomplete.getSelectionModel().getSelectedItem() == null) {
                        return;
                    }
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        AutocompleteSuggestionField selectedSuggestionField =
                                listViewAutocomplete.getSelectionModel().getSelectedItem();
                        int caretPos = lastFreshInputSection.getCaretPosition();
                        int matchingLength =
                                selectedSuggestionField.getMatchingSuggestionIndexEnd() -
                                selectedSuggestionField.getMatchingSuggestionIndexStart();
                        lastFreshInputSection.replaceText(
                                caretPos - matchingLength,
                                caretPos,
                                selectedSuggestionField.getFullSuggestion()
                        );
                        hideAndClearPopup();
                    }
                });
            }

            if (listViewAutocomplete.getItems().size() == 0) {
                return;
            }

            listViewAutocomplete.setMaxHeight(80);
            popupAutocomplete.show(
                    terminalInputArea,
                    terminalInputArea.getCaretBounds().isPresent()
                            ? terminalInputArea.getCaretBounds().get().getMaxX()
                            : 0,
                    terminalInputArea.getCaretBounds().isPresent()
                            ? terminalInputArea.getCaretBounds().get().getMaxY()
                            : 0
            );

            terminalInputArea.requestFocus();
        });
    }

    private Pair<Integer, Integer> getMatchingKeywordIndexes(String word, String keyword) {
        if (word.length() > keyword.length()) {
            return null;
        }
        word = word.toLowerCase();
        keyword = keyword.toLowerCase();

        outer:
        for (int i = 0; i < keyword.length(); i++) {
            if (i + word.length() > keyword.length()) {
                // we've reached the end of the word. No matches possible
                return null;
            }
            for (int j = 0; j < word.length(); j++) {
                if (keyword.charAt(i + j) != word.charAt(j)) {
                    continue outer;
                }
            }
            return new Pair<>(i, i + word.length());
        }
        return null;
    }

    private void hideAndClearPopup() {
        popupAutocomplete.hide();
        listViewAutocomplete.getItems().clear();
    }
}
