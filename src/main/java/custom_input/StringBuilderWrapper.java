package custom_input;

public class StringBuilderWrapper {
    private StringBuilder stringBuilder;

    public StringBuilderWrapper(StringBuilder stringBuilder) {
        setStringBuilder(stringBuilder);
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public void setStringBuilder(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }
}
