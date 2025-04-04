package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpMethod.*;
import static enums.URL.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private Controller controller = new ForwardController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
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
                controller = new HomeController();
            }

            if (httpRequest.getMethod().equals(GET.toString()) && httpRequest.getUrl().endsWith(".html")){
                controller = new ForwardController();
            }

//            // 요구사항 2: GET 방식으로 회원가입하기
//            if (httpRequest.getMethod().equals(GET.toString()) && httpRequest.getUrl().startsWith(REGISTER_URL.getUrl())){
//
//                Map<String, String> userInfoMap = httpRequest.getQueryMap();
//                User newUser = new User(userInfoMap.get(USERID.getKey()), userInfoMap.get(PASSWORD.getKey()), userInfoMap.get(NAME.getKey()), userInfoMap.get(EMAIL.getKey()));
//                repository.addUser(newUser);
//
//                httpResponse.redirect(INDEX_HTML);
//                return;
//            }

            // 요구사항 3: POST 방식으로 회원가입하기
            if (httpRequest.getMethod().equals(POST.toString()) && httpRequest.getUrl().equals(REGISTER_URL.getUrl())){
                controller = new SignUpController();
            }

            // 요구사항 5: 로그인하기
            if (httpRequest.getMethod().equals(POST.toString()) && httpRequest.getUrl().equals(LOGIN_URL.getUrl())){
                controller = new LoginController();
            }

            // 요구사항 6: 사용자 목록 출력
            if (httpRequest.getMethod().equals(GET.toString()) && httpRequest.getUrl().equals(USER_LIST_URL.getUrl())){
                controller = new ListController();
            }

            // 요구사항 7: CSS 출력
            if (httpRequest.getUrl().endsWith(".css")){
                controller = new ForwardController();
            }

            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

}