package Java;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Date;
import DS.*;
import DBMS.DBConnection;


public class Admin {
    private String username;
    private boolean loggedIn = false;
    Scanner sc = new Scanner(System.in);
    private Scanner scanner;

    Connection con;
    String query;
    ResultSet rs;
    PreparedStatement pst;
    CallableStatement cst;

    // Data structure instances
    private SystemMetricsGraph systemGraph;
    private ReportGeneratorHashMap reportCache;
    private AdminActivityLog activityLog;

    public Admin() throws Exception {
        con = DBConnection.getConnection();

        // Initialize DS instances with increased capacity
        systemGraph = new SystemMetricsGraph(100); // Increased from 20 to 100
        reportCache = new ReportGeneratorHashMap(100);
        activityLog = new AdminActivityLog(1000);
        
        // Initialize counter from database to avoid duplicate IDs
        initializeCounter();
    }

    public Admin(Connection con) {
        this.con = con;
        this.activityLog = new AdminActivityLog(1000); // Using the capacity-based constructor
        this.scanner = new Scanner(System.in);
        testDatabaseConnection();
    }

    public boolean isLoggedIn() { return loggedIn; }

    public void login() {
        System.out.println("--- Admin Login ---");
        System.out.print("Enter Username: ");
        String uname = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        // Granular feedback for built-in admin
        if (!uname.equalsIgnoreCase("admin")) {
            System.out.println("Invalid username.");
            return;
        }
        if (!pass.equals("admin123")) {
            System.out.println("Incorrect password.");
            return;
        }

        loggedIn = true;
        username = uname;
        System.out.println("Admin login successful.");

        // Log activity
        activityLog.logActivity("LOGIN", "Admin logged into system");

        try {
            loadSystemMetrics();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Load system metrics into graph structure
    private void loadSystemMetrics() throws SQLException {
        cst = con.prepareCall("{CALL GetComplaintAnalytics()}");
        rs = cst.executeQuery();

        while (rs.next()) {
            systemGraph.addVertex(
                    rs.getString("area"),
                    rs.getString("category"),
                    rs.getInt("total_complaints"),
                    rs.getDouble("resolution_rate")
            );
        }

        // Close resources
        rs.close();
        cst.close();
    }
    // Static counter for generating unique complaint IDs
    static int i = 1050;
    
    // Initialize counter from database to avoid duplicates
    private void initializeCounter() {
        try {
            query = "SELECT MAX(complain_id) FROM complaints";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (maxId > 0) {
                    i = maxId + 1; // Start from next available ID
                }
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.out.println("Warning: Could not initialize counter from database. Using default: " + i);
        }
    }
    
    // Get next available complaint ID safely
    private synchronized int getNextComplaintId() {
        try {
            // Double-check with database to ensure no duplicates
            query = "SELECT MAX(complain_id) FROM complaints";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (maxId >= i) {
                    i = maxId + 1; // Update counter if database has higher IDs
                }
            }
            rs.close();
            pst.close();
            
            int nextId = i++;
            return nextId;
        } catch (SQLException e) {
            System.out.println("Warning: Could not verify ID with database. Using counter: " + i);
            return i++;
        }
    }

    private String validateAndNormalizeArea(String inputArea) {
        if (inputArea == null) {
            throw new IllegalArgumentException("Area cannot be null. Allowed: Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura");
        }
        String trimmed = inputArea.trim();
        String[] allowed = {"Paldi", "Bapunagar", "Ellisbridge", "Maninagar", "Navrangpura"};
        for (String a : allowed) {
            if (a.equalsIgnoreCase(trimmed)) {
                return a; // return canonical casing
            }
        }
        throw new IllegalArgumentException("Invalid area: " + inputArea + ". Allowed: Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura");
    }

    private String validateAndNormalizeCategory(String inputCategory) {
        if (inputCategory == null) {
            throw new IllegalArgumentException("Category cannot be null. Allowed: Drainage, Water, Electricity, Road Maintenance, Other");
        }
        String trimmed = inputCategory.trim();
        String[] allowed = {"Drainage", "Water", "Electricity", "Road Maintenance", "Other"};
        for (String c : allowed) {
            if (c.equalsIgnoreCase(trimmed)) {
                return c; // return canonical casing
            }
        }
        throw new IllegalArgumentException("Invalid category: " + inputCategory + ". Allowed: Drainage, Water, Electricity, Road Maintenance, Other");
    }

    // Email validation method
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        String trimmedEmail = email.trim();
        if (!trimmedEmail.contains("@") || (!trimmedEmail.contains(".com") && !trimmedEmail.contains(".in"))) {
            throw new IllegalArgumentException("Invalid email format. Email must contain '@' and either '.com' or '.in'");
        }
    }
    
