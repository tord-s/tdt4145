import java.util.ArrayList;

public class Controller {
    public static void main(String[] args) throws Exception {
        Course.initialize();
        ArrayList<Course> courses_before_add = Course.getCourses();
        for (Course course : courses_before_add) {
            System.out.println(course.getCourseCode());
            System.out.println("\t" + course.getName());
            System.out.println("\t" + course.getTerm());
            System.out.println("\t" + course.getAllowAnonymous().toString());
            System.out.println("\n");
        } 
        Course.save("TDT4140", "PU", "Nå", false);
        ArrayList<Course> courses_after_add = Course.getCourses();
        for (Course course : courses_after_add) {
            System.out.println(course.getCourseCode());
            System.out.println("\t" + course.getName());
            System.out.println("\t" + course.getTerm());
            System.out.println("\t" + course.getAllowAnonymous().toString());
            System.out.println("\n");
        } 
    }
}
