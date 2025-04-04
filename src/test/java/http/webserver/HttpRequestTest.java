package http.webserver;

import org.junit.jupiter.api.Test;
import http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static enums.HttpHeaderMessage.COOKIE_LOGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {

    private String testDirectory = "src/test/resources/";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    public void testHttpRequestParsing() throws IOException {
        String getPath = "httpRequestTest.txt"; // 테스트할 파일 이름
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + getPath));

        assertEquals("/user/register", httpRequest.getUrl());
        assertEquals("POST", httpRequest.getMethod());
        assertEquals("jw", httpRequest.getBodyValue("userId"));
        assertEquals(COOKIE_LOGIN.getMessage(), httpRequest.getCookie());
    }
}
