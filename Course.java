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
	private List<String> instructorEmails = new LinkedList<>(); //TO-DO: INITIALIZE! 

	public Course(String courseCode) {
		this.courseCode = courseCode;
	}
	
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
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT Name, Term, AllowAnonymous FROM Course WHERE CourseCode=" + courseCode);
			while (rs.next()) {
				name = rs.getString("Name");
				term = rs.getString("Term");
				allowAnonymous = rs.getInt("AllowAnonymous");
			}
			// Initialize folderIDs
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT FolderID FROM Folder WHERE CourseCode=" + courseCode);
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

	public static LinkedList<String> getCoursesForUser(MainCtrl mainCtrl) {
		LinkedList<String> result = new LinkedList<String>();
		try {
			String query = "SELECT CourseCode FROM UserInCourse WHERE Email=(?)";
			PreparedStatement st = mainCtrl.conn.prepareStatement(query);
			st.setString(1, mainCtrl.getUserEmail());
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("CourseCode"));
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Courses for User " +  mainCtrl.getUserEmail());
		}
		return result;
	}
	
	/**
	 * Checks if course has student
	 * @param studentEmail Email of student
	 * @return A boolean that is true if course has student
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
	 * @param studentEmail Email of instructor
	 * @return A boolean that is true if course has instructor
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

}
