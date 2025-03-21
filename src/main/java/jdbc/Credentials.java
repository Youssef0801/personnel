package jdbc;

public class Credentials {
    private static final String DRIVER_CLASS_NAME = "";
    private static final String URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";

    public static String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}
