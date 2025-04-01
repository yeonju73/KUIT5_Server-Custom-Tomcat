package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String[] tokens = br.readLine().split(" ");

            if (tokens[1].equals("/")){
                tokens[1] = "/index.html";
                System.out.println(tokens[1]);
            }

            if (tokens[1].startsWith("/user/signup")){
                String[] query = tokens[1].split("\\?");

                System.out.println(query[1]);
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(query[1]);

                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                User newUser = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                memoryUserRepository.addUser(newUser);

                response302Header(dos, "/index.html");
                return;
            }

            if (tokens[1].endsWith(".html")){
                String filePath = "webapp" + tokens[1];

                try {
                    byte[] body = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, body.length);
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
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos) {
        try {
            String responseBody = "<h1>404 Not Found</h1>";
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + responseBody.length() + "\r\n");
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