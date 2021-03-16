import java.sql.*;
import java.util.Properties;

// 1. import --> java.sql
// 2. load and register the driver --> com.mysql.jdbc.Driver
// 3. create a connection
// 4. create a statement
// 5. excecute the query
// 6. process the resluts
// 7. close



public class DemoClass {
   public static void main(String[] args) throws Exception {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Properties p = new Properties();
      p.put("user", "root");
      p.put("password", "LiteKreativtPass0rd");

      Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/prosjekt_db_f_test?allowPubicKeyRetrival=true&autoReconnect=true&useSSL=false&serverTimezone=UTC", p);

      String query = "SELECT * FROM course;";

      Statement st = con.createStatement();
      ResultSet resultSet = st.executeQuery(query);
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
}