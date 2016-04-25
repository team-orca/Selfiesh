package sample.google.com.cloudvision;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
	
	private static Connection conn;
	private static Statement st;
	public  Connection retConnection(String myUrl, String username, String pass) throws SQLException{

	conn = DriverManager.getConnection(myUrl, username, pass);
	return conn;
	
	}
	public Statement retStatement(Connection connection) throws SQLException {
		st = connection.createStatement();
		return st;
	}
}