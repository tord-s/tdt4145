package application.ados;

import java.sql.*;
import application.ActiveDomainObject;

public class User extends ActiveDomainObject {
	private String email;
	private String name;
	private String password;

	public User(String email) {
		this.email = email;
	}

	@Override
	public void initialize(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Name, Password FROM User WHERE Email=" + email);
			while (rs.next()) {
				name = rs.getString("Name");
				password = rs.getString("Password");
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of User " + email);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO User VALUES (" + email + ", " + name + ", " + password
					+ ") ON DUPLICATE KEY UPDATE Name=" + name + ", Password=" + password);
		} catch (Exception e) {
			System.out.println("db error during saving of User " + email);
		}
	}

	public boolean checkPassword(String p) {
		return password.equals(p);
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getName() {
		return name;
	}

}
