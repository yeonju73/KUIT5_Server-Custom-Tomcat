package enums;

public enum StatusCode {
    OK("HTTP/1.1 200 OK \r\n"),
    REDIRECT("HTTP/1.1 302 Redirect \r\n"),
    ERROR("HTTP/1.1 404 Not Found \r\n");

    private final String status;

    StatusCode(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
