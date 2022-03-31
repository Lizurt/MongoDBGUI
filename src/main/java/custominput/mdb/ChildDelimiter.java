package custominput.mdb;

public enum ChildDelimiter {
    DOT('.'),
    IGNORED_DELIMITER('\0')
    ;

    public final Character DELIMITER;

    ChildDelimiter(Character DELIMITER) {
        this.DELIMITER = DELIMITER;
    }

    public static ChildDelimiter getByChar(char delimiterChar) {
        for (ChildDelimiter childDelimiter : ChildDelimiter.values()) {
            if (childDelimiter.DELIMITER == delimiterChar) {
                return childDelimiter;
            }
        }

        return null;
    }
}
