package enums;

public enum URL {
    REGISTER_URL("/user/signup"),
    LOGIN_URL("/user/login"),
    USER_LIST_URL("/user/userList");

    private final String url;

    URL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
