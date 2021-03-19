import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class MainCtrl implements Runnable {
	private String userEmail; // Active user
	private String courseCode; // Active course
	private int folderID; // Active folder
	private Connection conn;

	// public String userInput(String consoleMessage) {
	// Scanner sc = new Scanner(System.in);
	// System.out.println(consoleMessage);
	// return sc.nextLine();
	// }

	// Flyttet connect() og disconnect() fra DBConn hit ettersom vi antageligvis ikke trenger flere connector-klasser
	public void connect() {
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

	public void disconnect() {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println("Unable to close connection!");
		}
	}

	/*private static Boolean logIn(Scanner sc, MainCtrl mainCtrl) {
		System.out.print("Email:");
		String email = sc.nextLine();
		System.out.print("Password:");
		String password = sc.nextLine();
		User user = new User(email);
		user.initialize(mainCtrl.conn);
		Boolean valid_login = user.checkPassword(password);
		if (valid_login) {
			mainCtrl.userEmail = email;
			System.out.println("Logged in successfully");
			return true;
		} else {
			System.out.println("Log in failed - Invalid email or password");
			System.out.println("Please try again");
			return false;
		}
	}*/
	
	// Gjorde denne ikke-statisk
	private boolean logIn(Scanner sc) {
		System.out.print("Email:");
		String email = sc.nextLine();
		System.out.print("Password:");
		String password = sc.nextLine();
		User user = new User(email);
		user.initialize(conn);
		Boolean valid_login = user.checkPassword(password);
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
	
	// Flyttet denne fra Course til her for føler dette gir mer mening etterom den uansett trenge et MainCtrl objekt
	public List<String> getCoursesForUser() {
		List<String> result = new LinkedList<String>();
		try {
			String query = "SELECT CourseCode FROM UserInCourse WHERE Email=(?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, getUserEmail());
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("CourseCode"));
			}
		} catch (Exception e) {
			System.out.println("db error during initialization of Courses for User " + getUserEmail());
		}
		return result;
	}

	/*private static void selectCourseAndFolder(MainCtrl mainCtrl, Scanner sc) {
		LinkedList<String> courses = Course.getCoursesForUser(mainCtrl);
		System.out.println("\nYou are following these courses");
		for (String i : courses) {
			System.out.println(i);
		}
		System.out.println("\nPlease write course code of course you want to view");
		System.out.print("Course code:");
		mainCtrl.courseCode = sc.nextLine();
		Course course = new Course(mainCtrl.courseCode);
		course.initialize(mainCtrl.conn);
		LinkedList<String> folders = course.getFolders(mainCtrl);
		System.out.println("\nCourse has following folders:");
		for (String i : folders) {
			System.out.println(i);
		}
		System.out.println("\nPlease write ID of folder you want to view");
		System.out.print("ID:");
		mainCtrl.folderID = Integer.parseInt(sc.nextLine());
		course.initialize(mainCtrl.conn);
	}*/
	
	// Gjorde denne metoden ikke-statisk
	private void selectCourseAndFolder(Scanner sc) {
		List<String> courses = getCoursesForUser();
		System.out.println("\nYou are following these courses");
		for (String i : courses) {
			System.out.println(i);
		}
		System.out.println("\nPlease write course code of course you want to view");
		System.out.print("Course code:");
		courseCode = sc.nextLine();
		Course course = new Course(courseCode);
		course.initialize(conn);
		List<String> folders = course.getFolders(conn);
		System.out.println("\nCourse has following folders:");
		for (String i : folders) {
			System.out.println(i);
		}
		System.out.println("\nPlease write ID of folder you want to view");
		System.out.print("ID:");
		folderID = Integer.parseInt(sc.nextLine());
		course.initialize(conn);
	}

	@Override
	public void run() {
		connect();
		System.out.println("\nWelcome to our Piazza-ish application");
		Scanner sc = new Scanner(System.in);
		boolean successfull_login = logIn(sc);
		while (!successfull_login) {
			successfull_login = logIn(sc);
		}
		selectCourseAndFolder(sc);
		sc.close();
	}

	public String getUserEmail() {
		return userEmail;
	}

	public static void main(String[] args) {
		MainCtrl mainCtrl = new MainCtrl();
		mainCtrl.run();
		
		// Flyttet dette inn i en egen run() metode i MainCtrl for mer ryddighet og oversikt 
		/*mainCtrl.connect();
		System.out.println("\nWelcome to our Piazza-ish application");
		Scanner sc = new Scanner(System.in);
		Boolean successfull_login = logIn(sc, mainCtrl);
		while (!successfull_login) {
			successfull_login = logIn(sc, mainCtrl);
		}
		selectCourseAndFolder(mainCtrl, sc);
		sc.close();*/
	}

}
