import java.sql.*;
import java.util.LinkedList;

public class Folder extends ActiveDomainObject {
	private int folderID;
	private String courseCode;
	private String name;
	private int parentID;
	private LinkedList<Thread> threads = new LinkedList<>();
	
	public Folder(int folderID, String courseCode) {
		this.folderID = folderID;
		this.courseCode = courseCode;
	}
	
	public Folder(String name, int parentID) {
		this.name = name;
		this.parentID = parentID;
	}
	
	@Override
	public void initialize(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT Name, ParentID FROM Folder WHERE folderID=" + folderID + " AND courseCode=" + courseCode);
			while (rs.next()) {
				name = rs.getString("Name");
				parentID = rs.getInt("ParentID");
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery("");
		} catch (Exception e) {
			System.out.println("db error during initialization of Folder " + folderID + ", " + courseCode);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Folder VALUES (" + folderID + ", " + courseCode + ", " + name
					+ ", " + parentID + ") ON DUPLICATE KEY UPDATE Name=" + name + ", ParentID=" + parentID);
		} catch (Exception e) {
			System.out.println("db error during saving of Folder " + folderID + ", " + courseCode);
		}
	}

}
