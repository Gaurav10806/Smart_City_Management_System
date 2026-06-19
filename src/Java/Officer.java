package Java;

import DBMS.DBConnection;
import DS.ComplaintBinaryTree;

import DS.WorkloadHashTable;
import java.sql.*;
import java.util.*;

public class Officer {
    private String username;
    private String password;
    private String area;
    private String category;
    private boolean loggedIn = false;

    Connection con;
    Scanner sc = new Scanner(System.in);
    String query;
    ResultSet rs;
    PreparedStatement pst;
    CallableStatement cst;

    // Custom Data Structures for DS Implementation
    ComplaintBinaryTree complaintTree;
    WorkloadHashTable workloadTracker;

    public Officer() throws Exception {
        con = DBConnection.getConnection();

        // Initialize custom DS
        complaintTree = new ComplaintBinaryTree();
        workloadTracker = new WorkloadHashTable(50);
    }



    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean login() {
        System.out.println("--- Officer Login ---");
        String inputUsername = getValidUsername("Enter Username: ");
        String inputPassword = getValidPassword("Enter Password: ");

        try {
            // Step 1: check username exists
            query = "SELECT password, category FROM officer WHERE username=?";
            pst = con.prepareStatement(query);
            pst.setString(1, inputUsername);
            rs = pst.executeQuery();
            if (!rs.next()) {
                System.out.println("Invalid username.");
                rs.close();
                pst.close();
                return false;
            }
            String dbPassword = rs.getString("password");
            String dbCategory = rs.getString("category");
            rs.close();
            pst.close();

            // Step 2: verify password
            if (!Objects.equals(dbPassword, inputPassword)) {
                System.out.println("Incorrect password.");
                return false;
            }

            // success
                username = inputUsername;
                area = "All Areas"; // Officers handle all areas for their category
                category = dbCategory;
                loggedIn = true;

                // Load data into custom data structures
                loadComplaintsIntoTree();
                updateWorkloadTracker();

                System.out.println("Login successful! Welcome Officer " + username);
                System.out.println("Area: " + area + " | Category: " + category);
                return true;
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }

    // Load complaints into BST (DS)
    private void loadComplaintsIntoTree() throws SQLException {
        query = "SELECT complain_id, complain, status FROM user " +
                "WHERE category=? AND complain IS NOT NULL";
        pst = con.prepareStatement(query);
        pst.setString(1, category);
        rs = pst.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("complain_id");
            String desc = rs.getString("complain");
            String status = rs.getString("status");
            complaintTree.insert(id, desc, status, "Medium");
        }
    }

    // Update workload tracker hash table
    private void updateWorkloadTracker() throws SQLException {
        // Complex query with subqueries and GROUP BY for DBMS marks
        query = "SELECT o.username, " +
                "       (SELECT COUNT(*) FROM user u WHERE u.assigned_officer = o.username AND u.status != 'Resolved') as active_complaints, " +
                "       (SELECT COUNT(*) FROM user u WHERE u.assigned_officer = o.username AND u.status = 'Resolved') as resolved_complaints " +
                "FROM officer o " +
                "WHERE o.category = ?";

        pst = con.prepareStatement(query);
        pst.setString(1, category);
        rs = pst.executeQuery();

        while (rs.next()) {
            String officer = rs.getString("username");
            int active = rs.getInt("active_complaints");
            int resolved = rs.getInt("resolved_complaints");

            workloadTracker.put(officer, active, resolved);
        }
    }


    public void viewAssignedComplaints() {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }

