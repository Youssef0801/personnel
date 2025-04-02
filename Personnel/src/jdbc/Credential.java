package jdbc;

public class Credential
{
	private static String driver = "mysql";
	private static String driverClassName = "com.mysql.cj.jdbc.Driver";
	private static String host = "localhost";
	private static String port = "3306";
	private static String database = "M2L";
	private static String user = "root";
	private static String password = "Youssef123&";
	
	static String getUrl() 
	{
		return "jdbc:" + driver + "://" + host + ":" + port + "/" + database ;
	}
	
	static String getDriverClassName()
	{
		return driverClassName;
	}
	
	static String getUser() 
	{
		return user;
	}

	static String getPassword() 
	{
		return password;
	}
}
