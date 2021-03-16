package application.ados;

import java.sql.*;
import application.ActiveDomainObject;

public class Folder extends ActiveDomainObject {
	private int folderID;
	private String courseCode;
	private String Name;
	private int ParentID;
	
	public Folder(int folderID, String courseCode) {
		this.folderID = folderID;
		this.courseCode = courseCode;
	}
	
	@Override
	public void initialize(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT Name, ParentID FROM Folder WHERE folderID=" + folderID + " AND courseCode=" + courseCode);
			while (rs.next()) {
				name = rs.getString("Name");
				term = rs.getString("Term");
				allowAnonymous = rs.getInt("AllowAnonymous");
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Folder " + folderID + ", " + courseCode);
		}
	}

	@Override
	public void save(Connection conn) {
		// TODO Auto-generated method stub
		
	}

}
