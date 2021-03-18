import java.sql.*;
import java.util.Scanner;

public class MainCtrl extends DBConn {
	private String userEmail; // Active user
	private String courseCode; // Active course
	private String folderID; // Active folder
	
	public String userInput(String consoleMessage) {
		Scanner sc = new Scanner(System.in);
		System.out.println(consoleMessage);
		return sc.nextLine();
	}
}
