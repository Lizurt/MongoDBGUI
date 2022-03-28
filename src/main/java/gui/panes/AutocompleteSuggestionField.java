package gui.panes;

import custominput.autocomplete.AutocompleteSuggestion;
import org.fxmisc.richtext.InlineCssTextField;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class AutocompleteSuggestionField extends InlineCssTextField {
    private AutocompleteSuggestion autocompleteSuggestion;
    private StyleSpansBuilder<String> styleSpansBuilder;
    // ehh i can't set .css file's classes and don't know how to read their contents, so...
    private String matchingStyle = "-fx-font-weight: bold;";
    private String notMatchingStyle = "-fx-font-weight: normal;";

    {
        setEditable(false);
    }

    public AutocompleteSuggestionField(
            String fullSuggestion,
            int matchingSuggestionIndexStart,
            int matchingSuggestionIndexEnd
    ) {
        this(new AutocompleteSuggestion(
                fullSuggestion,
                matchingSuggestionIndexStart,
                matchingSuggestionIndexEnd
        ));
    }

    private AutocompleteSuggestionField(AutocompleteSuggestion autocompleteSuggestion) {
        this.autocompleteSuggestion = autocompleteSuggestion;
        setText(getFullSuggestion());
        setMatchingSuggestionIndexes(
                autocompleteSuggestion.getMatchingSuggestionIndexStart(),
                autocompleteSuggestion.getMatchingSuggestionIndexEnd()
        );
    }

    public String getFullSuggestion() {
        return autocompleteSuggestion.getFullSuggestion();
    }

    public void setFullSuggestion(String fullSuggestion) {
        autocompleteSuggestion.setFullSuggestion(fullSuggestion);
    }

    public int getMatchingSuggestionIndexStart() {
        return autocompleteSuggestion.getMatchingSuggestionIndexStart();
    }

    public int getMatchingSuggestionIndexEnd() {
        return autocompleteSuggestion.getMatchingSuggestionIndexEnd();
    }

    public void setMatchingSuggestionIndexes(int matchingSuggestionIndexStart, int matchingSuggestionIndexEnd) {
        autocompleteSuggestion.setMatchingSuggestionIndexes(matchingSuggestionIndexStart, matchingSuggestionIndexEnd);
        setStyle(0, matchingSuggestionIndexStart, notMatchingStyle);
        setStyle(matchingSuggestionIndexStart, matchingSuggestionIndexEnd, matchingStyle);
        setStyle(matchingSuggestionIndexEnd, getFullSuggestion().length(), notMatchingStyle);
    }
}