        try {
            // Query to get complaints assigned to this officer
            query = "SELECT complain_id, username, complain, status, email, " +
                    "       ROW_NUMBER() OVER (ORDER BY complain_id) as row_num, " +
                    "       COUNT(*) OVER () as total_complaints " +
                    "FROM user " +
                    "WHERE assigned_officer = ? AND complain IS NOT NULL";

            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();

            boolean found = false;
            int complaintCount = 0;
            System.out.println("=== Your Assigned Complaints ===");
            System.out.println(String.format("%-12s %-15s %-30s %-15s %s", 
                "Complaint ID", "User", "Description", "Status", "Email"));
            System.out.println("-".repeat(90));

            while (rs.next()) {
                found = true;
                complaintCount++;
                System.out.println(String.format("%-12d %-15s %-30s %-15s %s",
                    rs.getInt("complain_id"),
                    rs.getString("username"),
                    (rs.getString("complain").length() > 25 ? 
                     rs.getString("complain").substring(0, 22) + "..." : 
                     rs.getString("complain")),
                    rs.getString("status"),
                    rs.getString("email")
                ));
            }

            if (!found) {
                System.out.println("No complaints are currently assigned to you.");
            } else {
                System.out.println("\nTotal assigned complaints: " + complaintCount);
            }

        } catch (SQLException e) {
            System.out.println("Error loading complaints into tree: " + e.getMessage());
        }
    }

    public void updateComplaintStatus(String complaintId) {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }
        
        // Validate complaint ID is numeric
        try {
            Integer.parseInt(complaintId);
        } catch (NumberFormatException e) {
            System.out.println("Error: Complaint ID must be a number");
            return;
        }

        // Status input validation loop
        String status;
        while (true) {
            System.out.print("\nEnter new status (Pending/In Progress/Resolved): ");
            status = sc.nextLine().trim();
            
            if (status.equalsIgnoreCase("Pending") || 
                status.equalsIgnoreCase("In Progress") || 
                status.equalsIgnoreCase("Resolved")) {
                // Convert to title case for consistency
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                if (status.equals("In progress")) {
                    status = "In Progress"; // Fix case for two-word status
                }
                break;
            } else {
                System.out.println("Invalid status. Please enter one of: Pending, In Progress, or Resolved");
            }
        }

        try {
            con.setAutoCommit(false);

            // Pre-check: complaint existence and assignment
            query = "SELECT user_username, officer_username FROM complaints WHERE complain_id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(complaintId));
            rs = pst.executeQuery();
            if (!rs.next()) {
                System.out.println("Complaint not found.");
                rs.close();
                pst.close();
                con.setAutoCommit(true);
                return;
            }
            String complaintUser = rs.getString("user_username");
            String assignedOfficer = rs.getString("officer_username");
            rs.close();
            pst.close();

            if (assignedOfficer == null || !assignedOfficer.equals(username)) {
                System.out.println("This complaint is assigned to: " + (assignedOfficer == null ? "Unassigned" : assignedOfficer) + ". You cannot update it.");
                con.setAutoCommit(true);
                return;
            }

            // Update complaints table directly to avoid CALL rowcount issues
            query = "UPDATE complaints SET status = ? WHERE complain_id = ? AND officer_username = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, status);
            pst.setInt(2, Integer.parseInt(complaintId));
            pst.setString(3, username);
            int complaintsUpdated = pst.executeUpdate();
            pst.close();

            // If resolved, stamp resolved_at and compute SLA
            if ("Resolved".equalsIgnoreCase(status)) {
                try {
                    PreparedStatement ensure = con.prepareStatement("ALTER TABLE complaints ADD COLUMN IF NOT EXISTS resolved_at DATETIME NULL");
                    ensure.executeUpdate();
                    ensure.close();
                    ensure = con.prepareStatement("ALTER TABLE complaints ADD COLUMN IF NOT EXISTS sla_met TINYINT(1) NULL");
                    ensure.executeUpdate();
                    ensure.close();
                } catch (SQLException ignore) {}

                query = "UPDATE complaints SET resolved_at = CURRENT_TIMESTAMP, sla_met = CASE WHEN due_by IS NOT NULL AND CURRENT_TIMESTAMP <= due_by THEN 1 ELSE 0 END WHERE complain_id = ?";
                pst = con.prepareStatement(query);
                pst.setInt(1, Integer.parseInt(complaintId));
                pst.executeUpdate();
                pst.close();
            }

            // Mirror status to user table by complain_id
            query = "UPDATE user SET status = ? WHERE complain_id = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, status);
            pst.setInt(2, Integer.parseInt(complaintId));
            int userUpdated = pst.executeUpdate();
            pst.close();

            // Fallback: update by username if no row matched complain_id
            if (userUpdated == 0 && complaintUser != null) {
                query = "UPDATE user SET status = ? WHERE username = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, status);
                pst.setString(2, complaintUser);
                userUpdated = pst.executeUpdate();
                pst.close();
            }

            if (complaintsUpdated > 0) {
                con.commit();
                System.out.println("Status updated in both tables.");

                if ("Resolved".equalsIgnoreCase(status)) {
                    query = "SELECT sla_met, TIMESTAMPDIFF(HOUR, created_at, resolved_at) AS hrs, TIMESTAMPDIFF(HOUR, created_at, due_by) AS sla FROM complaints WHERE complain_id = ?";
                    pst = con.prepareStatement(query);
                    pst.setInt(1, Integer.parseInt(complaintId));
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        boolean met = rs.getInt("sla_met") == 1;
                        Integer taken = rs.getObject("hrs") != null ? rs.getInt("hrs") : null;
                        Integer allowed = rs.getObject("sla") != null ? rs.getInt("sla") : null;
                        if (met) {
                            System.out.println(" SLA met" + (taken != null && allowed != null ? (" (" + taken + "h taken / " + allowed + "h allowed)") : ""));
                        } else {
                            System.out.println(" SLA breached" + (taken != null && allowed != null ? (" (" + taken + "h taken / " + allowed + "h allowed)") : ""));
                        }
                    }
                    rs.close();
                    pst.close();
                }

                // Update in BST
                complaintTree.updateStatus(Integer.parseInt(complaintId), status);

                // Update workload tracker
                updateWorkloadTracker();
            } else {
                con.rollback();
                System.out.println("Complaint ID not found or not assigned to you. Rolled back.");
            }
        } catch (SQLException | NumberFormatException e) {
            try { con.rollback(); } catch (SQLException ignore) {}
            System.out.println("Error updating complaint status: " + e.getMessage());
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    // Advanced workload analysis with DBMS features
    public void viewAdvancedLoad() {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }

        try {
            System.out.println("=== Advanced Workload Analysis ===");

            // Use stored procedure for officer performance
            cst = con.prepareCall("{CALL GetOfficerPerformance(?)}");
            cst.setString(1, username);
            rs = cst.executeQuery();

            if (rs.next()) {
                System.out.println("Your Statistics:");
                System.out.println("Active Complaints: " + rs.getInt("active_complaints"));
                System.out.println("Resolved Complaints: " + rs.getInt("resolved_complaints"));
                System.out.println("Workload Rank: " + rs.getInt("workload_rank"));
                System.out.println("Load Status: " + rs.getString("load_status"));
            }

            // Query 2: Category-wise comparison with HAVING clause
            System.out.println("\n=== Category-wise Load Comparison ===");
            query = "SELECT o.category, COUNT(DISTINCT o.username) as total_officers, " +
                    "       AVG(active_complaints) as avg_load, " +
                    "       MAX(active_complaints) as max_load, " +
                    "       MIN(active_complaints) as min_load " +
                    "FROM officer o " +
                    "LEFT JOIN ( " +
                    "    SELECT u.assigned_officer, " +
                    "           COUNT(CASE WHEN u.status != 'Resolved' THEN 1 END) as active_complaints " +
                    "    FROM user u " +
                    "    GROUP BY u.assigned_officer " +
                    ") workload ON o.username = workload.assigned_officer " +
                    "GROUP BY o.category " +
                    "HAVING total_officers > 0 " +
                    "ORDER BY avg_load DESC";

            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            System.out.println("Category\t\tOfficers\tAvg Load\tMax Load\tMin Load");
            System.out.println("----------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-15s %-8d %-8.1f %-8d %-8d\n",
                        rs.getString("category"),
                        rs.getInt("total_officers"),
                        rs.getDouble("avg_load"),
                        rs.getInt("max_load"),
                        rs.getInt("min_load"));
            }

            // Display from hash table
            System.out.println("\n=== From Workload Hash Table ===");
            workloadTracker.displayWorkloadStats();

        } catch (SQLException e) {
            System.out.println("");
        }
    }

    public void viewLoad() {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }
        try {
            // Query to get only the officer's assigned complaints
            query = "SELECT COUNT(*) AS total, " +
                   "       SUM(CASE WHEN status = 'In Progress' THEN 1 ELSE 0 END) as in_progress, " +
                   "       SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending " +
                   "FROM user " +
                   "WHERE assigned_officer = ? AND status != 'Resolved'";
            
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int inProgress = rs.getInt("in_progress");
                int pending = rs.getInt("pending");
                
                System.out.println("\n=== Your Current Workload ===");
                System.out.println("Total active complaints: " + total);
                System.out.println("In Progress: " + inProgress);
                System.out.println("Pending: " + pending);
                
                // Workload status indicators
                if (total == 0) {
                    System.out.println(" Workload Status:  Light (No active complaints)");
                } else if (total <= 2) {
                    System.out.println(" Workload Status:  Light (" + total + " active complaints)");
                } else if (total <= 4) {
                    System.out.println(" Workload Status:  Manageable (" + total + " active complaints)");
                } else if (total <= 6) {
                    System.out.println(" Workload Status:  High (" + total + " active complaints - Consider resolving some)");
                } else {
                    System.out.println(" Workload Status:  Very High (" + total + " active complaints - Prioritize resolution)");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating workload tracker: " + e.getMessage());
        }
    }

    // New Feature: Complaint Search using BST
    public void searchComplaintInTree() {
        System.out.print("Enter Complaint ID to search: ");
        int searchId = sc.nextInt();

        ComplaintBinaryTree.TreeNode found = complaintTree.search(searchId);

        if (found != null) {
            System.out.println("=== Complaint Found in BST ===");
            System.out.println("ID: " + found.complaintId);
            System.out.println("Description: " + found.description);
            System.out.println("Status: " + found.status);
        } else {
            System.out.println("Complaint not found in tree structure.");
        }
    }

    // Enhanced complaint history with advanced DBMS queries
    public void viewAdvancedComplaintHistory() {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }

        try {
            // Complex query with multiple JOINs and subqueries
            query = "SELECT u.complain_id, u.username, u.complain, u.status, u.email, " +
                    "       DATEDIFF(CURDATE(), '2025-01-01') as days_since_year_start, " +
                    "       (SELECT AVG(CASE WHEN status = 'Resolved' THEN 1 ELSE 0 END) * 100 " +
                    "        FROM user " +
                    "        WHERE assigned_officer = ? AND complain IS NOT NULL) as your_resolution_rate, " +
                    "       (SELECT COUNT(*) FROM user WHERE area = u.area AND category = u.category) as area_category_total " +
                    "FROM user u " +
                    "WHERE u.assigned_officer = ? AND u.complain IS NOT NULL " +
                    "ORDER BY " +
                    "CASE " +
                    "    WHEN u.status = 'Pending' THEN 1 " +
                    "    WHEN u.status = 'In Progress' THEN 2 " +
                    "    WHEN u.status = 'Resolved' THEN 3 " +
                    "END, u.complain_id DESC";

            pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, username);
            rs = pst.executeQuery();

            boolean found = false;
            double resolutionRate = 0;

            while (rs.next()) {
                if (!found) {
                    found = true;
                    resolutionRate = rs.getDouble("your_resolution_rate");
                    System.out.println("=== Your Performance Metrics ===");
                    System.out.printf("Your Resolution Rate: %.2f%%\n", resolutionRate);
                    System.out.println("\n=== Complaint History (Priority Sorted) ===");
                }

                System.out.println("Complaint ID: " + rs.getInt("complain_id"));
                System.out.println("User: " + rs.getString("username") + " (" + rs.getString("email") + ")");
                System.out.println("Description: " + rs.getString("complain"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Area/Category Load: " + rs.getInt("area_category_total") + " total complaints");
                System.out.println("--------------------------");
            }

            if (!found) {
                System.out.println("No complaint history found for your assignments.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing advanced complaint history: " + e.getMessage());
        }
    }

    public void viewComplaintHistory() {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }
        try {
            // Query to get complaints assigned to this officer, ordered by status and date
            query = "SELECT c.complain_id, c.user_username as username, c.description as complain, " +
                   "       c.status, u.email, c.created_at as last_updated, " +
                   "       DATEDIFF(CURDATE(), c.created_at) as days_open " +
                   "FROM complaints c " +
                   "JOIN user u ON c.user_username = u.username " +
                   "WHERE c.officer_username = ? " +
                   "ORDER BY " +
                   "  CASE WHEN c.status = 'In Progress' THEN 1 " +
                   "       WHEN c.status = 'Pending' THEN 2 " +
                   "       ELSE 3 END, " +
                   "  c.created_at DESC";
            
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();
            
            System.out.println("\n=== Your Complaint History ===");
            System.out.println(String.format("%-12s %-15s %-30s %-15s %-10s %s", 
                "Complaint ID", "User", "Description", "Status", "Days Open", "Last Updated"));
            System.out.println("-".repeat(100));

            boolean found = false;
            int count = 0;
            while (rs.next()) {
                if (!found) {
                    found = true;
                }
                count++;
                
                // Format the complaint description to fit in the table
                String description = rs.getString("complain");
                if (description.length() > 25) {
                    description = description.substring(0, 22) + "...";
                }
                
                // Format the last updated date
                String lastUpdated = "N/A";
                try {
                    java.sql.Timestamp ts = rs.getTimestamp("last_updated");
                    if (ts != null) {
                        lastUpdated = new java.text.SimpleDateFormat("MMM dd, yyyy").format(ts);
                    }
                } catch (Exception e) {
                    // If there's an error, just use N/A
                }
                
                // Print the formatted row
                System.out.println(String.format("%-12d %-15s %-30s %-15s %-10d %s",
                    rs.getInt("complain_id"),
                    rs.getString("username"),
                    description,
                    rs.getString("status"),
                    rs.getInt("days_open"),
                    lastUpdated
                ));
            }
            
            if (found) {
                System.out.println("\nTotal complaints found: " + count);
            }
            if (!found) {
                System.out.println("No complaint history found for your assignments.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing complaint history: " + e.getMessage());
        }
    }

    public void logout() {
        loggedIn = false;
        username = null;
        area = null;
        category = null;
        System.out.println("Officer logged out.");
    }

    // Add missing methods for Main.java
    public void displayComplaintTree() {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }
        System.out.println("=== Complaint Binary Search Tree ===");
        complaintTree.inorderTraversal();
    }

    public void displayWorkloadStats() {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }
        System.out.println("=== Workload Statistics ===");
        workloadTracker.displayStats();
    }


    private String getValidUsername(String prompt) {
        while (true) {
            System.out.print(prompt);
            String username = sc.nextLine().trim();
            
            if (username.isEmpty()) {
                System.out.println(" Username cannot be empty.");
                continue;
            }
            
            if (username.matches("^[0-9]+$")) {
                System.out.println(" Username must contain only letters  and underscores.");
                continue;
            }
            
            if (username.length() < 3 || username.length() > 20) {
                System.out.println(" Username mus t be between 3 and 20 characters long.");
                continue;
            }
            
            return username;
        }
    }
    
    private String getValidPassword(String prompt) {
        while (true) {
            System.out.print(prompt);
            String password = sc.nextLine();
            
            if (password.isEmpty()) {
                System.out.println(" Password cannot be empty.");
                continue;
            }
            
            if (password.length() <= 3 || password.length() > 50) {
                System.out.println(" Password must be between 4 and 50 characters long.");
                continue;
            }
            
            if (password.contains(" ")) {
                System.out.println(" Password cannot contain spaces.");
                continue;
            }
            
            return password;
        }
    }

    public void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
