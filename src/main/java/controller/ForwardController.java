package controller;

import http.HttpRequest;
import http.HttpResponse;

public class ForwardController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse){
        String path = httpRequest.getUrl();
        httpResponse.forward(path);
    }
}
