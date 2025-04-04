package enums;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type: "),
    CONTENT_LENGTH("Content-Length: "),
    LOCATION("Location: "),
    SET_COOKIE("Set-Cookie: "),
    COOKIE("Cookie: ");

    private final String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public String getSplitHeader() {
        return header.split(": ")[0];
    }

}
