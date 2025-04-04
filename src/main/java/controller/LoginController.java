package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import static enums.HttpHeaderMessage.COOKIE_LOGIN;
import static enums.Path.INDEX_HTML;
import static enums.Path.LOGIN_FAILED_HTML;
import static enums.QueryKey.PASSWORD;
import static enums.QueryKey.USERID;

public class LoginController implements Controller {

    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        User findUser = repository.findUserById(httpRequest.getBodyValue(USERID.getKey()));

        if (findUser != null && findUser.getPassword().equals(httpRequest.getBodyValue(PASSWORD.getKey()))){
            httpResponse.redirect(INDEX_HTML, COOKIE_LOGIN);
            return;
        }

        httpResponse.redirect(LOGIN_FAILED_HTML);
    }
}
