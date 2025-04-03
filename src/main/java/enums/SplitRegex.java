package enums;

public enum SplitRegex {
    QUERY_SPLIT("\\?"),
    HEADER_SPLIT(": ");

    private final String regex;

    SplitRegex(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
