package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

import static enums.URL.*;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        initializeControllers();
    }

    private void initializeControllers() {
        controllers.put("/", new HomeController());
        controllers.put(".html", new ForwardController());
        controllers.put(REGISTER_URL.getUrl(), new SignUpController());
        controllers.put(LOGIN_URL.getUrl(), new LoginController());
        controllers.put(USER_LIST_URL.getUrl(), new ListController());
        controllers.put(".css", new ForwardController());
    }

    public void proceed(){
        Controller controller = null;

        // URL 매칭
        if (controllers.containsKey(httpRequest.getUrl())) {
            controller = controllers.get(httpRequest.getUrl());
        }
        if (httpRequest.getUrl().endsWith(".css")) {
            controller = controllers.get(".css");
        }
        if (httpRequest.getUrl().endsWith(".html")) {
            controller = controllers.get(".html");
        }
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
        }
    }

}
