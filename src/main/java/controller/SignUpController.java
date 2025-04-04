package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import static enums.Path.INDEX_HTML;
import static enums.QueryKey.*;
import static enums.QueryKey.EMAIL;

public class SignUpController implements Controller {

    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        User newUser = new User(httpRequest.getBodyValue(USERID.getKey()), httpRequest.getBodyValue(PASSWORD.getKey()), httpRequest.getBodyValue(NAME.getKey()), httpRequest.getBodyValue(EMAIL.getKey()));
        repository.addUser(newUser);

        httpResponse.redirect(INDEX_HTML);
    }
}
