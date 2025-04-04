package webserver;

public class HttpStartLine {
    private final String method;
    private final String url;
    private final String version;

    private HttpStartLine(String method, String url, String version) {
        this.method = method;
        this.url = url;
        this.version = version;
    }

    public static HttpStartLine from(String[] startLine) {
        return new HttpStartLine(startLine[0], startLine[1], startLine[2]);
    }

    public String getMethod() {
        return method;
    }
    public String getUrl() {
        return url;
    }

}
