package custominput.mdb.parameters;

public enum ParameterSearchPlace {
    NOWHERE(null, null),
    BRACKETS_FORWARD('(', ')'),
    SPACED_PREVIOUS_WORD(' ', '\0'),
    SPACED_NEXT_WORD(' ', '\0'),
    COMMAND_AS_PARAMETER(null, null);

    public final Character parametersShouldStartWith;
    public final Character parametersShouldEndWith;

    ParameterSearchPlace(Character parametersShouldStartWith, Character parametersShouldEndWith) {
        this.parametersShouldStartWith = parametersShouldStartWith;
        this.parametersShouldEndWith = parametersShouldEndWith;
    }
}
