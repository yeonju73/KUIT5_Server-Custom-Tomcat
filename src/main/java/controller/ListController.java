package controller;

import http.HttpRequest;
import http.HttpResponse;

import static enums.HttpHeaderMessage.COOKIE_LOGIN;
import static enums.Path.INDEX_HTML;
import static enums.Path.USER_LIST_HTML;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String cookie = httpRequest.getCookie();

        if (cookie != null && cookie.contains(COOKIE_LOGIN.getMessage())){
            httpResponse.forward(USER_LIST_HTML.getPath());
            return;
        }

        httpResponse.redirect(INDEX_HTML);
    }
}
