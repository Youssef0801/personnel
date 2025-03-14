package jdbc;

public class Credentials {
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/user";
    private static final String USER = "myappuser";
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