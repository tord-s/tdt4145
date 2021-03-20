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

	// public String userInput(String consoleMessage) {
	// Scanner sc = new Scanner(System.in);
	// System.out.println(consoleMessage);
	// return sc.nextLine();
	// }

	// FLYTTET connect() OG disconnect() FRA DBConn HIT ETTERSOM VI ANTAGELIGVIS
	// IKKE TRENGER FLERE CONNECTOR-KLASSER UANSETT
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

	/*
	 * private static Boolean logIn(Scanner sc, MainCtrl mainCtrl) {
	 * System.out.print("Email:"); String email = sc.nextLine();
	 * System.out.print("Password:"); String password = sc.nextLine(); User user =
	 * new User(email); user.initialize(mainCtrl.conn); Boolean valid_login =
	 * user.checkPassword(password); if (valid_login) { mainCtrl.userEmail = email;
	 * System.out.println("Logged in successfully"); return true; } else {
	 * System.out.println("Log in failed - Invalid email or password");
	 * System.out.println("Please try again"); return false; } }
	 */

	// GJORDE DENNE IKKE-STATISK
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

	// FLYTTET DENNE FRA COURSE TIL HER.
	// FØLER DETTE GIR MER MENING ETTERSOM METODEN UANSETT TRENGER ET MainCtrl
	// OBJEKT FOR HENTING AV CONN
	/**
	 * Finds all courses that the user participates in
	 * 
	 * @return A List of course codes
	 */
	private List<String> viewCoursesForUser() {
		List<String> result = new LinkedList<>();
		try {
			String query = "SELECT CourseCode FROM UserInCourse WHERE Email=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, userEmail);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("CourseCode"));
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Courses for User " + userEmail);
		}
		return result;
	}

	/*
	 * private static void selectCourseAndFolder(MainCtrl mainCtrl, Scanner sc) {
	 * LinkedList<String> courses = Course.getCoursesForUser(mainCtrl);
	 * System.out.println("\nYou are following these courses"); for (String i :
	 * courses) { System.out.println(i); }
	 * System.out.println("\nPlease write course code of course you want to view");
	 * System.out.print("Course code:"); mainCtrl.courseCode = sc.nextLine(); Course
	 * course = new Course(mainCtrl.courseCode); course.initialize(mainCtrl.conn);
	 * LinkedList<String> folders = course.getFolders(mainCtrl);
	 * System.out.println("\nCourse has following folders:"); for (String i :
	 * folders) { System.out.println(i); }
	 * System.out.println("\nPlease write ID of folder you want to view");
	 * System.out.print("ID:"); mainCtrl.folderID = Integer.parseInt(sc.nextLine());
	 * course.initialize(mainCtrl.conn); }
	 */

	// GJORDE DENNE METODEN IKKE-STATISK OG SEPARERTE DEN INN I FLERE METODER
	/*private void selectCourseAndFolder() {
		// Find and list all accessible courses
		List<String> courses = viewCoursesForUser();
		System.out.println("\nYou are following these courses");
		for (String s : courses) {
			System.out.println(s);
		}
		// Ask for user input on the courseCode of the course to view
		System.out.println("\nPlease write course code of course you want to view");
		System.out.print("Course code:");
		courseCode = sc.nextLine();
		// Initialize a course based on input courseCode
		Course course = new Course(courseCode);
		course.initialize(conn);
		// Find and list the folders within the course
		List<String> folders = course.viewFolders(conn);
		System.out.println("\nCourse has following folders:");
		for (String s : folders) {
			System.out.println(s);
		}
		// Ask for user input on the folderID of the folder to view
		System.out.println("\nPlease write ID of folder you want to view");
		System.out.print("ID:");
		folderID = Integer.parseInt(sc.nextLine());
		// Initialize a folder based on input folderID, courseCode
		Folder folder = new Folder(folderID, courseCode);
		folder.initialize(conn);
		// Find and list all Threads in this Folder
		List<String> threads = folder.viewThreads(conn);
		System.out.println("\nThreads:");
		for (String s : threads) {
			System.out.println(s);
		}
	}*/
	
	/**
	 * Lists all available courses for the user and asks for course-selection
	 */
	private void courseSelection() {
		// Find and list all accessible courses
		List<String> courses = viewCoursesForUser();
		System.out.println("\nYou are following these courses");
		for (String s : courses) {
			System.out.println(s);
		}
		// Ask for user input on the courseCode of the course to view
		System.out.println("\nPlease write course code of the course you want to view");
		System.out.print("Course code:");
		courseCode = sc.nextLine();
	}
	
	/**
	 * Lists all folders in active course and asks for folder-selection
	 */
	private void folderSelection() {
		// Initialize a course based on input courseCode
		Course course = new Course(courseCode);
		course.initialize(conn);
		// Find and list the folders within the course
		List<String> folders = course.viewFolders(conn);
		System.out.println("\nCourse has following folders:");
		for (String s : folders) {
			System.out.println(s);
		}
		// Ask for user input on the folderID of the folder to view
		System.out.println("\nPlease write ID of the folder you want to view");
		System.out.print("ID:");
		folderID = Integer.parseInt(sc.nextLine());
	}
	
	/**
	 * Lists all threads in active folder and asks for thread-selection
	 */
	private void threadSelection() {
		// Initialize a folder based on input folderID, courseCode
		Folder folder = new Folder(folderID, courseCode);
		folder.initialize(conn);
		// Find and list all Threads in this Folder
		List<String> threads = folder.viewThreads(conn);
		System.out.println("\nThreads:");
		for (String s : threads) {
			System.out.println(s);
		}
		// Asks for user input on the threadID of the thread to view
		System.out.println("\nPlease write ID of the thread you want to view");
		System.out.print("ID:");
		threadID = Integer.parseInt(sc.nextLine());
	}
	
	/**
	 * Takes user input on posting of a new thread
	 */
	private void threadPosting() {
		// TO-DO!
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
		// User selections
		courseSelection();
		folderSelection();
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
			
		// Done
		sc.close();
		disconnect();
	}

	public static void main(String[] args) {
		// HAR GENERELT GJORT HELE KODEN MINDRE STATISK OG MER OBJEKTORIENTERT. SYNTES
		// DETTE ER RYDDIGERE. ÅPEN FOR MOTARGUMENTER.
		//
		// PRØV OGSÅ GJERNE SÅ GODT MAN KAN Å SKRIVE KOMMENTARER UNDERVEIS I KODEN.
		// DETTE GJØR DET LETTERE Å LESE, SAMT REDIGERE. I TILLEGG SER JEG FOR MEG AT
		// DET VIL VÆRE ET PLUSS VED INNLEVERING.

		MainCtrl mainCtrl = new MainCtrl();
		mainCtrl.run();

		// FLYTTET DETTE INN I EN EGEN run() METODE I MainCtrl FOR MER RYDDIGHET OG
		// OVERSIKT I KODEN
		/*
		 * mainCtrl.connect();
		 * System.out.println("\nWelcome to our Piazza-ish application"); Scanner sc =
		 * new Scanner(System.in); Boolean successfull_login = logIn(sc, mainCtrl);
		 * while (!successfull_login) { successfull_login = logIn(sc, mainCtrl); }
		 * selectCourseAndFolder(mainCtrl, sc); sc.close();
		 */
	}

}
