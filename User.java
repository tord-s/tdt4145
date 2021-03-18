import java.sql.*;

public class User extends ActiveDomainObject {
	private String email;
	private String name;
	private String password;

	public User(String email) {
		this.email = email;
	}
	
	public User(String email, String name, String password) {
		this.email = email;
		this.name = name;
		this.password = password;
	}

	@Override
	public void initialize(Connection conn) {
		try {
			// Initialize name and password
			String query = "SELECT Name, Password FROM User WHERE Email=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, email);
			ResultSet rs = st.executeQuery();
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
		if (password == null) {
			return false;
		}
		return password.equals(p);
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getName() {
		return name;
	}

}
