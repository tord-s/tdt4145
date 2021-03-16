import java.sql.*;

public class User extends Database { 

    public static void PrintResult(String query, ResultSet resultSet) throws Exception {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();   

        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    
    }

    public static void ListAll() throws Exception {
        Connection con = Database.connect();
        Statement st = con.createStatement();
        String query = "SELECT * FROM user;";
        ResultSet resultSet = st.executeQuery(query);
        PrintResult(query, resultSet);
    }   
    
}
