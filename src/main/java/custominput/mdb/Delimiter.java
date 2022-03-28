package custominput.mdb;

public enum Delimiter {
    DOT('.'),
    SPACE(' '),
    COMMA(','),
    IGNORED_DELIMITER('\0')
    ;

    public final Character delimiterChar;

    Delimiter(Character delimiterChar) {
        this.delimiterChar = delimiterChar;
    }

    public static Delimiter getByChar(char delimiterChar) {
        for (Delimiter delimiter : Delimiter.values()) {
            if (delimiter.delimiterChar == delimiterChar) {
                return delimiter;
            }
        }

        return null;
    }
}
