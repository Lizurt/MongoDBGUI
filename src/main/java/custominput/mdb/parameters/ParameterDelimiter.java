package custominput.mdb.parameters;

import custominput.mdb.ChildDelimiter;

public enum ParameterDelimiter {
    SPACE(' '),
    COMMA(','),
    NOTHING('\0')
    ;

    public final Character DELIMITER;

    ParameterDelimiter(Character delimiter) {
        this.DELIMITER = delimiter;
    }

    public static ParameterDelimiter getByChar(char delimiterChar) {
        for (ParameterDelimiter parameterDelimiter : ParameterDelimiter.values()) {
            if (parameterDelimiter.DELIMITER == delimiterChar) {
                return parameterDelimiter;
            }
        }

        return null;
    }
}
