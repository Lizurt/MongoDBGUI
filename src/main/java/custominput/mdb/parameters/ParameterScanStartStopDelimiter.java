package custominput.mdb.parameters;

import custominput.mdb.ChildDelimiter;

public enum ParameterScanStartStopDelimiter {
    BRACKETS_FORWARD('(', ')'),
    SPACE(' ', '\0'),
    NOTHING('\0', '\0')
    ;

    public final Character SHOULD_START_WITH;
    public final Character SHOULD_END_WITH;

    ParameterScanStartStopDelimiter(Character shouldStartWith, Character shouldEndWith) {
        this.SHOULD_START_WITH = shouldStartWith;
        this.SHOULD_END_WITH = shouldEndWith;
    }

    public static ParameterScanStartStopDelimiter getByStartChar(char delimiterChar) {
        for (ParameterScanStartStopDelimiter startStopDelimiter : ParameterScanStartStopDelimiter.values()) {
            if (startStopDelimiter.SHOULD_START_WITH == delimiterChar) {
                return startStopDelimiter;
            }
        }

        return null;
    }

    public static ParameterScanStartStopDelimiter getByEndChar(char delimiterChar) {
        for (ParameterScanStartStopDelimiter startStopDelimiter : ParameterScanStartStopDelimiter.values()) {
            if (startStopDelimiter.SHOULD_END_WITH == delimiterChar) {
                return startStopDelimiter;
            }
        }

        return null;
    }
}

