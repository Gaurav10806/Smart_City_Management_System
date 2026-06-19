
package DBMS;

import java.sql.*;

public class DBConnection {
    // FIXED: Changed from user3 to smart_city to match SQL schema
    public static final String DB_URL = "jdbc:mysql://localhost:3306/smart_city";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    public static final String driverName = "com.mysql.cj.jdbc.Driver";
    
    public static Connection getConnection() throws Exception {
        Class.forName(driverName);
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    // Additional helper method to test connection
    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con != null && !con.isClosed();
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
