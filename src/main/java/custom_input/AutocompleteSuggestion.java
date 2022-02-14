package custom_input;

public class AutocompleteSuggestion {
    private String fullSuggestion = "";
    private int matchingSuggestionIndexEnd = -1;
    private int matchingSuggestionIndexStart = -1;

    public AutocompleteSuggestion(
            String fullSuggestion,
            int matchingSuggestionIndexStart,
            int matchingSuggestionIndexEnd
    ) {
        setFullSuggestion(fullSuggestion);
        setMatchingSuggestionIndexes(matchingSuggestionIndexStart, matchingSuggestionIndexEnd);
    }

    public String getFullSuggestion() {
        return fullSuggestion;
    }

    public void setFullSuggestion(String fullSuggestion) {
        this.fullSuggestion = fullSuggestion;
    }

    public int getMatchingSuggestionIndexStart() {
        return matchingSuggestionIndexStart;
    }

    public int getMatchingSuggestionIndexEnd() {
        return matchingSuggestionIndexEnd;
    }

    public void setMatchingSuggestionIndexes(int matchingSuggestionIndexStart, int matchingSuggestionIndexEnd) {
        if (matchingSuggestionIndexStart >= matchingSuggestionIndexEnd || matchingSuggestionIndexStart < 0) {
            throw new IndexOutOfBoundsException();
        }
        this.matchingSuggestionIndexStart = matchingSuggestionIndexStart;
        this.matchingSuggestionIndexEnd = matchingSuggestionIndexEnd;
    }
}
