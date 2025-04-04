package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static enums.HttpHeader.CONTENT_LENGTH;
import static enums.HttpHeader.COOKIE;
import static enums.SplitRegex.HEADER_SPLIT;
import static enums.SplitRegex.QUERY_SPLIT;

public class HttpRequest {
    private final HttpStartLine httpStartLine;
    private final Map<String, String> httpHeader;
    private final Map<String, String> httpBody;

    private HttpRequest(HttpStartLine httpStartLine, Map<String, String> httpHeader, Map<String, String> httpBody) {
        this.httpStartLine = httpStartLine;
        this.httpHeader = httpHeader;
        this.httpBody = httpBody;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        HttpStartLine httpStartLine = HttpStartLine.from(reader.readLine().split(" "));

        String headerLine;
        Map<String, String> httpHeader = new HashMap<>();

        while (!(headerLine = reader.readLine()).isEmpty()) {
            String[] header = headerLine.split(HEADER_SPLIT.getRegex());
            httpHeader.put(header[0], header[1]);
        }

        int contentLength = getContentLength(httpHeader);

        String requestBody = IOUtils.readData(reader, contentLength);
        Map<String, String> requestBodyMap = HttpRequestUtils.parseQueryParameter(requestBody);

        return new HttpRequest(httpStartLine, httpHeader, requestBodyMap);
    }

    private static int getContentLength(Map<String, String> httpHeader) {
        String contentLength = httpHeader.get(CONTENT_LENGTH.getSplitHeader());
        if (contentLength != null)
            return Integer.parseInt(contentLength);
        return 0;
    }

    public Map<String, String> getQueryMap() {
        String[] query = this.getUrl().split(QUERY_SPLIT.getRegex());
        return HttpRequestUtils.parseQueryParameter(query[1]);
    }

    public String getMethod(){
        return this.httpStartLine.getMethod();
    }

    public String getUrl(){
        return this.httpStartLine.getUrl();
    }

    public String getCookie(){
        return this.httpHeader.get(COOKIE.getSplitHeader());
    }

    public String getBodyValue(String key){
        return this.httpBody.get(key);
    }
}
