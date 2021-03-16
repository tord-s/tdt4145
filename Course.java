import java.sql.*;
import java.util.*;

public class Course extends Database {

    private String courseCode;
    private String name;
    private String term;
    private Boolean allowAnonymous;

    private static ArrayList<Course> courses = new ArrayList<Course>();

    public Course (String courseCode, String name, String term, Boolean allowAnonymous) {
        this.courseCode = courseCode;
        this.name = name;
        this.term = term;
        this.allowAnonymous = allowAnonymous;
    }

    public static void initialize() throws Exception {
        Connection con = Database.connect();
        Statement st = con.createStatement();
        String query = "SELECT * FROM course;";
        ResultSet resultSet = st.executeQuery(query);
        while (resultSet.next()) {
            String c = resultSet.getString("CourseCode");
            String n = resultSet.getString("Name");
            String t = resultSet.getString("Term");
            Boolean a = resultSet.getBoolean("AllowAnonymous");
            Course course_from_db = new Course(c,n,t,a);
            Course.courses.add(course_from_db);
        }
    }

    public static void save(String courseCode, String name, String term, Boolean allowAnonymous) throws Exception {
        Course course_to_add_to_db = new Course(courseCode, name, term, allowAnonymous);
        Connection con = Database.connect();
        String query = "INSERT INTO Course ("
        + "CourseCode,"
        + "Name,"
        + "Term) VALUES ("
        + "?, ?, ?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, courseCode);
        st.setString(2, name);
        st.setString(3, term);
        System.out.println(query);
        st.executeUpdate();
        st.close();
        Course.courses.add(course_to_add_to_db);
    }


    public Boolean getAllowAnonymous() {
        return allowAnonymous;
    }

    public String getCourseCode() {
        return courseCode;
    }
    
    public String getName() {
        return name;
    }

    public String getTerm() {
        return term;
    }

    public static ArrayList<Course> getCourses() {
        return courses;
    }

}

