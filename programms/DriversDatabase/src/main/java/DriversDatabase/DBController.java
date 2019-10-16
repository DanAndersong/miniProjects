package DriversDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
/**
 * METHODS FROM STATEMENT
 1. executeUpdate, (CREATE, INSERT, UPDATE) return quantity.
 2. executeQuery, (SELECT) return object.
 3. execute, return true if do something from 1, statement.getUpdateCount() for get.
                return false - if do something from 2. statement.getResultSet() for get.
 */

public class DBController {
    private static final String DB_URL = "jdbc:mysql://localhost:8889/?serverTimezone=UTC";
    private static final String DB_Driver = "com.mysql.cj.jdbc.Driver";
    private static String user;
    private static String password;

    private static Connection connection;
    private static Statement statement;

    public DBController() {

    }

    public void init() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("User: ");
            user = reader.readLine();
            System.out.print("Password: ");
            password = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName(DB_Driver);
            connection = DriverManager.getConnection(DB_URL, user, password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error.");
        } finally {
            try {
                if (connection != null)
                    connection.close();
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            if (statement != null)
                statement.close();

            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }


    //    private static void addNewDriverLicense(String personId, Categories ... categories) {
//        Person person;
//        if (people.containsKey(personId)) {
//            person = people.get(personId);
//        } else {
//            System.out.println("id do not found");
//            return;
//        }
//        driverLicenses.add(new DriverLicense(person, categories));
//    }
//
//    private static void initPeopleDB() {
//        people = new HashMap();
//        people.put("F4421145642", new Person("Ben", "Aplic", "F4421145642", new GregorianCalendar(1995,Calendar.MARCH,24), true));
//    }
//
}