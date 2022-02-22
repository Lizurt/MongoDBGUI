package gui.panes;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.util.Pair;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedCodeArea extends CodeArea {
    // ===== KEYWORDS =====
    public static final String[] KEYWORDS = new String[]{
            // todo: more keywords
            "aggregate",
            "count",
            "distinct",
            "mapReduce",
            "geoSearch",
            "delete",
            "find",
            "findAndModify",
            "getLastError",
            "getMore",
            "insert",
            "resetError",
            "update"
    };
    // ===== HIGHLIGHTING PATTERNS =====
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[\\]]";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
    );
    // ===== AUTOADD CHARS =====
    private static final Map<String, String> PAIRED_CHARS = Map.of(
            "\"", "\"",
            "[", "]",
            "(", ")",
            "{", "}"
    );
    // ===== AUTOCOMPLETE STUFF =====
    private Popup popupAutocomplete;
    private ListView<AutocompleteSuggestionField> listViewAutocomplete;

    {
        getVisibleParagraphs().addModificationObserver(
                new VisibleParagraphStyler<>(this, this::computeHighlighting)
        );

        addEventHandler(KeyEvent.KEY_TYPED, keyEvent -> {
            if (PAIRED_CHARS.containsKey(keyEvent.getCharacter())) {
                int caretPosition = getCaretPosition();
                insertText(caretPosition, PAIRED_CHARS.get(keyEvent.getCharacter()));
                moveTo(caretPosition);
            }
        });
    }

    private void hideAndClearPopup() {
        popupAutocomplete.hide();
        listViewAutocomplete.getItems().clear();
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

    public void initAutocomplete() {
        if (popupAutocomplete != null && listViewAutocomplete != null) {
            return;
        }

        popupAutocomplete = new Popup();
        listViewAutocomplete = new ListView<>();

        popupAutocomplete.setAutoHide(true);
        popupAutocomplete.setHideOnEscape(true);
        popupAutocomplete.getContent().add(listViewAutocomplete);

        textProperty().addListener((observableVal, oldVal, newVal) -> {
            if (newVal.length() < oldVal.length()) {
                // we're erasing. No need to handle keywords
                hideAndClearPopup();
                return;
            }
            int indexCurrWordEnd = getAnchor();
            int indexCurrWordStart = indexCurrWordEnd;
            for (int i = indexCurrWordEnd; i >= 0; i--) {
                if (newVal.charAt(i) == '\n' || newVal.charAt(i) == ' ') {
                    break;
                }
                indexCurrWordStart--;
            }

            String currWord = newVal.substring(indexCurrWordStart + 1, indexCurrWordEnd + 1);

            hideAndClearPopup();
            if (currWord.isEmpty() || currWord.isBlank()) {
                return;
            }

            for (String keyword : AdvancedCodeArea.KEYWORDS) {
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
                        int caretPos = getCaretPosition();
                        int matchingLength =
                                selectedSuggestionField.getMatchingSuggestionIndexEnd() -
                                        selectedSuggestionField.getMatchingSuggestionIndexStart();
                        replaceText(
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
                    this,
                    getCaretBounds().isPresent()
                            ? getCaretBounds().get().getMaxX()
                            : 0,
                    getCaretBounds().isPresent()
                            ? getCaretBounds().get().getMaxY()
                            : 0
            );

            requestFocus();
        });
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("STRING") != null ? "string" :
                                                            null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private static class VisibleParagraphStyler<PS, SEG, S> implements Consumer<ListModification<? extends Paragraph<PS, SEG, S>>> {
        private final GenericStyledArea<PS, SEG, S> area;
        private final Function<String, StyleSpans<S>> computeStyles;
        private int prevParagraph, prevTextLength;

        public VisibleParagraphStyler(GenericStyledArea<PS, SEG, S> area, Function<String, StyleSpans<S>> computeStyles) {
            this.computeStyles = computeStyles;
            this.area = area;
        }

        @Override
        public void accept(ListModification<? extends Paragraph<PS, SEG, S>> lm) {
            if (lm.getAddedSize() > 0) {
                int paragraph = Math.min(
                        area.firstVisibleParToAllParIndex() + lm.getFrom(),
                        area.getParagraphs().size() - 1
                );
                String text = area.getText(paragraph, 0, paragraph, area.getParagraphLength(paragraph));

                if (paragraph != prevParagraph || text.length() != prevTextLength) {
                    int startPos = area.getAbsolutePosition(paragraph, 0);
                    Platform.runLater(() -> area.setStyleSpans(startPos, computeStyles.apply(text)));
                    prevTextLength = text.length();
                    prevParagraph = paragraph;
                }
            }
        }
    }
}
