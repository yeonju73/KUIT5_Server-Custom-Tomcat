package controller;

import http.HttpRequest;
import http.HttpResponse;

import static enums.Path.INDEX_HTML;

public class HomeController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String path = INDEX_HTML.getPath();
        httpResponse.forward(path);
    }
}
