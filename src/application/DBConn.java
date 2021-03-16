package application;

import java.sql.*;
import java.util.Properties;

public abstract class DBConn {
	protected Connection conn;

	public void connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Properties p = new Properties();
			p.put("user", "root");
			p.put("password", "toor");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost/prosjekt?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false", p);
		} catch (Exception e) {
			throw new RuntimeException("Unable to connect", e);
		}
	}
	
	public void closeConnection() {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println("Unable to close connection!");
		}
	}
}
