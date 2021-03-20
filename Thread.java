import java.sql.*;

public class Thread extends ActiveDomainObject {
	private int threadID;
	private String courseCode;
	private String content;
	private String email;
	private int folderID;
	private int studAnsID; // ReplyID of type "StudentsAnswer"
	private int instAnsID; // ReplyID of type "InstructorsAnswer"

	/**
	 * Constructor for a thread in the database
	 * 
	 * @param threadID   Part of primary key
	 * @param courseCode Part of primary key
	 */
	public Thread(int threadID, String courseCode) {
		this.threadID = threadID;
		this.courseCode = courseCode;
	}

	/**
	 * Constructor for a thread not in the database
	 * 
	 * @param threadID   Part of primary key
	 * @param courseCode Part of primary key
	 * @param content
	 * @param email
	 * @param folderID
	 * @param studAnsID
	 * @param instAnsID
	 */
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
			String query = "SELECT Content, Email, FolderID FROM Thread WHERE ThreadID=(?) AND CourseCode=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, threadID);
			st.setString(2, courseCode);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				content = rs.getString("Content");
				email = rs.getString("Email");
				folderID = rs.getInt("FolderID");
			}
			
			// Initialize studAnsID and instAnsID
			query = "SELECT ReplyID FROM Reply WHERE ThreadID=(?) AND Type=(?)";
			st = conn.prepareStatement(query);
			st.setInt(1, threadID);
			st.setString(2, "StudentsAnswer");
			studAnsID = st.executeQuery().getInt("ReplyID");
			/*
			 * st = conn.prepareStatement(query); // Usikker på om dette trengs st.setInt(1,
			 * threadID);
			 */
			st.setString(2, "InstructorsAnswer");
			instAnsID = st.executeQuery().getInt("ReplyID");
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

	public int getStudAnsID() {
		return studAnsID;
	}

	public int getInstAnsID() {
		return instAnsID;
	}
}
