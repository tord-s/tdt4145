package application;

import java.sql.*;

public class Test extends DBConn {
	private Statement testStatement;
	
	/*
	 * Printer ut alle tabeller i databasen.
	 */
	public void showTables() {
		try {
			testStatement = conn.createStatement();
			ResultSet rs = testStatement.executeQuery("SHOW TABLES");
			while(rs.next()) {
				System.out.println(rs.getString(1));
			}
		} catch(Exception e) {
			System.out.println("Something went wrong!");
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Test 1:");
		Test test1 = new Test();
		test1.connect();
		test1.showTables();
		test1.closeConnection();
	}
}
