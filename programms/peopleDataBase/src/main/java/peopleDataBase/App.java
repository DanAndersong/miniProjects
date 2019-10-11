package peopleDataBase;

import java.sql.*;

public class App {

    public static final  String DB_URL = "jdbc:h2://Users/user/github/miniProjects/programms/peopleDataBase/src/main/java/peopleDataBase/";
    public static final  String DB_Driver = "org.h2.Driver";

    public static void main( String[] args ) {
        try {
            Class.forName(DB_Driver);
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection on.");
            connection.close();
            System.out.println("Connection off.");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC driver not found.");
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error.");
        }
    }
}
