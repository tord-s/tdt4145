import java.sql.*;
import java.util.LinkedList;
import java.util.Scanner;

public class MainCtrl extends DBConn {
	private String userEmail; // Active user
	private String courseCode; // Active course
	private String folderID; // Active folder
	
	// public String userInput(String consoleMessage) {
	// 	Scanner sc = new Scanner(System.in);
	// 	System.out.println(consoleMessage);
	// 	return sc.nextLine();
	// }

	private static Boolean logIn(Scanner sc, MainCtrl mainCtrl) {
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
		}
		else {
			System.out.println("Log in failed - Invalid email or password");
			System.out.println("Please try again");
			return false;
		}
	}

	public static void main(String[] args) {
		MainCtrl mainCtrl = new MainCtrl();
		mainCtrl.connect();
		System.out.println("\nWelcome to our Piazza-ish application");
		Scanner sc = new Scanner(System.in);
		Boolean successfull_login = logIn(sc, mainCtrl);
		while (!successfull_login) {
			successfull_login = logIn(sc, mainCtrl);
		}
		LinkedList<String> courses = new LinkedList<String>();
		courses = Course.getCoursesForUser(mainCtrl);
		System.out.println("\nYou are following these courses");
		for (String i : courses) {
			System.out.println(i);
		  }
		sc.close();
	}

	public String getUserEmail() {
		return userEmail;
	}
}

