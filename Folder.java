import java.sql.*;
import java.util.LinkedList;
import java.util.List;

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
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Folder VALUES (" + folderID + ", " + courseCode + ", " + name + ", "
					+ parentID + ") ON DUPLICATE KEY UPDATE Name=" + name + ", ParentID=" + parentID);
		} catch (Exception e) {
			System.out.println("db error during saving of Folder " + folderID + ", " + courseCode);
		}
	}

	public int getFolderID() {
		return folderID;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public String getName() {
		return name;
	}

	public int getParentID() {
		return parentID;
	}

	public List<Integer> getThreadIDs() {
		return threadIDs;
	}

}
