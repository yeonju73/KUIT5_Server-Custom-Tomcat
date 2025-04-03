package enums;

public enum Path {
    FILE_DIR("webapp"),
    INDEX_HTML("/index.html"),
    LOGIN_FAILED_HTML("/user/logined_failed.html"),
    USER_LIST_HTML("/user/list.html");

    private final String path;

    Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
