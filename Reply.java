import java.sql.*;

public class Reply extends ActiveDomainObject {
	private int replyID;
	private String email;
	private String content;
	private int threadID;
	private String courseCode;
	private String type;
	
	/**
	 * Constructor for Reply based on primary key
	 * @param replyID
	 */
	public Reply(int replyID) {
		this.replyID = replyID;
	}
	
	/**
	 * Constructor for 
	 * @param replyID
	 * @param email
	 * @param content
	 * @param threadID
	 * @param courseCode
	 * @param type
	 */
	public Reply(int replyID, String email, String content, int threadID, String courseCode, String type) {
		this.replyID = replyID;
		this.email = email;
		this.content = content;
		this.threadID = threadID;
		this.courseCode = courseCode;
		this.type = type;
	}
	
	@Override
	public void initialize(Connection conn) {
		try {
			// Initialize email, content, threadID, courseCode and type
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Email, Content, ThreadID, CourseCode, Type FROM Reply WHERE ReplyID=" + replyID);
			while (rs.next()) {
				email = rs.getString("Email");
				content = rs.getString("Content");
				threadID = rs.getInt("ThreadID");
				courseCode = rs.getString("CourseCode");
				type = rs.getString("String");
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Reply " + replyID);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Reply VALUES (" + replyID+ ", " + email + ", " + content + ", "
					+ threadID + ", " + courseCode + ", " + type + ") ON DUPLICATE KEY UPDATE Email=" + email + ", Content=" + content + ", ThreadID=" + threadID
					+ ", CourseCode=" + courseCode + ", Type=" + type);
		} catch (Exception e) {
			System.out.println("db error during saving of Reply " + replyID);
		}
	}
	
	public int getReplyID() {
		return replyID;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getContent() {
		return content;
	}
	
	public int getThreadID() {
		return threadID;
	}
	
	public String getCourseCode() {
		return courseCode;
	}
	
	public String getType() {
		return type;
	}
}
