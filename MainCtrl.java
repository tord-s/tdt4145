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
	 * Initializes the conn field as a Connection to a database
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
		System.out.print("Email:");
		String email = sc.nextLine();
		System.out.print("Password:");
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
		// Find and list all accessible courses
		User user = new User(userEmail);
		user.initialize(conn);
		user.viewCourses(conn);
		
		// Ask for user input on the courseCode of the course to view
		System.out.println("\nPlease write course code of the course you want to view");
		System.out.print("Course code:");
		courseCode = sc.nextLine();
	}
	
	/**
	 * Lists all folders in active course and asks for folder-selection
	 */
	private void folderSelection() {
		// Initialize a course based on input courseCode and view its folders
		Course course = new Course(courseCode);
		course.initialize(conn);
		course.viewFolders(conn);
		
		// Ask for user input on the folderID of the folder to view
		System.out.println("\nPlease write ID of the folder you want to view");
		System.out.print("ID:");
		folderID = Integer.parseInt(sc.nextLine());
	}
	
	/**
	 * Lists all threads in active folder and asks for thread-selection
	 */
	private void threadSelection() {
		// Initialize a folder based on input folderID, courseCode and view its threads
		Folder folder = new Folder(folderID, courseCode);
		folder.initialize(conn);
		folder.viewThreads(conn);
		
		// Asks for user input on the threadID of the thread to view
		System.out.println("\nPlease write ID of the thread you want to view");
		System.out.print("ID:");
		threadID = Integer.parseInt(sc.nextLine());
	}
	
	/**
	 * Posts a new thread to active folder based on user input
	 */
	private void threadPosting() {
		// Take user input on content
		System.out.println("\nWrite the content of the new thread here:");
		String content = sc.nextLine();
		
		// Take user input on tags
		List<String> tags = new LinkedList<>();
		System.out.println("\nDo you want to add a tag? (y/n)");
		String addTag = sc.nextLine();
		while (true) {
			if (addTag.equals("y") || addTag.equals("yes")) {
				System.out.println("\nTag:");
				tags.add(sc.nextLine());
				System.out.println("\nDo you want to add another tag? (y/n)");
				addTag = sc.nextLine();
			} else if (addTag.equals("n") || addTag.equals("no")) {
				break;
			} else {
				System.out.println("\n'" + addTag + "'" + " is not a valid input");
				System.out.println("Please try again:");
				addTag = sc.nextLine();
			}
		}
		
		// Find threadID of new thread
		Course course = new Course(courseCode);
		course.initialize(conn);
		List<Integer> threadIDs = course.getThreadIDs();
		int newID = threadIDs.get(threadIDs.size() - 1) + 1;
		
		// Create and save new thread
		new Thread(newID, courseCode, content, userEmail, folderID, tags).save(conn);
		
		// Create and save two empty replies
		int studReplyID = (int) (Integer.MAX_VALUE*Math.random()); // KJØRER DENNE TAKTIKKEN PÅ ReplyID INNTIL VIDERE TENKER JEG
		int instReplyID = (int) (Integer.MAX_VALUE*Math.random());
		new Reply(studReplyID, null, null, newID, courseCode, "StudentsAnswer").save(conn);
		new Reply(instReplyID, null, null, newID, courseCode, "InstructorsAnswer").save(conn);
		
		// Confirmation for user
		Folder folder = new Folder(folderID, courseCode);
		folder.initialize(conn);
		String folderName = folder.getName();
		System.out.println("Thread posted to folder " + folderName);
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
		System.out.println("\nWrite 'browse' if you would like to browse existing threads or 'post' if you would like to post a new thread");
		System.out.println("Your input:");
		String browseOrPost = sc.nextLine();
		while (true) {
			if (browseOrPost.equals("browse")) {
				threadSelection();
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
