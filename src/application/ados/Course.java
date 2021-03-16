package application.ados;

import java.sql.*;

import application.ActiveDomainObject;

public class Course extends ActiveDomainObject {
	private String courseCode;
	private String name;
	private String term;
	private int allowAnonymous;

	public Course(String courseCode) {
		this.courseCode = courseCode;
	}

	@Override
	public void initialize(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT Name, Term, AllowAnonymous FROM Course WHERE CourseCode=" + courseCode);
			while (rs.next()) {
				name = rs.getString("Name");
				term = rs.getString("Term");
				allowAnonymous = rs.getInt("AllowAnonymous");
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Course " + courseCode);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Course VALUES (" + courseCode + ", " + name + ", " + term + ", "
					+ allowAnonymous + ") ON DUPLICATE KEY UPDATE Name=" + name + ", Term=" + term + ", AllowAnonymous="
					+ allowAnonymous);
		} catch (Exception e) {
			System.out.println("db error during saving of Course " + courseCode);
		}
	}
	
	public String getCourseCode() {
		return courseCode;
	}
	
	public String getTerm() {
		return term;
	}
	
	public int allowsAnonymous() {
		return allowAnonymous;
	}

}
