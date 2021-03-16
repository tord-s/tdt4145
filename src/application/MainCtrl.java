package application;

import java.sql.*;

import application.ados.User;

public class MainCtrl extends DBConn {
	private User user; // Active user
	private String courseCode; // Active course code
	private String folder; // Active folder
}
