import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Bjørge, Martinus
 * @author Søfteland, Tord Østensen
 * @author Torsvik, Jakob Martin
 *
 */
public class Folder extends ActiveDomainObject {
	private int folderID;
	private String courseCode;
	private String name;
	private int parentID;
	private List<Integer> threadIDs = new LinkedList<>();

	/**
	 * Constructor for a in-database folder
	 * 
	 * @param folderID   Part of primary key
	 * @param courseCode Part of primary key
	 */
	public Folder(int folderID, String courseCode) {
		this.folderID = folderID;
		this.courseCode = courseCode;
	}

	/**
	 * Constructor for a not-in-database folder
	 * 
	 * @param folderID   Part of primary key
	 * @param courseCode Part of primary key
	 * @param name
	 * @param parentID
	 */
	public Folder(int folderID, String courseCode, String name, int parentID) {
		this.folderID = folderID;
		this.courseCode = courseCode;
		this.name = name;
		this.parentID = parentID;
	}

	@Override
	public void initialize(Connection conn) {
		try {
			// Initialize name and parentID
			String query = "SELECT Name, ParentID FROM Folder WHERE FolderID=(?) AND courseCode=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, folderID);
			st.setString(2, courseCode);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				name = rs.getString("Name");
				parentID = rs.getInt("ParentID");
			}

			// Initialize threadIDs
			query = "SELECT ThreadID FROM Thread WHERE FolderID=(?) AND CourseCode=(?)";
			st = conn.prepareStatement(query);
			st.setInt(1, folderID);
			st.setString(2, courseCode);
			rs = st.executeQuery();
			while (rs.next()) {
				threadIDs.add(rs.getInt("ThreadID"));
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Folder " + folderID + ", " + courseCode);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			String update = "INSERT INTO Folder VALUES ((?), (?), (?), (?)) ON DUPLICATE KEY UPDATE Name=(?), ParentID=(?)";
			PreparedStatement st = conn.prepareStatement(update);
			st.setInt(1, folderID);
			st.setString(2, courseCode);
			st.setString(3, name);
			st.setInt(4, parentID);
			st.setString(5, name);
			st.setInt(6, parentID);
			st.executeUpdate();
		} catch (Exception e) {
			System.out.println("db error during saving of Folder " + folderID + ", " + courseCode);
		}
	}

	/**
	 * Prints out all threads in folder to console
	 * @param conn Connection to database
	 */
	public void viewThreads(Connection conn) {
		try {
			String query = "SELECT ThreadID, Email FROM Thread WHERE CourseCode=(?) AND FolderID =(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			st.setInt(2, folderID);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				System.out.print("\n	ID: " + rs.getString("ThreadID") + " User: ");
				if (rs.getString("Email") == null) {
					System.out.print("Anonymous");
				} else {
					System.out.print(rs.getString("Email"));
				}
			}
		} catch (Exception e) {
			System.out.println("db error while getting Threads for Folder " + folderID + ", " + courseCode);
		}
	}

	public String getName() {
		return name;
	}
}
