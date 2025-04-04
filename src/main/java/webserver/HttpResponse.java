package webserver;

import enums.HttpHeaderMessage;
import enums.Path;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpHeader.*;
import static enums.HttpHeaderMessage.CONTENT_TYPE_CSS;
import static enums.HttpHeaderMessage.CONTENT_TYPE_HTML;
import static enums.Path.FILE_DIR;
import static enums.StatusCode.*;

public class HttpResponse {
    private final DataOutputStream dos;
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());

    public HttpResponse(DataOutputStream dos) {
         this.dos = dos;
    }

    public void forward(String path){
        String filePath = getFilePath(path);
        String contentType = getContentType(filePath);

        try {
            byte[] body = Files.readAllBytes(Paths.get(filePath));
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException e) {
            response404Header(dos); // 파일이 없을 경우 404 응답
        }

    }

    private static String getFilePath(String path) {
        return FILE_DIR.getPath() + path;
    }

    private static String getContentType(String filePath) {
        String contentType = CONTENT_TYPE_HTML.getMessage();
        if (filePath.endsWith(".css")){
            contentType = CONTENT_TYPE_CSS.getMessage();
        }
        return contentType;
    }

    public void redirect(Path path){
        try {
            dos.writeBytes(REDIRECT.getStatus());
            dos.writeBytes(LOCATION.getHeader() + path.getPath() + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(Path path, HttpHeaderMessage cookie){
        try {
            dos.writeBytes(REDIRECT.getStatus());
            dos.writeBytes(LOCATION.getHeader() + path.getPath() + "\r\n");
            dos.writeBytes(SET_COOKIE.getHeader() + cookie.getMessage() + "; HttpOnly; Path=/\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes(OK.getStatus());
            dos.writeBytes(CONTENT_TYPE.getHeader() + contentType + "\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader() + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos) {
        try {
            String responseBody = "<h1>404 Not Found</h1>";
            dos.writeBytes(ERROR.getStatus());
            dos.writeBytes(CONTENT_TYPE.getHeader() + CONTENT_TYPE_HTML.getMessage() + "\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader() + responseBody.length() + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(responseBody);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
