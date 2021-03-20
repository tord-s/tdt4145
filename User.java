import java.sql.*;

public class User extends ActiveDomainObject {
	private String email;
	private String name;
	private String password;

	/**
	 * Constructor for a user in the database
	 * 
	 * @param email Primary key
	 */
	public User(String email) {
		this.email = email;
	}

	/**
	 * Constructor for a user not in the database
	 * 
	 * @param email    Primary key
	 * @param name
	 * @param password
	 */
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
			String query = "INSERT INTO Course VALUES ((?), (?), (?)) ON DUPLICATE KEY UPDATE Name=(?), Password(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, email);
			st.setString(2, name);
			st.setString(3, password);
			st.setString(4, name);
			st.setString(5, password);
			st.execute();
		} catch (Exception e) {
			System.out.println("db error during saving of User " + email);
		}
	}

	/**
	 * Checks if a given password matches users password
	 * 
	 * @param p Given password
	 * @return True if password matches
	 */
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
