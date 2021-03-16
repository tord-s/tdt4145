import java.sql.*;
import java.util.Properties;

// 1. import --> java.sql
// 2. load and register the driver --> com.mysql.jdbc.Driver
// 3. create a connection
// 4. create a statement
// 5. excecute the query
// 6. process the resluts
// 7. close



public class Database {
   public static Connection connect() throws Exception  {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Properties p = new Properties();
      p.put("user", "root");
      p.put("password", "LiteKreativtPass0rd");

      Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/prosjekt_db_f_test?allowPubicKeyRetrival=true&autoReconnect=true&useSSL=false&serverTimezone=UTC", p);
      return con;
     }
   }