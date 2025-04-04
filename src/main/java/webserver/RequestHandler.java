package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpHeaderMessage.*;
import static enums.HttpMethod.*;
import static enums.Path.*;
import static enums.QueryKey.*;
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

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            // 요구사항 1: index.html 반환하기
            if (httpRequest.getUrl().equals("/")){
                String path = INDEX_HTML.getPath();
                httpResponse.forward(path);
                return;
            }

            if (httpRequest.getUrl().endsWith(".html")){
                String path = httpRequest.getUrl();
                httpResponse.forward(path);
                return;
            }

            // 요구사항 2: GET 방식으로 회원가입하기
            if (httpRequest.getMethod().equals(GET.toString()) && httpRequest.getUrl().startsWith(REGISTER_URL.getUrl())){

                Map<String, String> userInfoMap = httpRequest.getQueryMap();
                User newUser = new User(userInfoMap.get(USERID.getKey()), userInfoMap.get(PASSWORD.getKey()), userInfoMap.get(NAME.getKey()), userInfoMap.get(EMAIL.getKey()));
                repository.addUser(newUser);

                httpResponse.redirect(INDEX_HTML);
                return;
            }

            // 요구사항 3: POST 방식으로 회원가입하기
            if (httpRequest.getMethod().equals(POST.toString()) && httpRequest.getUrl().equals(REGISTER_URL.getUrl())){

                User newUser = new User(httpRequest.getBodyValue(USERID.getKey()), httpRequest.getBodyValue(PASSWORD.getKey()), httpRequest.getBodyValue(NAME.getKey()), httpRequest.getBodyValue(EMAIL.getKey()));
                repository.addUser(newUser);

                httpResponse.redirect(INDEX_HTML);
            }

            // 요구사항 5: 로그인하기
            if (httpRequest.getMethod().equals(POST.toString()) && httpRequest.getUrl().equals(LOGIN_URL.getUrl())){

                User findUser = repository.findUserById(httpRequest.getBodyValue(USERID.getKey()));

                if (findUser != null && findUser.getPassword().equals(httpRequest.getBodyValue(PASSWORD.getKey()))){
                    httpResponse.redirect(INDEX_HTML, COOKIE_LOGIN);
                    return;
                }
                httpResponse.redirect(LOGIN_FAILED_HTML);
            }

            // 요구사항 6: 사용자 목록 출력
            if (httpRequest.getMethod().equals(GET.toString()) && httpRequest.getUrl().equals(USER_LIST_URL.getUrl())){
                String cookie = httpRequest.getCookie();

                if (cookie != null && cookie.contains(COOKIE_LOGIN.getMessage())){
                    httpResponse.forward(USER_LIST_HTML.getPath());
                    return;
                }

                httpResponse.redirect(INDEX_HTML);
            }

            // 요구사항 7: CSS 출력
            if (httpRequest.getUrl().endsWith(".css")){
                String path = httpRequest.getUrl();
                httpResponse.forward(path);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}