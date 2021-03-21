import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Thread extends ActiveDomainObject {
	private int threadID;
	private String courseCode;
	private String content;
	private String email;
	private int folderID;
	private int studAnsID; // ReplyID of type "StudentsAnswer"
	private int instAnsID; // ReplyID of type "InstructorsAnswer"
	private List<String> tags = new LinkedList<>();

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
	public Thread(int threadID, String courseCode, String content, String email, int folderID, List<String> tags) {
		this.threadID = threadID;
		this.courseCode = courseCode;
		this.content = content;
		this.email = email;
		this.folderID = folderID;
		this.tags = tags;
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
			query = "SELECT ReplyID FROM Reply WHERE ThreadID=(?) AND CourseCode=(?) AND Type=(?)";
			st = conn.prepareStatement(query);
			st.setInt(1, threadID);
			st.setString(2, courseCode);
			st.setString(3, "StudentsAnswer");
			rs = st.executeQuery();
			rs.next();
			studAnsID = rs.getInt("ReplyID");
			st.setString(3, "InstructorsAnswer");
			rs = st.executeQuery();
			rs.next();
			instAnsID = rs.getInt("ReplyID");
			
			// Initialize tags
			query = "SELECT Tag FROM ThreadTags WHERE ThreadID=(?) AND CourseCode=(?)";
			st = conn.prepareStatement(query);
			st.setInt(1, threadID);
			st.setString(2, courseCode);
			rs = st.executeQuery();
			while (rs.next()) {
				tags.add(rs.getString("Tag"));
			}
			
		} catch (Exception e) {
			System.out.println("db error during initialization of Thread " + threadID + ", " + courseCode);
		}
	}

	@Override
	public void save(Connection conn) {
		try {
			String update = "INSERT INTO Thread VALUES ((?), (?), (?), (?), (?)) ON DUPLICATE KEY UPDATE Content=(?), Email=(?), FolderID=(?)";
			PreparedStatement st = conn.prepareStatement(update);
			st.setInt(1, threadID);
			st.setString(2, courseCode);
			st.setString(3, content);
			st.setString(4, email);
			st.setInt(5, folderID);
			st.setString(6, content);
			st.setString(7, email);
			st.setInt(8, folderID);
			st.executeUpdate();
			
			// Save tags
			update = "INSERT INTO ThreadTags VALUES((?), (?), (?))";
			st = conn.prepareStatement(update);
			st.setInt(1, threadID);
			st.setString(2, courseCode);
			for (String tag : tags) {
				st.setString(3, tag);
				st.execute();
			}
		} catch (Exception e) {
			System.out.println("db error during saving of Thread " + threadID + ", " + courseCode);
		}
	}
	
	/**
	 * Prints out content, tags and replies of thread to console
	 */
	public void view(Connection conn) {
		// Print out tags and content
		System.out.print("\n	");
		for (int i = 0; i < tags.size(); i++) {
			System.out.print("#" + tags.get(i));
			if (i != tags.size() - 1) {
				System.out.print(", ");
			}
		}
		System.out.println("\n	Likes: " + countLikes(conn));
		System.out.println("\n	" + content);
		
		// Print out replies
		Reply studReply = new Reply(studAnsID);
		Reply instReply = new Reply(instAnsID);
		studReply.initialize(conn);
		instReply.initialize(conn);
		studReply.view();
		System.out.println();
		instReply.view();
	}
	
	private int countLikes(Connection conn) {
		try {
			String query = "SELECT COUNT(*) AS NumberOfLikes FROM UserReadsThread WHERE ThreadID=(?) AND CourseCode=(?) AND Likes=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, threadID);
			st.setString(2, courseCode);
			st.setInt(3, 1);
			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getInt("NumberOfLikes");
		} catch (Exception e) {
			System.out.println("db error during counting of likes on Thread " + threadID + ", " + courseCode);
		}
		
		return 0;
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