    // Address validation method
    public void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        String trimmedAddress = address.trim();
        if (trimmedAddress.length() < 5) {
            throw new IllegalArgumentException("Address must be at least 5 characters long");
        }
        if (trimmedAddress.length() > 200) {
            throw new IllegalArgumentException("Address must not exceed 200 characters");
        }
    }
    
    public void ManageAllUsers() throws Exception {
        if (!loggedIn) {
            System.out.println(" Admin must be logged in to manage users.");
            return;
        }

        System.out.println("Add or delete user? (add/delete): ");
        String action = sc.nextLine();

        if (action.equalsIgnoreCase("add")) {
            System.out.println("\nAdding new user...");
            displayValidationRules();
            System.out.println("\n--- Enter User Details ---");

            String newUsername = getValidUsername("Enter username: ");
            String newPassword = getValidPassword("Enter password: ");
            
            // Email validation loop
            String newEmail;
            while (true) {
                try {
                    System.out.print("Enter email: ");
                    newEmail = sc.nextLine().trim();
                    validateEmail(newEmail);
                    break; // If validation passes, exit the loop
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("Please try again.");
                }
            }
            
            System.out.print("Enter address: ");
            String newAddress = sc.nextLine();
            validateAddress(newAddress);
            // Area validation loop
            String newArea;
            while (true) {
                try {
                    System.out.print("Enter area (Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura): ");
                    newArea = sc.nextLine();
                    newArea = validateAndNormalizeArea(newArea);
                    break; // If validation passes, exit the loop
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("Please try again.");
                }
            }

            // Start transaction
            con.setAutoCommit(false);
            int userResult = 0;
            PreparedStatement pst = null;
            ResultSet rs = null;
            
            try {
                // Get next complaint ID
                int nextComplainId = getNextComplaintId();
                
                // Use direct SQL for user insertion
                String sql = "INSERT INTO user (username, password, email, address, area, complain_id) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sql);
                pst.setString(1, newUsername);
                pst.setString(2, newPassword);
                pst.setString(3, newEmail);
                pst.setString(4, newAddress);
                pst.setString(5, newArea);
                pst.setInt(6, nextComplainId);

                userResult = pst.executeUpdate();
                
                // Check if user was inserted successfully
                if (userResult > 0) {
                    con.commit();
                    System.out.println("User added successfully!");
                    activityLog.logActivity("USER_ADD", "Added user: " + newUsername);
                } else {
                    // Check if user already exists
                    String checkSql = "SELECT COUNT(*) FROM user WHERE username = ?";
                    pst = con.prepareStatement(checkSql);
                    pst.setString(1, newUsername);
                    rs = pst.executeQuery();
                    
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("User already exists with username: " + newUsername);
                        con.rollback();
                        return;
                    }
                    
                    con.rollback();
                    System.out.println("Failed to add user. Please try again.");
                }
            } catch (SQLException e) {
                try {
                    con.rollback();
                    System.out.println("Error adding user: " + e.getMessage());
                } catch (SQLException ex) {
                    System.out.println("Error rolling back transaction: " + ex.getMessage());
                }
            } finally {
                // Close resources
                try {
                    if (rs != null) rs.close();
                    if (pst != null) pst.close();
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }

        } else if (action.equalsIgnoreCase("delete")) {
            System.out.print("Enter username to delete: ");
            String uname = sc.nextLine();

            // Start transaction
            con.setAutoCommit(false);
            try {
                // Delete from complaints table first (due to foreign key constraint)
                query = "DELETE FROM complaints WHERE user_username = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, uname);
                int complaintResult = pst.executeUpdate();
                pst.close();

                // Delete from user table using stored procedure
                cst = con.prepareCall("{CALL DeleteUser(?)}");
                cst.setString(1, uname);
                boolean hasResults = cst.execute();
                
                // Get the result set from the stored procedure
                int userResult = 0;
                if (hasResults) {
                    try (ResultSet rs = cst.getResultSet()) {
                        if (rs.next()) {
                            userResult = rs.getInt("affected_rows");
                        }
                    }
                }
                cst.close();

                if (userResult > 0) {
                    con.commit();
                    System.out.println("User deleted successfully from both user and complaints tables.");
                    System.out.println("Deleted " + complaintResult + " complaint records associated with the user.");
                    activityLog.logActivity("USER_DELETE", "Deleted user: " + uname + " from both tables");
                } else {
                    con.rollback();
                    System.out.println("No user found.");
                }
            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error deleting user: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void ManageAllOfficers() throws Exception {
        if (!loggedIn) {
            System.out.println(" Admin must be logged in to manage officers.");
            return;
        }

        System.out.println("Add or delete officer? (add/delete): ");
        String action = sc.nextLine();

        if (action.equalsIgnoreCase("add")) {
            System.out.println("\nAdding new officer...");
            displayValidationRules();
            System.out.println("\n--- Enter Officer Details ---");

            String uname = getValidUsername("Enter username: ", true); // true for officer
            String pass = getValidPassword("Enter password: ");
            System.out.print("Enter area: ");
            String area = sc.nextLine();
            area = validateAndNormalizeArea(area);
            System.out.print("Enter category: ");
            String category = sc.nextLine();
            category = validateAndNormalizeCategory(category);

            cst = con.prepareCall("{CALL InsertOfficer(?, ?, ?, ?)}");
            cst.setString(1, uname);
            cst.setString(2, pass);
            cst.setString(3, area);
            cst.setString(4, category);

            // Execute the stored procedure and get the result set
            boolean hasResults = cst.execute();
            int result = 0;
            
            if (hasResults) {
                try (ResultSet rs = cst.getResultSet()) {
                    if (rs.next()) {
                        result = rs.getInt("affected_rows");
                    }
                }
            }
            
            if (result > 0) {
                System.out.println("Officer added successfully.");
                activityLog.logActivity("OFFICER_ADD", "Added officer: " + uname + " for " + area + "-" + category);
            } else {
                System.out.println("Failed to add officer. The username might already exist.");
            }

            cst.close();

        } else if (action.equalsIgnoreCase("delete")) {
            System.out.print("Enter username to delete: ");
            String uname = sc.nextLine();

            cst = con.prepareCall("{CALL DeleteOfficer(?)}");
            cst.setString(1, uname);

            // Execute the stored procedure and get the result set
            boolean hasResults = cst.execute();
            int result = 0;
            
            if (hasResults) {
                try (ResultSet rs = cst.getResultSet()) {
                    if (rs.next()) {
                        result = rs.getInt("affected_rows");
                    }
                }
            }
            
            if (result > 0) {
                System.out.println("Officer deleted successfully.");
                activityLog.logActivity("OFFICER_DELETE", "Deleted officer: " + uname);
            } else {
                System.out.println("No officer found with that username.");
            }

            cst.close();
        }
    }

    // Advanced complaint viewing with complex DBMS queries
    public void viewAdvancedComplaintAnalytics() throws Exception {
        System.out.println("=== Advanced Complaint Analytics Dashboard ===");

        // Query 1: Complex JOIN with subqueries and window functions
        query = "SELECT u.area, u.category, u.complain_id, u.username, u.complain, u.status, " +
                "       o.username as officer_name, " +
                "       ROW_NUMBER() OVER (PARTITION BY u.area, u.category ORDER BY u.complain_id) as area_complaint_rank, " +
                "       COUNT(*) OVER (PARTITION BY u.area, u.category) as area_category_total, " +
                "       AVG(CASE WHEN u.status = 'Resolved' THEN 1 ELSE 0 END) OVER (PARTITION BY u.area) as area_resolution_rate " +
                "FROM user u " +
                "LEFT JOIN officer o ON u.assigned_officer = o.username " +
                "WHERE u.complain IS NOT NULL " +
                "ORDER BY u.area, u.category, u.complain_id";

        pst = con.prepareStatement(query);
        rs = pst.executeQuery();

        String currentArea = "";
        String currentCategory = "";

        while (rs.next()) {
            String area = rs.getString("area");
            String category = rs.getString("category");

            // Print headers when area/category changes
            if (!area.equals(currentArea) || !category.equals(currentCategory)) {
                if (!currentArea.isEmpty()) System.out.println();

                System.out.println("=== " + area + " - " + category + " ===");
                System.out.println("Total in this category: " + rs.getInt("area_category_total"));
                System.out.printf("Area resolution rate: %.2f%%\n", rs.getDouble("area_resolution_rate") * 100);
                System.out.printf("%-6s %-10s %-16s %-16s %-20s%n", "Rank", "ID", "User", "Status", "Officer");
                System.out.println("----------------------------------------------------------------------------");

                currentArea = area;
                currentCategory = category;
            }

            System.out.printf("%-6d %-10d %-16s %-16s %-20s%n",
                    rs.getInt("area_complaint_rank"),
                    rs.getInt("complain_id"),
                    rs.getString("username"),
                    rs.getString("status"),
                    rs.getString("officer_name") != null ? rs.getString("officer_name") : "Unassigned");
        }

        rs.close();
        pst.close();

        activityLog.logActivity("ANALYTICS", "Viewed advanced complaint analytics");
    }

    public void viewAllComplaints() throws Exception {
        // Simple view for basic functionality
        query = "SELECT complain_id, complain FROM user WHERE complain IS NOT NULL";
        pst = con.prepareStatement(query);
        rs = pst.executeQuery();

        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("complain_id"));
            System.out.println("Complaint: " + rs.getString("complain"));
            System.out.println("--------------------");
        }

        rs.close();
        pst.close();

        activityLog.logActivity("VIEW_COMPLAINTS", "Viewed all complaints summary");
    }

    // Enhanced export with advanced queries
    public void exportAdvancedComplaintsToCSV() {
        try (FileWriter writer = new FileWriter("advanced_complaints_report.csv")) {
            // Complex query with multiple JOINs and analytics
            query = "SELECT u.complain_id, u.username, u.email, u.area, u.category, " +
                    "       u.complain, u.status, u.assigned_officer, " +
                    "       o.username as officer_name, " +
                    "       DATEDIFF(CURDATE(), '2025-01-01') as days_in_system, " +
                    "       (SELECT COUNT(*) FROM user WHERE area = u.area AND category = u.category) as area_category_load, " +
                    "       CASE " +
                    "           WHEN u.status = 'Resolved' THEN 'Completed' " +
                    "           WHEN u.status = 'In Progress' THEN 'Active' " +
                    "           ELSE 'Waiting' " +
                    "       END as status_category " +
                    "FROM user u " +
                    "LEFT JOIN officer o ON u.assigned_officer = o.username " +
                    "WHERE u.complain IS NOT NULL " +
                    "ORDER BY u.area, u.category, " +
                    "CASE " +
                    "    WHEN u.status = 'Pending' THEN 1 " +
                    "    WHEN u.status = 'In Progress' THEN 2 " +
                    "    ELSE 3 " +
                    "END";

            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            // Write enhanced CSV header
            writer.append("ComplaintID,Username,Email,Area,Category,Description,Status,StatusCategory,")
                    .append("AssignedOfficer,OfficerName,DaysInSystem,AreaCategoryLoad\n");

            while (rs.next()) {
                writer.append(rs.getInt("complain_id") + ",");
                writer.append(rs.getString("username") + ",");
                writer.append(rs.getString("email") + ",");
                writer.append(rs.getString("area") + ",");
                writer.append(rs.getString("category") + ",");
                writer.append("\"" + rs.getString("complain") + "\",");
                writer.append(rs.getString("status") + ",");
                writer.append(rs.getString("status_category") + ",");
                writer.append((rs.getString("assigned_officer") != null ? rs.getString("assigned_officer") : "Unassigned") + ",");
                writer.append((rs.getString("officer_name") != null ? rs.getString("officer_name") : "N/A") + ",");
                writer.append(rs.getInt("days_in_system") + ",");
                writer.append(rs.getInt("area_category_load") + "\n");
            }

            System.out.println("Advanced complaints report exported to 'advanced_complaints_report.csv'");
            activityLog.logActivity("EXPORT", "Exported advanced complaints report");

            rs.close();
            stmt.close();

        } catch (IOException | SQLException e) {
            System.out.println("Error exporting advanced complaints: " + e.getMessage());
        }
    }

    public void exportResolvedComplaintsToCSV() {
        try (FileWriter writer = new FileWriter("resolved_complaints.csv")) {
            query = "SELECT * FROM user WHERE status='Resolved' AND complain IS NOT NULL";
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            writer.append("ComplaintID,Username,Area,Category,Description,Status\n");
            while (rs.next()) {
                writer.append(rs.getInt("complain_id") + ",");
                writer.append(rs.getString("username") + ",");
                writer.append(rs.getString("area") + ",");
                writer.append(rs.getString("category") + ",");
                writer.append("\"" + rs.getString("complain") + "\",");
                writer.append(rs.getString("status") + "\n");
            }

            System.out.println("Resolved complaints exported to 'resolved_complaints.csv'");
            activityLog.logActivity("EXPORT", "Exported resolved complaints");

            rs.close();
            stmt.close();

        } catch (IOException | SQLException e) {
            System.out.println("Error exporting resolved complaints: " + e.getMessage());
        }
    }

    // New Feature: System Performance Dashboard
    public void viewSystemPerformanceDashboard() throws SQLException {
        System.out.println("=== System Performance Dashboard ===");

        // Overall Statistics
        query = "SELECT " +
                "COUNT(*) as total_complaints, " +
                "COUNT(DISTINCT username) as total_users, " +
                "COUNT(DISTINCT area) as total_areas, " +
                "AVG(CASE WHEN status = 'Resolved' THEN 1 ELSE 0 END) * 100 as overall_resolution_rate, " +
                "COUNT(CASE WHEN status = 'Pending' THEN 1 END) as pending_complaints " +
                "FROM user WHERE complain IS NOT NULL";

        pst = con.prepareStatement(query);
        rs = pst.executeQuery();

        if (rs.next()) {
            System.out.println(" SYSTEM OVERVIEW:");
            System.out.println("Total Complaints: " + rs.getInt("total_complaints"));
            System.out.println("Active Users: " + rs.getInt("total_users"));
            System.out.println("Coverage Areas: " + rs.getInt("total_areas"));
            System.out.printf("Resolution Rate: %.2f%%\n", rs.getDouble("overall_resolution_rate"));
            System.out.println("Pending Complaints: " + rs.getInt("pending_complaints"));
        }

        rs.close();
        pst.close();

        // Officer Performance Rankings
        System.out.println("\n TOP PERFORMING OFFICERS:");
        query = "SELECT o.username, o.category, " +
                "       COUNT(u.complain_id) as total_assigned, " +
                "       COUNT(CASE WHEN u.status = 'Resolved' THEN 1 END) as resolved, " +
                "       ROUND(COUNT(CASE WHEN u.status = 'Resolved' THEN 1 END) * 100.0 / COUNT(u.complain_id), 2) as success_rate " +
                "FROM officer o " +
                "LEFT JOIN user u ON o.username = u.assigned_officer " +
                "WHERE u.complain_id IS NOT NULL " +
                "GROUP BY o.username, o.category " +
                "HAVING total_assigned > 0 " +
                "ORDER BY success_rate DESC, resolved DESC " +
                "LIMIT 10";

        pst = con.prepareStatement(query);
        rs = pst.executeQuery();

        System.out.println("OFFICER              CATEGORY     ASSIGNED  RESOLVED  SUCCESS RATE");
        System.out.println("------------------------------------------------------------");

        while (rs.next()) {
            String username = rs.getString("username");
            if (username.length() > 18) {
                username = username.substring(0, 15) + "...";
            }
            
            String category = rs.getString("category");
            if (category.length() > 9) {
                category = category.substring(0, 8) + "..";
            }
            
            System.out.printf("%-18s %-9s %9d %9d %12.2f%%\n",
                    username,
                    category,
                    rs.getInt("total_assigned"),
                    rs.getInt("resolved"),
                    rs.getDouble("success_rate"));
        }

        rs.close();
        pst.close();

        // Display from graph structure
        System.out.println("\n SYSTEM METRICS FROM GRAPH:");
        systemGraph.displaySystemOverview();
        systemGraph.findCriticalAreas();

        activityLog.logActivity("DASHBOARD", "Viewed system performance dashboard");
    }



    // New Feature: Data Structure Management
    public void manageDataStructures() {
        System.out.println("=== Data Structure Management ===");
        System.out.println("1. View System Graph");
        System.out.println("2. View Activity Log");
        System.out.println("3. Refresh DS from Database");
        System.out.println("0. Back to Main Menu");

        int choice = sc.nextInt(); sc.nextLine();

        switch (choice) {
            case 1:
                systemGraph.displaySystemOverview();
                systemGraph.findCriticalAreas();
                break;
            case 2:
                System.out.print("How many recent activities to show? ");
                int count = sc.nextInt();
                sc.nextLine(); // consume newline
                activityLog.displayRecentActivity(count);
                break;
            case 3:
                try {
                    refreshDataStructuresFromDatabase();
                } catch (SQLException e) {
                    System.out.println("Failed to refresh DS: " + e.getMessage());
                }
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Refresh all Admin-owned data structures from the latest database state
    private void refreshDataStructuresFromDatabase() throws SQLException {
        System.out.println("Refreshing data structures from database...");

        // Re-initialize structures
        systemGraph = new SystemMetricsGraph(20);
        reportCache = new ReportGeneratorHashMap(100);

        // Reload metrics into the graph
        loadSystemMetrics();

        System.out.println("Data structures refreshed from latest DB state.");
        activityLog.logActivity("DS_REFRESH", "Refreshed Admin DS from database");
    }

    // Generate comprehensive system report
    public void generateComprehensiveSystemReport() throws SQLException {
        System.out.println("=== Generating Comprehensive System Report ===");

        String reportKey = "comprehensive_" + System.currentTimeMillis();
        StringBuilder reportBuilder = new StringBuilder();

        // Check cache first
        String cachedReport = reportCache.getReport("comprehensive_latest");
        if (cachedReport != null) {
            System.out.println("Found cached report. Use cached version? (y/n)");
            if (sc.nextLine().toLowerCase().startsWith("y")) {
                System.out.println(cachedReport);
                return;
            }
        }

        reportBuilder.append("=== COMPREHENSIVE SYSTEM REPORT ===\n");
        reportBuilder.append("Generated at: ").append(new Date()).append("\n\n");

        // System Statistics with complex query
        query = "SELECT " +
                "    (SELECT COUNT(*) FROM user WHERE complain IS NOT NULL) as total_complaints, " +
                "    (SELECT COUNT(DISTINCT username) FROM user) as total_users, " +
                "    (SELECT COUNT(*) FROM officer) as total_officers, " +
                "    (SELECT COUNT(DISTINCT area) FROM user) as areas_covered, " +
                "    (SELECT AVG(CASE WHEN status = 'Resolved' THEN 1 ELSE 0 END) * 100 FROM user WHERE complain IS NOT NULL) as avg_resolution_rate";

        pst = con.prepareStatement(query);
        rs = pst.executeQuery();

        if (rs.next()) {
            reportBuilder.append("SYSTEM STATISTICS:\n");
            reportBuilder.append("- Total Complaints: ").append(rs.getInt("total_complaints")).append("\n");
            reportBuilder.append("- Total Users: ").append(rs.getInt("total_users")).append("\n");
            reportBuilder.append("- Total Officers: ").append(rs.getInt("total_officers")).append("\n");
            reportBuilder.append("- Areas Covered: ").append(rs.getInt("areas_covered")).append("\n");
            reportBuilder.append("- System Resolution Rate: ").append(String.format("%.2f%%", rs.getDouble("avg_resolution_rate"))).append("\n\n");
        }

        rs.close();
        pst.close();

        String report = reportBuilder.toString();

        // Cache the report
        reportCache.putReport("comprehensive_latest", report);
        reportCache.putReport(reportKey, report);

        System.out.println(report);

        activityLog.logActivity("REPORT_GEN", "Generated comprehensive system report");
    }

    public void logout() {
        activityLog.logActivity("LOGOUT", "Admin logged out of system");
        loggedIn = false;
        username = null;
        System.out.println("Admin logged out.");
    }

    public void testDatabaseConnection() {
        System.out.println("=== Testing Database Connection ===");
        
        try {
            // Test basic connection
            if (con != null && !con.isClosed()) {
                System.out.println(" Database connection is active");
                
                // Test if InsertUser stored procedure exists
                try {
                    DatabaseMetaData metaData = con.getMetaData();
                    ResultSet procedures = metaData.getProcedures(null, null, "InsertUser");
                    if (procedures.next()) {
                        System.out.println(" InsertUser stored procedure exists");
                        System.out.println("  - Procedure name: " + procedures.getString("PROCEDURE_NAME"));
                        System.out.println("  - Schema: " + procedures.getString("PROCEDURE_SCHEM"));
                        System.out.println("  - Remarks: " + procedures.getString("REMARKS"));
                    } else {
                        System.out.println(" InsertUser stored procedure does not exist");
                    }
                    procedures.close();
                    
                    // Get stored procedure parameters
                    ResultSet params = metaData.getProcedureColumns(null, null, "InsertUser", null);
                    System.out.println(" InsertUser parameters:");
                    while (params.next()) {
                        String paramName = params.getString("COLUMN_NAME");
                        String paramType = params.getString("TYPE_NAME");
                        int paramMode = params.getInt("COLUMN_TYPE");
                        String mode = "";
                        switch (paramMode) {
                            case DatabaseMetaData.procedureColumnIn: mode = "IN"; break;
                            case DatabaseMetaData.procedureColumnOut: mode = "OUT"; break;
                            case DatabaseMetaData.procedureColumnInOut: mode = "INOUT"; break;
                            default: mode = "UNKNOWN"; break;
                        }
                        System.out.println("  - " + paramName + " (" + paramType + ", " + mode + ")");
                    }
                    params.close();
                    
                } catch (SQLException e) {
                    System.out.println(" Error checking stored procedures: " + e.getMessage());
                }
                
                // Test if tables exist and show structure
                try {
                    DatabaseMetaData metaData = con.getMetaData();
                    ResultSet tables = metaData.getTables(null, null, "user", null);
                    if (tables.next()) {
                        System.out.println(" User table exists");
                        // Show table structure
                        ResultSet columns = metaData.getColumns(null, null, "user", null);
                        System.out.println(" User table structure:");
                        while (columns.next()) {
                            String colName = columns.getString("COLUMN_NAME");
                            String colType = columns.getString("TYPE_NAME");
                            String isNullable = columns.getString("IS_NULLABLE");
                            String defaultValue = columns.getString("COLUMN_DEF");
                            System.out.println("  - " + colName + " (" + colType + ") " + 
                                             (isNullable.equals("YES") ? "NULL" : "NOT NULL") +
                                             (defaultValue != null ? " DEFAULT " + defaultValue : ""));
                        }
                        columns.close();
                    } else {
                        System.out.println(" User table does not exist");
                    }
                    tables.close();
                    
                    tables = metaData.getTables(null, null, "complaints", null);
                    if (tables.next()) {
                        System.out.println(" Complaints table exists");
                    } else {
                        System.out.println(" Complaints table does not exist");
                    }
                    tables.close();
                } catch (SQLException e) {
                    System.out.println(" Error checking tables: " + e.getMessage());
                }
                
                // Test direct SQL insertion
                System.out.println("\n Testing direct SQL insertion...");
                try {
                    String query = "INSERT INTO user (username, password, email, area) VALUES (?, ?, ?, ?)";
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setString(1, "test_user_" + System.currentTimeMillis());
                    pst.setString(2, "TestPass123");
                    pst.setString(3, "test@example.com");
                    pst.setString(4, "Paldi");
                    
                    int testResult = pst.executeUpdate();
                    System.out.println(" Direct SQL insertion test result: " + testResult);
                    
                    // Clean up test data
                    query = "DELETE FROM user WHERE username LIKE 'test_user_%'";
                    pst = con.prepareStatement(query);
                    int deleteResult = pst.executeUpdate();
                    System.out.println(" Cleaned up " + deleteResult + " test records");
                    pst.close();
                    
                } catch (SQLException e) {
                    System.out.println(" Direct SQL insertion test failed: " + e.getMessage());
                }
            } else {
                System.out.println(" Database connection is not active");
            }
        } catch (SQLException e) {
            System.out.println(" Database connection test failed: " + e.getMessage());
        }
    }
    
    // Test InsertUser stored procedure specifically
    public void testInsertUserStoredProcedure() {
        try {
            System.out.println("=== Testing InsertUser Stored Procedure ===");
            
            // Test with sample data
            String testUsername = "test_user_" + System.currentTimeMillis();
            String testPassword = "TestPass123";
            String testEmail = "test@example.com";
            String testAddress = "123 Test Street, Test City";
            String testArea = "Paldi";
            
            System.out.println("Testing with:");
            System.out.println("  Username: " + testUsername);
            System.out.println("  Password: " + testPassword);
            System.out.println("  Email: " + testEmail);
            System.out.println("  Address: " + testAddress);
            System.out.println("  Area: " + testArea);
            
            // Test stored procedure
            try {
                cst = con.prepareCall("{CALL InsertUser(?, ?, ?, ?, ?)}");
                cst.setString(1, testUsername);
                cst.setString(2, testPassword);
                cst.setString(3, testEmail);
                cst.setString(4, testAddress);
                cst.setString(5, testArea);
                
                System.out.println("\nExecuting stored procedure...");
                int result = cst.executeUpdate();
                System.out.println("Stored procedure result: " + result);
                
                if (result == -1) {
                    System.out.println(" Stored procedure returned -1 (failure)");
                    
                    // Check for warnings
                    SQLWarning warnings = cst.getWarnings();
                    if (warnings != null) {
                        System.out.println("Warnings:");
                        while (warnings != null) {
                            System.out.println("  - " + warnings.getMessage());
                            warnings = warnings.getNextWarning();
                        }
                    }
                    
                    // Check if user was actually inserted
                    query = "SELECT COUNT(*) FROM user WHERE username = ?";
                    pst = con.prepareStatement(query);
                    pst.setString(1, testUsername);
                    rs = pst.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("️ User was actually inserted despite -1 result");
                        System.out.println("This suggests the stored procedure has an issue but data is inserted");
                    } else {
                        System.out.println(" User was NOT inserted");
                    }
                    rs.close();
                    pst.close();
                } else if (result > 0) {
                    System.out.println(" Stored procedure succeeded");
                }
                
                cst.close();
                
            } catch (SQLException e) {
                System.out.println(" Stored procedure execution failed:");
                System.out.println("  Error: " + e.getMessage());
                System.out.println("  SQL State: " + e.getSQLState());
                System.out.println("  Error Code: " + e.getErrorCode());
            }
            
            // Clean up test data
            try {
                query = "DELETE FROM user WHERE username = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, testUsername);
                int deleteResult = pst.executeUpdate();
                System.out.println("\n🧹 Cleaned up test user: " + deleteResult + " records deleted");
                pst.close();
            } catch (SQLException e) {
                System.out.println(" Could not clean up test data: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println(" Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Display validation rules for users
    public void displayValidationRules() {
        System.out.println("\n=== USERNAME AND PASSWORD VALIDATION RULES ===");
        System.out.println("USERNAME RULES:");
        System.out.println("• Must be 3-20 characters long");
        System.out.println("• Can only contain letters (a-z, A-Z), numbers (0-9), and underscores (_)");
        System.out.println("• Cannot contain only numbers and underscores - must include at least one letter");
        System.out.println("• Cannot be empty or contain spaces");
        System.out.println("• Must be unique (not already used by another user)");

        System.out.println("\nPASSWORD RULES:");
        System.out.println("• Must be at least 8 characters long (max 50)");
        System.out.println("• Must contain at least one uppercase letter (A-Z)");
        System.out.println("• Must contain at least one lowercase letter (a-z)");
        System.out.println("• Must contain at least one number (0-9)");
        System.out.println("• Cannot contain spaces");
                        System.out.println("• Must be confirmed (entered twice)");
        
        System.out.println("\nADDRESS RULES:");
        System.out.println("• Must be at least 5 characters long");
        System.out.println("• Must not exceed 200 characters");
        System.out.println("• Cannot be null or empty");
        
        System.out.println("\nAREA RULES:");
        System.out.println("• Must be one of: Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura");

        System.out.println("\nCATEGORY RULES (for officers):");
        System.out.println("• Must be one of: Drainage, Water, Electricity, Road Maintenance, Other");

        System.out.println("\nEMAIL RULES:");
        System.out.println("• Must contain '@' and either '.com' or '.in'");
        System.out.println("• Cannot be null or empty");
    }

    // Check if username is available (for both users and officers)
    public boolean isUsernameAvailable(String username, boolean isOfficer) {
        try {
            if (isOfficer) {
                // Check both user and officer tables for officers
                query = "SELECT COUNT(*) FROM user WHERE username = ? UNION SELECT COUNT(*) FROM officer WHERE username = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, username);
                rs = pst.executeQuery();
                int totalCount = 0;
                while (rs.next()) {
                    totalCount += rs.getInt(1);
                }
                rs.close();
                pst.close();
                return totalCount == 0;
            } else {
                // Check only user table for regular users
                query = "SELECT COUNT(*) FROM user WHERE username = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, username);
                rs = pst.executeQuery();
                boolean available = rs.next() && rs.getInt(1) == 0;
                rs.close();
                pst.close();
                return available;
            }
        } catch (SQLException e) {
            System.out.println(" Database error checking username availability: " + e.getMessage());
            return false;
        }
    }

    // Provide helpful validation feedback
    public void provideValidationHelp(String field, String value) {
        System.out.println("\n VALIDATION HELP for " + field.toUpperCase() + ":");

        switch (field.toLowerCase()) {
            case "username":
                System.out.println("• Current value: '" + value + "'");
                if (value.length() < 3) {
                    System.out.println("• Too short! Add " + (3 - value.length()) + " more characters");
                } else if (value.length() > 20) {
                    System.out.println("• Too long! Remove " + (value.length() - 20) + " characters");
                }
                if (!value.matches("^[a-zA-Z0-9_]+$")) {
                    System.out.println("• Contains invalid characters. Only letters, numbers, and underscores allowed");
                }
                if (value.matches("^[0-9_]+$")) {
                    System.out.println("• Contains only numbers and underscores. Must include at least one letter (a-z, A-Z)");
                }
                break;

            case "password":
                System.out.println("• Current value: '" + "*".repeat(value.length()) + "'");
                if (value.length() < 8) {
                    System.out.println("• Too short! Add " + (8 - value.length()) + " more characters");
                }
                if (!value.matches(".*[A-Z].*")) {
                    System.out.println("• Missing uppercase letter (A-Z)");
                }
                if (!value.matches(".*[a-z].*")) {
                    System.out.println("• Missing lowercase letter (a-z)");
                }
                if (!value.matches(".*\\d.*")) {
                    System.out.println("• Missing number (0-9)");
                }
                if (value.contains(" ")) {
                    System.out.println("• Remove spaces from password");
                }
                break;

            case "area":
                System.out.println("• Current value: '" + value + "'");
                System.out.println("• Valid areas: Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura");
                break;

            case "category":
                System.out.println("• Current value: '" + value + "'");
                System.out.println("• Valid categories: Drainage, Water, Electricity, Road Maintenance, Other");
                break;

            case "email":
                System.out.println("• Current value: '" + value + "'");
                if (!value.contains("@")) {
                    System.out.println("• Missing @ symbol");
                }
                if (!value.contains(".com") && !value.contains(".in")) {
                    System.out.println("• Missing .com or .in domain");
                }
                break;
        }

        System.out.println("• Use 'displayValidationRules' command to see all rules");
    }

    // assignOfficerToComplaint method is implemented below with transaction handling

    // New Feature: Reassign overdue complaints to another officer
    public void reassignOverdueComplaints() throws SQLException {
        System.out.println("=== Reassign Overdue Complaints ===");
        
        // First, show current officer workload
        query = "SELECT o.username, o.category, " +
                "       COUNT(CASE WHEN c.status = 'Assigned' THEN 1 END) AS active, " +
                "       COUNT(CASE WHEN c.status = 'Resolved' THEN 1 END) AS resolved " +
                "FROM officer o " +
                "LEFT JOIN complaints c ON c.officer_username = o.username " +
                "GROUP BY o.username, o.category " +
                "ORDER BY active DESC, resolved DESC";
                
        pst = con.prepareStatement(query);
        rs = pst.executeQuery();

        System.out.println("\nCurrent Officer Workload:");
        System.out.printf("%-20s %-15s %-10s %-10s%n", "Officer", "Category", "Active", "Resolved");
        System.out.println("--------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-20s %-15s %-10d %-10d%n",
                    rs.getString("username"),
                    rs.getString("category"),
                    rs.getInt("active"),
                    rs.getInt("resolved"));
        }
        rs.close();
        pst.close();

        // List unassigned complaints to pick from
        System.out.println("\nUnassigned Complaints:");
        query = "SELECT c.complain_id, c.user_username, c.area, c.category, c.description " +
                "FROM complaints c " +
                "WHERE (c.officer_username IS NULL) " +
                "  AND c.category IS NOT NULL " +
                "  AND (c.status IS NULL OR c.status <> 'No Complaint') " +
                "ORDER BY c.complain_id DESC";
        pst = con.prepareStatement(query);
        rs = pst.executeQuery();
        boolean have = false;
        while (rs.next()) {
            have = true;
            String desc = rs.getString("description");
            if (desc == null) desc = "";
            String preview = desc.length() > 30 ? desc.substring(0, 30) + "..." : desc;
            System.out.println(rs.getInt("complain_id") + "\t" + rs.getString("user_username") + "\t" + rs.getString("area") + "\t" + rs.getString("category") + "\t" + preview);
        }
        rs.close();
        pst.close();

        if (!have) {
            System.out.println("No unassigned complaints found.");
            return;
        }

        System.out.print("\nEnter complaint ID to assign: ");
        int complaintId = sc.nextInt();
        sc.nextLine();

        // Fetch complaint category
        query = "SELECT category FROM complaints WHERE complain_id = ? AND (officer_username IS NULL)";
        pst = con.prepareStatement(query);
        pst.setInt(1, complaintId);
        rs = pst.executeQuery();
        if (!rs.next()) {
            System.out.println("Invalid complaint ID or already assigned.");
            rs.close();
            pst.close();
            return;
        }
        String category = rs.getString("category");
        rs.close();
        pst.close();

        // Show officers for the category with workload numbers
        query = "SELECT o.username, o.category, " +
                "       COUNT(CASE WHEN c.status IS NOT NULL AND c.status <> 'Resolved' THEN 1 END) AS active " +
                "FROM officer o " +
                "LEFT JOIN complaints c ON c.officer_username = o.username " +
                "WHERE o.category = ? " +
                "GROUP BY o.username, o.category " +
                "ORDER BY active ASC, o.username ASC"; // least loaded first
        pst = con.prepareStatement(query);
        pst.setString(1, category);
        rs = pst.executeQuery();
        System.out.println("\nAvailable Officers (least loaded first):");
        System.out.printf("%-20s %-12s %-8s%n", "Officer", "Category", "Active");
        while (rs.next()) {
            System.out.printf("%-20s %-12s %-8d%n", rs.getString("username"), rs.getString("category"), rs.getInt("active"));
        }
        rs.close();
        pst.close();

        System.out.print("Enter officer username to assign: ");
        String officerUsername = sc.nextLine();

        // Verify officer exists for category
        query = "SELECT 1 FROM officer WHERE username = ? AND category = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, officerUsername);
        pst.setString(2, category);
        rs = pst.executeQuery();
        if (!rs.next()) {
            System.out.println("Invalid officer selection.");
            rs.close();
            pst.close();
            return;
        }
        rs.close();
        pst.close();

        // Perform assignment using the same synced transaction logic
        try {
            con.setAutoCommit(false);

            query = "UPDATE complaints SET officer_username = ?, status = 'Assigned' WHERE complain_id = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, officerUsername);
            pst.setInt(2, complaintId);
            int cUpd = pst.executeUpdate();
            pst.close();

            query = "UPDATE user SET assigned_officer = ? WHERE username = (SELECT user_username FROM complaints WHERE complain_id = ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, officerUsername);
            pst.setInt(2, complaintId);
            int uUpd = pst.executeUpdate();
            pst.close();

            if (cUpd > 0 && uUpd > 0) {
                con.commit();
                System.out.println(" Successfully assigned officer " + officerUsername + " to complaint #" + complaintId);
                activityLog.logActivity("OFFICER_REASSIGN", "Reassigned complaint #" + complaintId + " to " + officerUsername);
            } else {
                con.rollback();
                System.out.println(" Failed to assign officer. Please try again.");
            }
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println(" Error during rollback: " + ex.getMessage());
            }
            System.out.println(" Error assigning officer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(" Error resetting auto-commit: " + e.getMessage());
            }
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    public void assignOfficerToComplaint() throws SQLException {
        System.out.println("=== Assign Officer to Complaint ===");
        
        // List unassigned complaints
        query = "SELECT c.complain_id, c.description, c.category, c.area, c.status, c.timestamp, " +
                "u.username as user_username, u.area as user_area, u.address as user_address, u.phone as user_phone " +
                "FROM complaints c " +
                "JOIN user u ON c.user_username = u.username " +
                "WHERE c.status = 'Pending' AND c.officer_username IS NULL";
                
        pst = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = pst.executeQuery();
        
        if (!rs.isBeforeFirst()) {
            System.out.println("No unassigned complaints found.");
            rs.close();
            pst.close();
            return;
        }
        
        // Display unassigned complaints
        System.out.println("\nUnassigned Complaints:");
        System.out.println("ID  | Category      | Area      | Description");
        System.out.println("----|---------------|-----------|------------");
        
        while (rs.next()) {
            System.out.printf("%-4d| %-14s| %-10s| %s\n", 
                rs.getInt("complain_id"),
                rs.getString("category"),
                rs.getString("area"),
                rs.getString("description").substring(0, Math.min(30, rs.getString("description").length())) + 
                    (rs.getString("description").length() > 30 ? "..." : "")
            );
        }
        
        // Get complaint ID to assign
        System.out.print("\nEnter complaint ID to assign an officer: ");
        int complaintId = Integer.parseInt(sc.nextLine().trim());
        
        // Find the selected complaint
        rs.beforeFirst();
        boolean found = false;
        String category = "";
        
        while (rs.next()) {
            if (rs.getInt("complain_id") == complaintId) {
                found = true;
                category = rs.getString("category");
                break;
            }
        }
        
        if (!found) {
            System.out.println("Invalid complaint ID.");
            rs.close();
            pst.close();
            return;
        }
        
        // Show available officers for the complaint's category
        query = "SELECT o.username, o.category, " +
                "COUNT(CASE WHEN c.status IS NOT NULL AND c.status <> 'Resolved' THEN 1 END) AS active " +
                "FROM officer o " +
                "LEFT JOIN complaints c ON c.officer_username = o.username " +
                "WHERE o.category = ? " +
                "GROUP BY o.username, o.category " +
                "ORDER BY active ASC, o.username ASC";
                
        pst = con.prepareStatement(query);
        pst.setString(1, category);
        rs = pst.executeQuery();
        
        System.out.println("\nAvailable Officers (least loaded first):");
        System.out.printf("%-20s %-12s %-8s%n", "Officer", "Category", "Active");
        
        while (rs.next()) {
            System.out.printf("%-20s %-12s %-8d%n", 
                rs.getString("username"), 
                rs.getString("category"), 
                rs.getInt("active")
            );
        }
        
        if (!rs.isBeforeFirst()) {
            System.out.println("No officers available for this category.");
            rs.close();
            pst.close();
            return;
        }
        
        rs.beforeFirst();
        System.out.print("\nEnter officer username to assign: ");
        String officerUsername = sc.nextLine().trim();
        
        // Verify officer exists in the category
        boolean validOfficer = false;
        rs.beforeFirst();
        while (rs.next()) {
            if (rs.getString("username").equalsIgnoreCase(officerUsername)) {
                validOfficer = true;
                break;
            }
        }
        
        if (!validOfficer) {
            System.out.println("Invalid officer selection.");
            rs.close();
            pst.close();
            return;
        }
        
        // Perform the assignment
        try {
            con.setAutoCommit(false);
            
            // Update complaints table
            query = "UPDATE complaints SET officer_username = ?, status = 'Assigned' WHERE complain_id = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, officerUsername);
            pst.setInt(2, complaintId);
            int complaintsUpdated = pst.executeUpdate();
            pst.close();
            
            // Update user table
            query = "UPDATE user SET assigned_officer = ?, status = 'Assigned' WHERE username = (";
            query += "SELECT user_username FROM complaints WHERE complain_id = ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, officerUsername);
            pst.setInt(2, complaintId);
            int usersUpdated = pst.executeUpdate();
            pst.close();
            
            if (complaintsUpdated > 0 && usersUpdated > 0) {
                con.commit();
                System.out.println(" Successfully assigned officer " + officerUsername + " to complaint #" + complaintId);
                activityLog.logActivity("OFFICER_ASSIGN", "Assigned " + officerUsername + " to complaint #" + complaintId);
            } else {
                con.rollback();
                System.out.println(" Failed to assign officer. Please try again.");
            }
            
        } catch (SQLException e) {
            con.rollback();
            System.out.println(" Error during assignment: " + e.getMessage());
            e.printStackTrace();
        } finally {
            con.setAutoCommit(true);
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }
    
    private String getValidUsername(String prompt) {
        return getValidUsername(prompt, false);
    }

    private String getValidUsername(String prompt, boolean isOfficer) {
        while (true) {
            System.out.print(prompt);
            String username = sc.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println(" Username cannot be empty.");
                continue;
            }

            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                System.out.println(" Username must contain only letters, numbers, and underscores.");
                continue;
            }

            // Check if username contains only numbers
            if (username.matches("^[0-9_]+$")) {
                System.out.println(" Username cannot contain only numbers and underscores. Must include at least one letter.");
                continue;
            }

            if (username.length() < 3 || username.length() > 20) {
                System.out.println(" Username must be between 3 and 20 characters long.");
                continue;
            }

            // Check if username already exists in database
            try {
                if (isOfficer) {
                    // Check both user and officer tables for officers
                    query = "SELECT COUNT(*) FROM user WHERE username = ? UNION SELECT COUNT(*) FROM officer WHERE username = ?";
                    pst = con.prepareStatement(query);
                    pst.setString(1, username);
                    pst.setString(2, username);
                    rs = pst.executeQuery();
                    int totalCount = 0;
                    while (rs.next()) {
                        totalCount += rs.getInt(1);
                    }
                    if (totalCount > 0) {
                        System.out.println(" Username '" + username + "' already exists. Please choose a different username.");
                        rs.close();
                        pst.close();
                        continue;
                    }
                    rs.close();
                    pst.close();
                } else {
                    // Check only user table for regular users
                    query = "SELECT COUNT(*) FROM user WHERE username = ?";
                    pst = con.prepareStatement(query);
                    pst.setString(1, username);
                    rs = pst.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println(" Username '" + username + "' already exists. Please choose a different username.");
                        rs.close();
                        pst.close();
                        continue;
                    }
                    rs.close();
                    pst.close();
                }
            } catch (SQLException e) {
                System.out.println(" Database error checking username: " + e.getMessage());
                continue;
            }

            return username;
        }
    }

    public String getValidPassword(String prompt) {
        while (true) {
            System.out.print(prompt);
            String password = sc.nextLine();

            if (password.isEmpty()) {
                System.out.println(" Password cannot be empty.");
                continue;
            }

            if (password.length() < 8) {
                System.out.println(" Password must be at least 8 characters long.");
                continue;
            }

            if (password.length() > 50) {
                System.out.println(" Password must not exceed 50 characters.");
                continue;
            }

            if (password.contains(" ")) {
                System.out.println(" Password cannot contain spaces.");
                continue;
            }

            // Check for at least one uppercase letter, one lowercase letter, and one number
            boolean hasUpper = false, hasLower = false, hasNumber = false;
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) hasUpper = true;
                else if (Character.isLowerCase(c)) hasLower = true;
                else if (Character.isDigit(c)) hasNumber = true;
            }

            if (!hasUpper) {
                System.out.println(" Password must contain at least one uppercase letter.");
                continue;
            }

            if (!hasLower) {
                System.out.println(" Password must contain at least one lowercase letter.");
                continue;
            }

            if (!hasNumber) {
                System.out.println(" Password must contain at least one number.");
                continue;
            }

            return password;
        }
    }

    public void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
