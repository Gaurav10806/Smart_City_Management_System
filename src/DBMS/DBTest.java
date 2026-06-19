package DBMS;

import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        try {
            // Get connection
            Connection con = DBConnection.getConnection();
            System.out.println("✅ Connected to database successfully!");
            
            // Check complaints table
            System.out.println("\n🔍 Checking complaints table...");
            checkTable(con, "complaints");
            
            // Check user table
            System.out.println("\n🔍 Checking user table...");
            checkTable(con, "user");
            
            // Check officer table
            System.out.println("\n🔍 Checking officer table...");
            checkTable(con, "officer");
            
            con.close();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void checkTable(Connection con, String tableName) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
            System.out.println("\n📋 Table: " + tableName);
            System.out.println("COLUMN_NAME\tTYPE_NAME\tCOLUMN_SIZE");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String typeName = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                System.out.printf("%-15s %-15s %d%n", columnName, typeName, columnSize);
            }
            
            // Check primary keys
            try (ResultSet pkRs = meta.getPrimaryKeys(null, null, tableName)) {
                System.out.println("\n🔑 Primary Keys:");
                while (pkRs.next()) {
                    System.out.println("- " + pkRs.getString("COLUMN_NAME"));
                }
            }
            
            // Check foreign keys
            try (ResultSet fkRs = meta.getImportedKeys(null, null, tableName)) {
                System.out.println("\n🔗 Foreign Keys:");
                while (fkRs.next()) {
                    System.out.printf("- %s -> %s.%s%n", 
                        fkRs.getString("FKCOLUMN_NAME"),
                        fkRs.getString("PKTABLE_NAME"),
                        fkRs.getString("PKCOLUMN_NAME"));
                }
            }
        }
    }
}
