import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class MainCtrl implements Runnable {
	private String userEmail; // Active user
	private String courseCode; // Active course
	private int folderID; // Active folder
	private int threadID; // Active thread
	private Connection conn;
	private Scanner sc;

	/**
	 * Initializes the conn field as a Connection to a specific database
	 */
	private void connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Properties p = new Properties();
			p.put("user", "root");
			p.put("password", "toor");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/prosjekt?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&serverTimezone=UTC",
					p);
		} catch (Exception e) {
			throw new RuntimeException("Unable to connect", e);
		}
	}

	/**
	 * Closes the Connection conn
	 */
	private void disconnect() {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println("Unable to disconnect");
		}
	}

	/**
	 * Asks a user for input of email and password and logs in the user if these
	 * match
	 * 
	 * @param sc For taking input from console
	 * @return True if email and password match
	 */
	private boolean logIn() {
		// Take user input of email and password
		System.out.print("Email: ");
		String email = sc.nextLine();
		System.out.print("Password: ");
		String password = sc.nextLine();
		
		// Initialize a user based on input email
		User user = new User(email);
		user.initialize(conn);
		
		// Check if password is correct, and if so, log in user
		boolean valid_login = user.checkPassword(password);
		if (valid_login) {
			userEmail = email;
			System.out.println("Logged in successfully");
			return true;
		} else {
			System.out.println("Log in failed - Invalid email or password");
			System.out.println("Please try again");
			return false;
		}
	}

	/**
	 * Lists all available courses for the user and asks for course-selection
	 */
	private void courseSelection() {
		// Initialize active user and view its accessible courses
		User user = new User(userEmail);
		user.initialize(conn);
		user.viewCourses(conn);
		
		// Ask for user input on the courseCode of the course to view
		System.out.println("\nPlease write course code of the course you want to view");
		System.out.print("Course code: ");
		courseCode = sc.nextLine();
	}
	
	/**
	 * Lists all folders in active course and asks for folder-selection
	 */
	private void folderSelection() {
		// Initialize active course and view its folders
		Course course = new Course(courseCode);
		course.initialize(conn);
		course.viewFolders(conn);
		
		// Ask for user input on the folderID of the folder to view
		System.out.println("\nPlease write ID of the folder you want to view");
		System.out.print("ID: ");
		folderID = Integer.parseInt(sc.nextLine());
	}
	
	/**
	 * Lists all threads in active folder and asks for thread-selection
	 */
	private void threadSelection() {
		// Initialize active folder and view its threads
		Folder folder = new Folder(folderID, courseCode);
		folder.initialize(conn);
		folder.viewThreads(conn);
		
		// Asks for user input on the threadID of the thread to view
		System.out.println();
		System.out.println("\nPlease write ID of the thread you want to view");
		System.out.print("ID: ");
		threadID = Integer.parseInt(sc.nextLine());
	}
	
	/**
	 * Posts a new thread to active folder based on user input
	 */
	private void threadPosting() {
		// Take user input on anonymity if allowed
		boolean anonymous = false;
		Course course = new Course(courseCode);
		course.initialize(conn);
		if (course.allowsAnonymous()) {
			System.out.println("\nPost anonymously? (y/n)");
			System.out.print("Your input: ");
			String anonymousInput = sc.nextLine();
			while (true) {
				if (anonymousInput.equals("y") || anonymousInput.equals("yes")) {
					anonymous = true;
					break;
				} else if (anonymousInput.equals("n") || anonymousInput.equals("no")) {
					break;
				} else {
					System.out.println(anonymousInput + " is not a valid input.");
					System.out.print("Please try again: ");
					anonymousInput = sc.nextLine();
				}
			}
		}
		
		// Take user input on content
		System.out.println("\nWrite the content of the new thread here:");
		String content = sc.nextLine();
		
		// Take user input on tags
		List<String> tags = new LinkedList<>();
		System.out.println("\nDo you want to add a tag? (y/n)");
		System.out.print("Your input: ");
		String addTag = sc.nextLine();
		while (true) {
			if (addTag.equals("y") || addTag.equals("yes")) {
				System.out.print("\nTag: ");
				tags.add(sc.nextLine());
				System.out.println("\nDo you want to add another tag? (y/n)");
				System.out.print("Your input: ");
				addTag = sc.nextLine();
			} else if (addTag.equals("n") || addTag.equals("no")) {
				break;
			} else {
				System.out.println("\n'" + addTag + "'" + " is not a valid input");
				System.out.print("Please try again: ");
				addTag = sc.nextLine();
			}
		}
		
		// Find threadID of new thread
		List<Integer> threadIDs = course.getThreadIDs();
		int newID = threadIDs.get(threadIDs.size() - 1) + 1;
		
		// Create and save new thread with email set to null if thread is posted anonymously
		new Thread(newID, courseCode, content, anonymous ? null : userEmail, folderID, tags).save(conn);
		
		// Create and save two empty replies
		int studReplyID = (int) (Integer.MAX_VALUE*Math.random());
		int instReplyID = (int) (Integer.MAX_VALUE*Math.random());
		try {
			new Reply(studReplyID, null, null, newID, courseCode, "StudentsAnswer").save(conn);
			new Reply(instReplyID, null, null, newID, courseCode, "InstructorsAnswer").save(conn);
		} catch (Exception e) {
			System.out.println("Error during instantiation of Reply");
		}
		
		// Confirmation for user
		Folder folder = new Folder(folderID, courseCode);
		folder.initialize(conn);
		String folderName = folder.getName();
		System.out.println("\nThread posted to folder " + folderName);
	}
	
	/**
	 * Registers if the user likes the post and if they would like to leave a reply
	 */
	private void userReadsThread() {
		try {
			String update = "INSERT INTO UserReadsThread VALUES((?), (?), (?), (?)) ON DUPLICATE KEY UPDATE Likes=(?)";
			PreparedStatement st = conn.prepareStatement(update);
			st.setInt(1, threadID);
			st.setString(2, courseCode);
			st.setString(3, userEmail);
			
			// Take user input on if they like the post
			System.out.println("\nDo you like this post? (y/n)");
			System.out.print("Your input: ");
			String likesPost = sc.nextLine();
			while (true) {
				if (likesPost.equals("y") || likesPost.equals("yes")) {
					st.setInt(4, 1);
					st.setInt(5, 1);
					break;
				} else if (likesPost.equals("n") || likesPost.equals("no")) {
					st.setInt(4, 0);
					st.setInt(5, 0);
					break;
				} else {
					System.out.println(likesPost + " is not a valid input");
					System.out.print("Please try again: ");
					likesPost = sc.nextLine();
				}
			}
			st.executeUpdate();
		} catch (Exception e) {
			System.out.println("db error while reading Thread " + threadID + ", " + courseCode);
		}
		
		// Take user input on if they would like to reply to thread
		System.out.println("Would you like to reply to this post? (y/n)");
		System.out.println("Your input: ");
		String toReply = sc.nextLine();
		while (true) {
			if (toReply.equals("y") || toReply.equals("yes")) {
				replyToThread();
				break;
			} else if (toReply.equals("n") || toReply.equals("no")) {
				break;
			} else {
				System.out.println(toReply + "is not a valid input");
				System.out.print("Please try again: ");
				toReply = sc.nextLine();
			}
		}
	}
	
	private void replyToThread() {
		// Create and initialize user to get role in course
		User user = new User(userEmail);
		user.initialize(conn);
		String role = user.roleInCourse(courseCode, conn);
		
		// Create and initialize thread
		Thread thread = new Thread(threadID, courseCode);
		thread.initialize(conn);
		
		// Check if the reply corresponding to the users role is already replied to
		int replyID = 0;
		if (role.equals("Student")) {
			replyID = thread.getStudAnsID();
		} else if (role.equals("Instructor")) {
			replyID = thread.getInstAnsID();
		}
		Reply reply = new Reply(replyID);
		reply.initialize(conn);
		
		// Only continue if thread is not replied to
		if (reply.getContent() != null) {
			System.out.println("This thread already has a reply by a(n) " + role);
			return;
		} 
		
		// Take user input on anonymity if allowed
		boolean anonymous = false;
		Course course = new Course(courseCode);
		course.initialize(conn);
		if (course.allowsAnonymous()) {
			System.out.println("\nReply to post anonymously? (y/n)");
			System.out.print("Your input: ");
			String anonymousInput = sc.nextLine();
			while (true) {
				if (anonymousInput.equals("y") || anonymousInput.equals("yes")) {
					anonymous = true;
					break;
				} else if (anonymousInput.equals("n") || anonymousInput.equals("no")) {
					break;
				} else {
					System.out.println(anonymousInput + " is not a valid input.");
					System.out.print("Please try again: ");
					anonymousInput = sc.nextLine();
				}
			}
		}
		
		// Take user input on content
		System.out.println("\nWrite the content of the reply here:");
		String content = sc.nextLine();
		
		// Create and save a new reply
		try {
			new Reply(replyID, userEmail, content, threadID, courseCode, role.equals("Student") ? "StudentsAnswer" : "InstructorsAnswer").save(conn);
		} catch (Exception e) {
			System.out.println("Error during instantiaion of Reply");
		}
		
		// Confirmation for user
		System.out.println("Reply given to Thread " + threadID);
	}
	
	@Override
	public void run() {
		// Connect to database
		connect();
		
		// Welcome user and create a Scanner for input
		System.out.println("\nWelcome to our Piazza-ish application");
		sc = new Scanner(System.in);
		
		// Log in
		boolean successfull_login = logIn();
		while (!successfull_login) {
			successfull_login = logIn();
		}
		
		// Selection of course and folder
		courseSelection();
		folderSelection();
		
		
		// Get input from user on if they would like to browse or post threads
		System.out.println("\nWrite 'browse' if you would like to browse existing threads \nor 'post' if you would like to post a new thread");
		System.out.print("Your input: ");
		String browseOrPost = sc.nextLine();
		while (true) {
			if (browseOrPost.equals("browse")) {
				threadSelection();
				Thread thread = new Thread(threadID, courseCode);
				thread.initialize(conn);
				thread.view(conn);
				userReadsThread();
				break;
			} else if (browseOrPost.equals("post")) {
				threadPosting();
				break;
			}
			System.out.println("\n'" + browseOrPost + "'" + " is not a valid input");
			System.out.println("Please try again:");
			browseOrPost = sc.nextLine();
		}
			
		// Close scanner and disconnect from database
		sc.close();
		disconnect();
	}

	public static void main(String[] args) {
		MainCtrl mainCtrl = new MainCtrl();
		mainCtrl.run();
	}

}
