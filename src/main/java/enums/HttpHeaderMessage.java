package enums;

public enum HttpHeaderMessage {
    CONTENT_TYPE_HTML("text/html;charset=utf-8"),
    CONTENT_TYPE_CSS("text/css"),
    COOKIE_LOGIN("logined=true");

    private final String message;

    HttpHeaderMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
