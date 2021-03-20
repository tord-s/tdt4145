import java.sql.*;

public class Reply extends ActiveDomainObject {
	private int replyID;
	private String email;
	private String content;
	private int threadID;
	private String courseCode;
	private String type;

	/**
	 * Constructor for a reply in the database
	 * 
	 * @param replyID Primary key
	 */
	public Reply(int replyID) {
		this.replyID = replyID;
	}

	/**
	 * Constructor for a reply not in the database
	 * 
	 * @param replyID    Primary key
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
			String query = "SELECT Email, Content, ThreadID, CourseCode, Type FROM Reply WHERE ReplyID=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, replyID);
			ResultSet rs = st.executeQuery();
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
			String query = "INSERT INTO Reply VALUES ((?), (?), (?), (?), (?), (?)) ON DUPLICATE KEY UPDATE Email=(?), Content=(?), ThreadID=(?), CourseCode=(?), Type=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, replyID);
			st.setString(2, email);
			st.setString(3, content);
			st.setInt(4, threadID);
			st.setString(5, courseCode);
			st.setString(6, type);
			st.setString(7, email);
			st.setString(8, content);
			st.setInt(9, threadID);
			st.setString(10, courseCode);
			st.setString(11, type);
			st.executeUpdate();
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
