import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Course extends ActiveDomainObject {
	private String courseCode;
	private String name;
	private String term;
	private int allowAnonymous;
	private List<Integer> threadIDs = new LinkedList<>();
	private List<Integer> folderIDs = new LinkedList<>();
	private List<String> studentEmails = new LinkedList<>();
	private List<String> instructorEmails = new LinkedList<>();

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

			// Initialize threadIDs
			query = "SELECT ThreadID FROM Thread WHERE CourseCode=(?)";
			st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			rs = st.executeQuery();
			while (rs.next()) {
				threadIDs.add(rs.getInt("ThreadID"));
			}

			// Initialize folderIDs
			query = "SELECT FolderID FROM Folder WHERE CourseCode=(?)";
			st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			rs = st.executeQuery();
			while (rs.next()) {
				folderIDs.add(rs.getInt("FolderID"));
			}

			// Initialize studentEmails
			query = "SELECT Email FROM UserInCourse WHERE CourseCode=(?) AND Role=(?)";
			st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			st.setString(2, "Student");
			rs = st.executeQuery();
			while (rs.next()) {
				studentEmails.add(rs.getString("Email"));
			}

			// Initialize instructorEmails
			st.setString(2, "Instructor");
			rs = st.executeQuery();
			while (rs.next()) {
				instructorEmails.add(rs.getString("Email"));
			}

		} catch (Exception e) {
			System.out.println("db error during initialization of Course " + courseCode);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			String update = "INSERT INTO Course VALUES ((?), (?), (?), (?)) ON DUPLICATE KEY UPDATE Name=(?), Term=(?), AllowAnonymous=(?)";
			PreparedStatement st = conn.prepareStatement(update);
			st.setString(1, courseCode);
			st.setString(2, name);
			st.setString(3, term);
			st.setInt(4, allowAnonymous);
			st.setString(5, name);
			st.setString(6, term);
			st.setInt(7, allowAnonymous);
			st.executeUpdate();
		} catch (Exception e) {
			System.out.println("db error during saving of Course " + courseCode);
		}
	}

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

	/**
	 * Prints all folders in course to console
	 * 
	 * @param conn Connection to the database
	 */
	public void viewFolders(Connection conn) {
		try {
			String query = "SELECT FolderID, Name FROM Folder WHERE CourseCode=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, courseCode);
			ResultSet rs = st.executeQuery();
			System.out.println();
			while (rs.next()) {
				System.out.println("	ID: " + rs.getString("FolderID") + " Name: " + rs.getString("Name"));
			}
		} catch (Exception e) {
			System.out.println("db error while getting Folders for Course " + courseCode);
		}
	}

	public boolean allowsAnonymous() {
		return (allowAnonymous == 1);
	}

	public List<Integer> getThreadIDs() {
		return threadIDs;
	}
}
