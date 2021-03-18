import java.sql.*;

public class Thread extends ActiveDomainObject {
	private int threadID;
	private String courseCode;
	private String content;
	private String email;
	private int folderID;
	private int studAnsID; // ReplyID of type "StudentsAnswer"
	private int instAnsID; // ReplyID of type "InstructorsAnswer"

	public Thread(int threadID, String courseCode) {
		this.threadID = threadID;
		this.courseCode = courseCode;
	}

	public Thread(int threadID, String courseCode, String content, String email, int folderID, int studAnsID,
			int instAnsID) {
		this.threadID = threadID;
		this.courseCode = courseCode;
		this.content = content;
		this.email = email;
		this.folderID = folderID;
		this.studAnsID = studAnsID;
		this.instAnsID = instAnsID;
	}

	@Override
	public void initialize(Connection conn) {
		try {
			// Initialize content, email and folderID
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Content, Email, FolderID FROM Thread WHERE ThreadID=" + threadID
					+ " AND CourseCode=" + courseCode);
			// Initialize studAnsID and instAnsID
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT ReplyID FROM Reply WHERE ThreadID =" + threadID + " AND Type=?");
			pstmt.setString(1, "StudentsAnswer");
			studAnsID = pstmt.executeQuery().getInt("ReplyID");
			pstmt.setString(1, "InstructorsAnswer");
			instAnsID = pstmt.executeQuery().getInt("ReplyID");
		} catch (Exception e) {
			System.out.println("db error during initialization of Thread " + threadID + ", " + courseCode);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Thread VALUES (" + threadID + ", " + courseCode + ", " + content + ", "
					+ email + ", " + folderID + ") ON DUPLICATE KEY UPDATE Content=" + content + ", Email=" + email
					+ ", FolderID=" + folderID);
		} catch (Exception e) {
			System.out.println("db error during saving of Thread " + threadID + ", " + courseCode);
		}
	}

	public int getThreadID() {
		return threadID;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public String getContent() {
		return content;
	}

	public String getEmail() {
		return email;
	}

	public int getFolderID() {
		return folderID;
	}
}
