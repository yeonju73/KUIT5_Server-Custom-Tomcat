package webserver;

import db.MemoryUserRepository;
import db.Repository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpHeader.*;
import static enums.HttpHeaderMessage.*;
import static enums.HttpMethod.*;
import static enums.Path.*;
import static enums.QueryKey.*;
import static enums.StatusCode.*;
import static enums.URL.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            //String[] tokens = br.readLine().split(" ");
            HttpRequest httpRequest = HttpRequest.from(br);

            // 요구사항 1: index.html 반환하기
            if (httpRequest.getUrl().equals("/")){
                String filePath = FILE_DIR.getPath() + INDEX_HTML.getPath();

                try {
                    byte[] body = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage());
                    response404Header(dos); // 파일이 없을 경우 404 응답
                }

                return;
            }

            if (httpRequest.getUrl().endsWith(".html")){
                String filePath = FILE_DIR.getPath() + httpRequest.getUrl();

                try {
                    byte[] body = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage());
                    response404Header(dos); // 파일이 없을 경우 404 응답
                }
                return;
            }

            // 요구사항 2: GET 방식으로 회원가입하기
            if (httpRequest.getMethod().equals(GET.toString()) && httpRequest.getUrl().startsWith(REGISTER_URL.getUrl())){

                Map<String, String> userInfoMap = httpRequest.getQueryMap();
                User newUser = new User(userInfoMap.get(USERID.getKey()), userInfoMap.get(PASSWORD.getKey()), userInfoMap.get(NAME.getKey()), userInfoMap.get(EMAIL.getKey()));
                repository.addUser(newUser);

                response302Header(dos, INDEX_HTML.getPath());
                return;
            }

            // 요구사항 3: POST 방식으로 회원가입하기
            if (httpRequest.getMethod().equals(POST.toString()) && httpRequest.getUrl().equals(REGISTER_URL.getUrl())){

                User newUser = new User(httpRequest.getBodyValue(USERID.getKey()), httpRequest.getBodyValue(PASSWORD.getKey()), httpRequest.getBodyValue(NAME.getKey()), httpRequest.getBodyValue(EMAIL.getKey()));
                repository.addUser(newUser);

                response302Header(dos, INDEX_HTML.getPath());
            }

            // 요구사항 5: 로그인하기
            if (httpRequest.getMethod().equals(POST.toString()) && httpRequest.getUrl().equals(LOGIN_URL.getUrl())){

                User findUser = repository.findUserById(httpRequest.getBodyValue(USERID.getKey()));

                if (findUser != null && findUser.getPassword().equals(httpRequest.getBodyValue(PASSWORD.getKey()))){
                    response302Header(dos, INDEX_HTML.getPath(), COOKIE_LOGIN.getMessage());
                    return;
                }
                response302Header(dos, LOGIN_FAILED_HTML.getPath());
            }

            // 요구사항 6: 사용자 목록 출력
            if (httpRequest.getMethod().equals(GET.toString()) && httpRequest.getUrl().equals(USER_LIST_URL.getUrl())){
                String cookie = httpRequest.getCookie();

                if (cookie != null && cookie.contains(COOKIE_LOGIN.getMessage())){

                    byte[] body = Files.readAllBytes(Paths.get(FILE_DIR.getPath() + USER_LIST_HTML.getPath()));
                    response200Header(dos, body.length);
                    responseBody(dos, body);

                    return;
                }

                response302Header(dos, INDEX_HTML.getPath());
            }

            // 요구사항 7: CSS 출력
            if (httpRequest.getUrl().endsWith(".css")){
                String filePath = FILE_DIR.getPath() + httpRequest.getUrl();

                try {
                    byte[] body = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, body.length, CONTENT_TYPE_CSS.getMessage());
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage());
                    response404Header(dos); // 파일이 없을 경우 404 응답
                }
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(OK.getStatus());
            dos.writeBytes(CONTENT_TYPE.getHeader() + CONTENT_TYPE_HTML.getMessage() + "\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader() +  lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 메소드 오버로딩: 같은 클래스 내에서, 같은 이름의 메소드를 매개변수의 타입이나 수가 다르게 설정
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

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(REDIRECT.getStatus());
            dos.writeBytes(LOCATION.getHeader() + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path, String cookie) {
        try {
            dos.writeBytes(REDIRECT.getStatus());
            dos.writeBytes(LOCATION.getHeader() + path + "\r\n");
            dos.writeBytes(SET_COOKIE.getHeader() + cookie + "; HttpOnly; Path=/\r\n");
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