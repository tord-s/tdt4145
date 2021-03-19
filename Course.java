import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Course extends ActiveDomainObject {
	private String courseCode;
	private String name;
	private String term;
	private int allowAnonymous;
	private List<Integer> folderIDs = new LinkedList<>();
	private List<String> studentEmails = new LinkedList<>(); // TO-DO: INITIALIZE!
	private List<String> instructorEmails = new LinkedList<>(); // TO-DO: INITIALIZE!

	/**
	 * Constructor for a in-database course
	 * 
	 * @param courseCode Primary key
	 */
	public Course(String courseCode) {
		this.courseCode = courseCode;
	}

	/**
	 * Constructor for a not-in-database course
	 * 
	 * @param courseCode     Primary key
	 * @param name
	 * @param term
	 * @param allowAnonymous
	 */
	public Course(String courseCode, String name, String term, int allowAnonymous) {
		this.courseCode = courseCode;
		this.name = name;
		this.term = term;
		this.allowAnonymous = allowAnonymous;
	}

	// How to use prepeared statements:
	// String query = "SELECT Name FROM Folder WHERE CourseCode=(?)";
	// PreparedStatement st = mainCtrl.conn.prepareStatement(query);
	// st.setString(1, courseCode);
	// ResultSet rs = st.executeQuery();

	@Override
	public void initialize(Connection conn) {
		try {
			// Initialize name, term and allowAnonymous
			String query = "SELECT Name, Term, AllowAnonymous FROM Course WHERE CourseCode=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				name = rs.getString("Name");
				term = rs.getString("Term");
				allowAnonymous = rs.getInt("AllowAnonymous");
			}
			// Initialize folderIDs
			query = "SELECT FolderID FROM Folder WHERE CourseCode=(?)";
			st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			rs = st.executeQuery();
			while (rs.next()) {
				folderIDs.add(rs.getInt("FolderID"));
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

	/*
	 * public static List<String> getCoursesForUser(MainCtrl mainCtrl) {
	 * List<String> result = new LinkedList<String>(); try { String query =
	 * "SELECT CourseCode FROM UserInCourse WHERE Email=(?)"; PreparedStatement st =
	 * mainCtrl.conn.prepareStatement(query); st.setString(1,
	 * mainCtrl.getUserEmail()); ResultSet rs = st.executeQuery(); while (rs.next())
	 * { result.add(rs.getString("CourseCode")); } } catch (Exception e) {
	 * System.out.println("db error during initialization of Courses for User " +
	 * mainCtrl.getUserEmail()); } return result; }
	 */

	/**
	 * Checks if course has student
	 * 
	 * @param studentEmail Email of student
	 * @return True if course has student
	 */
	public boolean hasStudent(String studentEmail) {
		boolean result = false;
		for (String email : studentEmails) {
			if (email.equals(studentEmail)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Checks if course has instructor
	 * 
	 * @param studentEmail Email of instructor
	 * @return True if course has instructor
	 */
	public boolean hasInstructor(String instructorEmail) {
		boolean result = false;
		for (String email : instructorEmails) {
			if (email.equals(instructorEmail)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public String getName() {
		return name;
	}

	public String getTerm() {
		return term;
	}

	public int allowsAnonymous() {
		return allowAnonymous;
	}

	// Endret denne fra � ha MainCtrl som parameter til � ha en Connection for �
	// v�re mer konsekvent
	public List<String> getFolders(Connection conn) {
		List<String> result = new LinkedList<String>();
		try {
			String query = "SELECT Name, FolderID FROM Folder WHERE CourseCode=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				result.add("ID: " + rs.getString("FolderID") + " Name: " + rs.getString("Name"));
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Folders for Course " + courseCode);
		}
		return result;
	}

}
