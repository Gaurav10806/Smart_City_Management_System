package Java;

import java.sql.*;
import java.util.*;
import DBMS.DBConnection;

public class User {
    private String username;
    private String password;
    private String email;
    private String address;
    private String area;
    private String category;
    private String complaint;
    private boolean loggedIn = false;

    Scanner sc = new Scanner(System.in);
    Connection con;
    String query;
    ResultSet rs;
    PreparedStatement pst;
    CallableStatement cst;

    public User() throws Exception {
        con = DBConnection.getConnection();
    }


    public boolean isLoggedIn() { return loggedIn; }
    public String getCategory() { return category; }
    public String getComplaint() { return complaint; }
    public String getUsername() { return username; }
    
    // Display user profile information
    public void viewProfile() {
        System.out.println("\n=== MY PROFILE ===");
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Address: " + address);
        System.out.println("Area: " + area);
        
        // Get and display complaint statistics
        try {
            // Get total complaints
            query = "SELECT COUNT(*) as total FROM complaints WHERE user_username = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                System.out.println("\nComplaint Statistics:");
                System.out.println("• Total Complaints: " + rs.getInt("total"));
            }
            
            // Get most recent complaint
            query = "SELECT complain_id, category, status, created_at FROM complaints WHERE user_username = ? ORDER BY created_at DESC LIMIT 1";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                System.out.println("\nMost Recent Complaint:");
                System.out.println("• ID: " + rs.getInt("complain_id"));
                System.out.println("• Category: " + rs.getString("category"));
                System.out.println("• Status: " + rs.getString("status"));
                System.out.println("• Created: " + rs.getTimestamp("created_at"));
            }
            
        } catch (SQLException e) {
            System.out.println("\nError fetching profile statistics: " + e.getMessage());
        }
    }
    static int i=1050;

    // Validate and normalize area to allowed set
    private String validateAndNormalizeArea(String inputArea) {
        if (inputArea == null) {
            throw new IllegalArgumentException("Area cannot be null. Allowed: Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura");
        }
        String trimmed = inputArea.trim();
        String[] allowed = {"Paldi", "Bapunagar", "Ellisbridge", "Maninagar", "Navrangpura"};
        for (String a : allowed) {
            if (a.equalsIgnoreCase(trimmed)) {
                return a; // canonical casing
            }
        }
        throw new IllegalArgumentException("Invalid area: " + inputArea + ". Allowed: Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura");
    }
    public void register() throws Exception {
        System.out.println("--- User Registration ---");
        displayValidationRules();
        System.out.println("\n--- Enter User Details ---");
        
        username = getValidUsername("Enter username: ");
        password = getValidPassword("Enter password: ");
        
        // Email validation loop
        while (true) {
            try {
                System.out.print("Enter email: ");
                email = sc.nextLine().trim();
                validateEmail(email);
                break; // If validation passes, exit the loop
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        
        // Address input loop
        while (true) {
            System.out.print("Enter address: ");
            address = sc.nextLine();
            if (validateAddress(address)) {
                break;
            }
            System.out.println("Please try again.");
        }
        // Area validation loop
        while (true) {
            try {
                System.out.print("Enter area (Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura): ");
                area = sc.nextLine();
                area = validateAndNormalizeArea(area);
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
            // First check if username already exists
            String checkUserSql = "SELECT COUNT(*) FROM user WHERE username = ?";
            pst = con.prepareStatement(checkUserSql);
            pst.setString(1, username);
            rs = pst.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Username already exists. Please choose a different username.");
                return;
            }
            rs.close();
            pst.close();
            
            // Get next available complaint ID
            int nextComplaintId = getNextComplaintId();
            
            // Use direct SQL for user insertion
            String sql = "INSERT INTO user (username, password, email, address, area, complain_id) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, email);
            pst.setString(4, address);
            pst.setString(5, area);
            pst.setInt(6, nextComplaintId);

            userResult = pst.executeUpdate();
            
            if (userResult > 0) {
                con.commit();
                System.out.println("User registered successfully!");
            } else {
                con.rollback();
                System.out.println("Failed to register user. Please try again.");
            }
        } catch (SQLException e) {
            try {
                con.rollback();
                if (e.getErrorCode() == 1062) { // Duplicate entry
                    System.out.println("Error: The system detected a conflict with an existing record.");
                    System.out.println("Please try registering again or contact support if the issue persists.");
                } else {
                    System.out.println("Database error: " + e.getMessage());
                }
            } catch (SQLException ex) {
                System.out.println("Error during transaction rollback: " + ex.getMessage());
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
    }

    public void login() {
        System.out.println("--- User Login ---");
        System.out.print("Enter Username: ");
        String inputUsername = sc.nextLine().trim();
        String inputPassword = getValidPassword("Enter Password: ");

        try {
            // 1) Check username exists
            query = "SELECT password, email, area FROM user WHERE username = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, inputUsername);
            rs = pst.executeQuery();

            if (!rs.next()) {
                System.out.println("Invalid username.");
                rs.close();
                pst.close();
                return;
            }

            String dbPassword = rs.getString("password");
            String dbEmail = rs.getString("email");
            String dbArea = rs.getString("area");
            rs.close();
            pst.close();

            // 2) Verify password
            if (!Objects.equals(dbPassword, inputPassword)) {
                System.out.println("Incorrect password.");
            return;
        }


            // Successful login
                this.username = inputUsername;
            this.password = dbPassword;
            this.email = dbEmail;
            this.area = dbArea;
                loggedIn = true;
                System.out.println("Login successful! Welcome " + username);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String trimmedEmail = email.trim();
        return trimmedEmail.contains("@") && (trimmedEmail.contains(".com") || trimmedEmail.contains(".in"));
    }
    
    // Enhanced email validation method (same as Admin.java)
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        String trimmedEmail = email.trim();
        if (!trimmedEmail.contains("@") || (!trimmedEmail.contains(".com") && !trimmedEmail.contains(".in"))) {
            throw new IllegalArgumentException("Invalid email format. Email must contain '@' and either '.com' or '.in'");
        }
    }
    
    // Address validation method with loop and SOP
    public boolean validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            System.out.println("Error: Address cannot be empty");
            return false;
        }
        String trimmedAddress = address.trim();
        if (trimmedAddress.length() < 5) {
            System.out.println("Error: Address must be at least 5 characters long");
            return false;
        }
        if (trimmedAddress.length() > 200) {
            System.out.println("Error: Address must not exceed 200 characters");
            return false;
        }
        return true;
    }
    
    // Display validation rules for users (same as Admin.java)
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
     
        
        System.out.println("\nADDRESS RULES:");
        System.out.println("• Must be at least 5 characters long");
        System.out.println("• Must not exceed 200 characters");
        System.out.println("• Cannot be null or empty");
        
        System.out.println("\nAREA RULES:");
        System.out.println("• Must be one of: Paldi, Bapunagar, Ellisbridge, Maninagar, Navrangpura");
        
        System.out.println("\nEMAIL RULES:");
        System.out.println("• Must contain '@' and either '.com' or '.in'");
        System.out.println("• Cannot be null or empty");
    }

    // Generate unique complaint ID (DS + DBMS)
    private int generateUniqueComplaintId() throws SQLException {
        int complaintId;
        boolean exists;

        do {
            complaintId = 1000 + (int)(Math.random() * 9000);

            // Check if ID exists using subquery (DBMS)
            query = "SELECT EXISTS(SELECT 1 FROM complaints WHERE complain_id = ?) as id_exists";
            pst = con.prepareStatement(query);
            pst.setInt(1, complaintId);
            rs = pst.executeQuery();
            rs.next();
            exists = rs.getBoolean("id_exists");

        } while (exists);

        return complaintId;
    }
    
    // Get next available complaint ID safely using database sequence
    private int getNextComplaintId() {
        Connection tempCon = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            // Get a new connection to avoid transaction issues
            try {
                tempCon = DBConnection.getConnection();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get database connection", e);
            }
            
            // Use database's auto-increment or sequence feature
            String sql = "INSERT INTO complaint_id_sequence (dummy) VALUES (1)";
            pst = tempCon.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.executeUpdate();
            
            rs = pst.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            // Fallback if generated keys not supported
            rs = tempCon.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            throw new SQLException("Could not generate complaint ID");
        } catch (SQLException e) {
            // If sequence table doesn't exist, create it
            if (e.getMessage().contains("Table 'smart_city.complaint_id_sequence' doesn't exist")) {
                try {
                    tempCon.createStatement().execute(
                        "CREATE TABLE complaint_id_sequence (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "dummy INT DEFAULT 1, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")"
                    );
                    // Try again after creating the table
                    return getNextComplaintId();
                } catch (SQLException ex) {
                    System.err.println("Error creating sequence table: " + ex.getMessage());
                }
            }
            
            // Fallback to timestamp-based ID if all else fails
            System.err.println("Warning: Could not generate sequential ID. Using timestamp-based fallback: " + e.getMessage());
            return (int) (System.currentTimeMillis() % 1000000);
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (tempCon != null && tempCon != this.con) {
                    tempCon.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }



    public void updateArea(String newArea) throws Exception {
        newArea = validateAndNormalizeArea(newArea);
        this.area = newArea;
        query = "UPDATE user SET area = ? WHERE username = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, newArea);
        pst.setString(2, username);
        System.out.println(pst.executeUpdate() > 0 ? "Area updated successfully." : "Failed to update area.");
    }

    public void LodgeComplaint() throws Exception {
        System.out.println("--- Lodge Complaint ---");
        System.out.println("1. Drainage\n2. Water Supply\n3. Electricity\n4. Road Maintenance\n5. Others");
        
        // Read category choice with validation
        int choice;
        while (true) {
            try {
                System.out.print("Select category (1-5): ");
                choice = Integer.parseInt(sc.nextLine().trim());
                if (choice >= 1 && choice <= 5) break;
                System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        switch (choice) {
            case 1: category = "drainage"; break;
            case 2: category = "water"; break;
            case 3: category = "electricity"; break;
            case 4: category = "road maintanence"; break;
            case 5: category = "other"; break;
        }

        // Validate complaint description is not null or empty
        while (true) {
            System.out.print("Enter complaint description: ");
            complaint = sc.nextLine().trim();
            if (complaint == null || complaint.isEmpty()) {
                System.out.println("Error: Complaint description cannot be empty. Please enter a valid description.");
            } else {
                break;
            }
        }

        // Generate unique complaint ID
        int complaintId = generateUniqueComplaintId();

        // Officer will be assigned by admin
        String assignedOfficer = "Unassigned";

        // Insert complaint and mirror into user table within a transaction
        try {
            con.setAutoCommit(false);

        // Insert complaint using stored procedure
        cst = con.prepareCall("{CALL InsertComplaint(?, ?, ?, ?, ?, ?)}");
        cst.setInt(1, complaintId);
        cst.setString(2, username);
        cst.setString(3, area);
        cst.setString(4, category);
        cst.setString(5, complaint);
        cst.setString(6, "Pending");
        cst.execute();
        cst.close();

            // Ensure SLA columns exist and set due_by based on category
            ensureSLAColumnsExist();
            int slaHours = getSlaHoursForCategory(category);
            query = "UPDATE complaints SET due_by = DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? HOUR) WHERE complain_id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, slaHours);
            pst.setInt(2, complaintId);
            pst.executeUpdate();
            pst.close();
            // Mirror latest complaint details into user table
            query = "UPDATE user SET complain_id = ?, category = ?, complain = ?, status = ?, assigned_officer = ? WHERE username = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, complaintId);
            pst.setString(2, category);
            pst.setString(3, complaint);
            pst.setString(4, "Pending");
            pst.setString(5, assignedOfficer);
            pst.setString(6, username);
            int userUpdated = pst.executeUpdate();
            pst.close();

        // Verify complaint exists
        query = "SELECT 1 FROM complaints WHERE complain_id = ?";
        pst = con.prepareStatement(query);
        pst.setInt(1, complaintId);
        rs = pst.executeQuery();
        boolean inserted = rs.next();
        rs.close();
        pst.close();

            if (inserted && userUpdated > 0) {
                con.commit();
                System.out.println("Complaint lodged successfully (synced to both tables)!");
                System.out.println("Complaint ID: " + complaintId);
                System.out.println("Assigned Officer: " + assignedOfficer);
                System.out.println("SLA: " + slaHours + " hours to resolve");
            } else {
                con.rollback();
                System.out.println("Failed to lodge complaint in both tables.");
            }
        } catch (SQLException e) {
            con.rollback();
            System.out.println("Error lodging complaint: " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    // New Feature: Complaint Analytics using DBMS advanced queries
    public void viewComplaintAnalytics() throws SQLException {
        System.out.println("=== Complaint Analytics ===");

        // Query 1: GROUP BY with HAVING clause
        query = "SELECT area, category, COUNT(*) as complaint_count, " +
                "AVG(CASE WHEN status = 'Resolved' THEN 1 ELSE 0 END) * 100 as resolution_rate " +
                "FROM complaints " +
                "GROUP BY area, category " +
                "HAVING complaint_count > 1 " +
                "ORDER BY complaint_count DESC";

        pst = con.prepareStatement(query);
        rs = pst.executeQuery();

        System.out.println("Area-wise Category Analysis:");
        System.out.println("Area\t\tCategory\t\tCount\tResolution%");
        System.out.println("--------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-15s %-15s %-8d %.2f%%\n",
                    rs.getString("area"),
                    rs.getString("category"),
                    rs.getInt("complaint_count"),
                    rs.getDouble("resolution_rate"));
        }

        // Query 2: Subquery for user ranking
        System.out.println("\n=== Your Complaint Performance ===");
        query = "SELECT user_username, complaint_count, user_rank FROM (" +
                "SELECT user_username, COUNT(*) as complaint_count, " +
                "RANK() OVER (ORDER BY COUNT(*) DESC) as user_rank " +
                "FROM complaints " +
                "GROUP BY user_username" +
                ") ranked_users WHERE user_username = ?";

        pst = con.prepareStatement(query);
        pst.setString(1, username);
        rs = pst.executeQuery();

        if (rs.next()) {
            System.out.println("Your Complaints: " + rs.getInt("complaint_count"));
            System.out.println("Your Rank: " + rs.getInt("user_rank"));
        }
    }



    // Enhanced complaint history with stack
    public void viewAdvancedComplaintHistory() throws SQLException {
        System.out.println("=== Advanced Complaint History ===");

        // Database history with JOINs
        query = "SELECT c.complain_id, c.category, c.description, c.status, " +
                "c.officer_username, o.category as officer_category " +
                "FROM complaints c " +
                "LEFT JOIN officer o ON c.officer_username = o.username " +
                "WHERE c.user_username = ? " +
                "ORDER BY c.complain_id DESC";

        pst = con.prepareStatement(query);
        pst.setString(1, username);
        rs = pst.executeQuery();

        System.out.println("Database History:");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("complain_id"));
            System.out.println("Category: " + rs.getString("category"));
            System.out.println("Description: " + rs.getString("description"));
            System.out.println("Status: " + rs.getString("status"));
            System.out.println("Officer: " + rs.getString("officer_username"));
            System.out.println("--------------------------");
        }

        // Recent activity logging removed
    }

    public void addEvidence(String url) {
        System.out.println("=== Add Evidence ===");
        
        // Validate URL is not empty and starts with https://
        while (true) {
            if (url == null || url.trim().isEmpty()) {
                System.out.println("Error: URL cannot be empty.");
            } else if (!url.toLowerCase().startsWith("https://")) {
                System.out.println("Error: URL must start with 'https://'");
            } else {
                break; // Valid URL, exit the loop
            }
            
            System.out.print("Please enter a valid URL (must start with https://): ");
            url = sc.nextLine();
        }

        // First, show user's complaints to select which one to add evidence to
        try {
            query = "SELECT complain_id, category, description, status FROM complaints WHERE user_username = ? AND status != 'Resolved' ORDER BY complain_id DESC";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();

            if (!rs.next()) {
                System.out.println("No active complaints found to add evidence to.");
                return;
            }

            System.out.println("Select a complaint to add evidence to:");
            System.out.println("0. Cancel");

            // Reset result set to beginning
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();

            int count = 1;
            while (rs.next()) {
                System.out.println(count + ". ID: " + rs.getInt("complain_id") +
                        " | " + rs.getString("category") +
                        " | " + rs.getString("description").substring(0, Math.min(50, rs.getString("description").length())) + "...");
                count++;
            }

            System.out.print("Enter choice (0-" + (count-1) + "): ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            if (choice == 0) {
                System.out.println("Evidence upload cancelled.");
                return;
            }

            if (choice < 1 || choice >= count) {
                System.out.println("Invalid choice.");
                return;
            }

            // Get the selected complaint ID
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();

            int targetComplaintId = 0;
            for (int i = 1; i <= choice; i++) {
                rs.next();
                if (i == choice) {
                    targetComplaintId = rs.getInt("complain_id");
                    break;
                }
            }

            if (targetComplaintId == 0) {
                System.out.println("Error: Could not find complaint.");
                return;
            }

            // Get evidence details
            System.out.print("Enter evidence type (url): ");
            String evidenceType = sc.nextLine();
            
            // Validate evidence type
            if (!evidenceType.equalsIgnoreCase("url")) {
                System.out.println("Only URL evidence type is supported.");
                evidenceType = "url";
            }

            // Validate evidence description is not null or empty
            String description;
            while (true) {
                System.out.print("Enter evidence description: ");
                description = sc.nextLine().trim();
                if (description == null || description.isEmpty()) {
                    System.out.println("Error: Evidence description cannot be empty. Please enter a valid description.");
                } else {
                    break;
                }
            }

            // Store evidence in database
            try {
                // Ensure evidence table exists with correct structure
                ensureEvidenceTableExists();

                // Insert evidence directly
                query = "INSERT INTO evidence (complain_id, user_username, evidence_type, evidence_url, description) VALUES (?, ?, ?, ?, ?)";
                pst = con.prepareStatement(query);
                pst.setInt(1, targetComplaintId);
                pst.setString(2, username);
                pst.setString(3, evidenceType);
                pst.setString(4, url);
                pst.setString(5, description);

                if (pst.executeUpdate() > 0) {
                    System.out.println(" Evidence added successfully!");
                    System.out.println("Complaint ID: " + targetComplaintId);
                    System.out.println("Evidence Type: " + evidenceType);
                    System.out.println("URL: " + url);

                    // Update complaint status to indicate evidence provided and mirror in user table
                    try {
                        con.setAutoCommit(false);

                    query = "UPDATE complaints SET status = 'Evidence Provided' WHERE complain_id = ?";
                    pst = con.prepareStatement(query);
                    pst.setInt(1, targetComplaintId);
                        int cUpdated = pst.executeUpdate();
                        pst.close();

                        query = "UPDATE user SET status = 'Evidence Provided' WHERE complain_id = ?";
                        pst = con.prepareStatement(query);
                        pst.setInt(1, targetComplaintId);
                        int uUpdated = pst.executeUpdate();
                        pst.close();

                        if (cUpdated > 0 && uUpdated > 0) {
                            con.commit();
                        } else {
                            con.rollback();
                            System.out.println("Warning: evidence status not synced to both tables.");
                        }
                    } catch (SQLException ex) {
                        con.rollback();
                        System.out.println("Error syncing evidence status: " + ex.getMessage());
                    } finally {
                        con.setAutoCommit(true);
                    }

                } else {
                    System.out.println(" Failed to add evidence.");
                }

            } catch (SQLException e) {
                System.out.println(" Database error: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    // New method: View evidence for a specific complaint
    public void viewEvidence(int complaintId) {
        try {
            // Ensure evidence table exists with correct structure
            ensureEvidenceTableExists();

            // Now query the evidence table
            query = "SELECT e.evidence_type, e.evidence_url, e.description, e.uploaded_at " +
                    "FROM evidence e " +
                    "WHERE e.complain_id = ? AND e.user_username = ? " +
                    "ORDER BY e.uploaded_at DESC";

            pst = con.prepareStatement(query);
            pst.setInt(1, complaintId);
            pst.setString(2, username);
            rs = pst.executeQuery();

            if (!rs.next()) {
                System.out.println("No evidence found for complaint ID: " + complaintId);
                return;
            }

            System.out.println("=== Evidence for Complaint ID: " + complaintId + " ===");

            // Reset result set
            pst = con.prepareStatement(query);
            pst.setInt(1, complaintId);
            pst.setString(2, username);
            rs = pst.executeQuery();

            int count = 1;
            while (rs.next()) {
                System.out.println("\nEvidence #" + count + ":");
                System.out.println("Type: " + rs.getString("evidence_type"));
                System.out.println("URL: " + rs.getString("evidence_url"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Uploaded: " + rs.getTimestamp("uploaded_at"));
                System.out.println("---");
                count++;
            }

        } catch (SQLException e) {
            System.out.println(" Error viewing evidence: " + e.getMessage());
        }
    }

    // New method: List all evidence for user
    public void listAllEvidence() {
        try {
            // Ensure evidence table exists with correct structure
            ensureEvidenceTableExists();

            // Now query the evidence table
            query = "SELECT e.complain_id, e.evidence_type, e.evidence_url, e.description, e.uploaded_at, " +
                    "c.category, c.status " +
                    "FROM evidence e " +
                    "JOIN complaints c ON e.complain_id = c.complain_id " +
                    "WHERE e.user_username = ? " +
                    "ORDER BY e.uploaded_at DESC";

            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();

            if (!rs.next()) {
                System.out.println("No evidence found for your complaints.");
                return;
            }

            System.out.println("=== All Your Evidence ===");

            // Reset result set
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();

            while (rs.next()) {
                System.out.println("\nComplaint ID: " + rs.getInt("complain_id"));
                System.out.println("Category: " + rs.getString("category"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Evidence Type: " + rs.getString("evidence_type"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Uploaded: " + rs.getTimestamp("uploaded_at"));
                System.out.println("---");
            }

        } catch (SQLException e) {
            System.out.println(" Error listing evidence: " + e.getMessage());
        }
    }

    public void trackComplaintStatus(int complaintId) {
        try {
            // Using subquery for enhanced tracking
            query = "SELECT c.status, c.officer_username, " +
                    "(SELECT COUNT(*) FROM complaints WHERE officer_username = c.officer_username AND status != 'Resolved') as officer_workload " +
                    "FROM complaints c WHERE c.complain_id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, complaintId);
            rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Assigned Officer: " + rs.getString("officer_username"));
                System.out.println("Officer Current Workload: " + rs.getInt("officer_workload"));
            } else {
                System.out.println("Complaint not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error tracking complaint status: " + e.getMessage());
        }
    }

    public void forgotPassword(String newPassword) {
        try {
            query = "UPDATE user SET password=? WHERE username=?";
            pst = con.prepareStatement(query);
            pst.setString(1, newPassword);
            pst.setString(2, username);
            System.out.println(pst.executeUpdate() > 0 ? "Password updated." : "Password update failed.");
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
        }
    }

    public void viewComplaintHistory() {
        try {
            query = "SELECT * FROM complaints WHERE user_username=? ORDER BY complain_id DESC";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();
            
            boolean hasComplaints = false;
            while (rs.next()) {
                hasComplaints = true;
                System.out.println("ID: " + rs.getInt("complain_id"));
                System.out.println("Category: " + rs.getString("category"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Officer: " + rs.getString("officer_username"));
                System.out.println("--------------------------");
            }
            
            if (!hasComplaints) {
                System.out.println("No complaints found. You haven't lodged any complaints yet.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing complaint history: " + e.getMessage());
        }
    }

    public void logout() {
        loggedIn = false;
        username = null;
        password = null;
        System.out.println("User logged out.");
    }

    // Helper method to ensure evidence table exists with correct structure
    private void ensureEvidenceTableExists() throws SQLException {
        // First check if table exists
        boolean tableExists = false;
        try {
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "evidence", null);
            tableExists = tables.next();
            tables.close();
        } catch (SQLException e) {
            System.out.println("Error checking for evidence table: " + e.getMessage());
            throw e;
        }

        if (!tableExists) {
            System.out.println("Creating evidence table...");
            // Create table with all necessary columns
            String createTableQuery = "CREATE TABLE evidence (" +
                    "evidence_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "complain_id INT NOT NULL, " +
                    "user_username VARCHAR(50) NOT NULL, " +
                    "evidence_type VARCHAR(50), " +
                    "evidence_url TEXT, " +
                    "description TEXT, " +
                    "uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (complain_id) REFERENCES complaints(complain_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_username) REFERENCES user(username) ON DELETE CASCADE" +
                    ")";

            try (Statement stmt = con.createStatement()) {
                stmt.execute(createTableQuery);
                System.out.println("Evidence table created successfully");
            } catch (SQLException e) {
                System.err.println("Error creating evidence table: " + e.getMessage());
                throw e;
            }
        } else {
            // Table exists, verify structure
            try {
                // Check if required columns exist
                DatabaseMetaData meta = con.getMetaData();
                ResultSet columns = meta.getColumns(null, null, "evidence", null);
                Set<String> columnNames = new HashSet<>();
                while (columns.next()) {
                    columnNames.add(columns.getString("COLUMN_NAME").toLowerCase());
                }
                columns.close();

                // Check for required columns
                Set<String> requiredColumns = new HashSet<>(Arrays.asList(
                        "evidence_id", "complain_id", "user_username", 
                        "evidence_type", "evidence_url", "description", "uploaded_at"
                ));

                boolean structureValid = columnNames.containsAll(requiredColumns);
                
                if (!structureValid) {
                    System.out.println("Evidence table structure is invalid. Recreating...");
                    try (Statement stmt = con.createStatement()) {
                        stmt.execute("DROP TABLE IF EXISTS evidence");
                        ensureEvidenceTableExists(); // Recursively recreate
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error verifying evidence table structure: " + e.getMessage());
                throw e;
            }
        }
    }

    // SLA helpers for limited-time resolution
    private void ensureSLAColumnsExist() throws SQLException {
        // Create required columns if they do not exist (MySQL 8.0+ supports IF NOT EXISTS)
        String alter1 = "ALTER TABLE complaints ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        String alter2 = "ALTER TABLE complaints ADD COLUMN IF NOT EXISTS due_by DATETIME NULL";
        String alter3 = "ALTER TABLE complaints ADD COLUMN IF NOT EXISTS resolved_at DATETIME NULL";
        String alter4 = "ALTER TABLE complaints ADD COLUMN IF NOT EXISTS sla_met TINYINT(1) NULL";

        try {
            pst = con.prepareStatement(alter1); pst.executeUpdate(); pst.close();
        } catch (SQLException ignored) {}
        try {
            pst = con.prepareStatement(alter2); pst.executeUpdate(); pst.close();
        } catch (SQLException ignored) {}
        try {
            pst = con.prepareStatement(alter3); pst.executeUpdate(); pst.close();
        } catch (SQLException ignored) {}
        try {
            pst = con.prepareStatement(alter4); pst.executeUpdate(); pst.close();
        } catch (SQLException ignored) {}
    }

    private int getSlaHoursForCategory(String cat) {
        if (cat == null) return 48;
        String c = cat.toLowerCase();
        switch (c) {
            case "electricity": return 12;
            case "water": return 24;
            case "drainage": return 24;
            case "road maintanence": return 72;
            default: return 48;
        }
    }


    // Add missing methods for Main.java
    public Connection getConnection() {
        return con;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String getValidUsername(String prompt) {
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
            } catch (SQLException e) {
                System.out.println(" Database error checking username: " + e.getMessage());
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
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}

