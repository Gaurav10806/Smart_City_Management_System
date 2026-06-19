package Main;

import Java.*;
import DBMS.*;

import java.util.Scanner;
import java.sql.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static Admin admin;
    private static Officer officer;
    private static User user;

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("WELCOME TO SMART CITY MANAGEMENT SYSTEM");
        System.out.println("=========================================");

        try {
            Connection con = DBConnection.getConnection();
            
            // Initialize objects
            admin = new Admin();
            officer = new Officer();
            user = new User();

            // Main application loop
            while (true) {
                displayMainMenu();
                int choice = getValidChoice(1, 4);

                switch (choice) {
                    case 1:
                        adminModule();
                        break;
                    case 2:
                        officerModule();
                        break;
                    case 3:
                        userModule();
                        break;
                    case 4:
                        exitApplication();
                        System.exit(0);
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Application initialization failed: " + e.getMessage());
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Admin Login");
        System.out.println("2. Officer Login");
        System.out.println("3. User Portal");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void adminModule() {
        System.out.println("\n=== ADMIN MODULE ===");

        if (!admin.isLoggedIn()) {
            admin.login();
            if (!admin.isLoggedIn()) {
                return; // Login failed
            }
        }

        while (admin.isLoggedIn()) {
            displayAdminMenu();
            int choice = getValidChoice(1, 14);

            try {
                switch (choice) {
                    case 1:
                        admin.ManageAllUsers();
                        break;
                    case 2:
                        admin.ManageAllOfficers();
                        break;
                    case 3:
                        admin.viewAllComplaints();
                        break;
                    case 4:
                        admin.viewAdvancedComplaintAnalytics();
                        break;
                    case 5:
                        admin.viewSystemPerformanceDashboard();
                        break;
                    case 6:
                        admin.reassignOverdueComplaints();
                        break;
                    case 7:
                        admin.exportAdvancedComplaintsToCSV();
                        break;
                    case 8:
                        admin.exportResolvedComplaintsToCSV();
                        break;
                    case 9:
                        admin.manageDataStructures();
                        break;
                    case 10:
                        admin.generateComprehensiveSystemReport();
                        break;
                    case 11:
                        admin.logout();
                        break;
                    case 12:
                        admin.reassignOverdueComplaints();
                        break;
                    case 13:
                        return; // Back to main menu
                }
            } catch (Exception e) {
                System.err.println("Error in admin operation: " + e.getMessage());
            }

            if (admin.isLoggedIn()) {
                pressEnterToContinue();
            }
        }
    }

    private static void displayAdminMenu() {
        System.out.println("\n=== ADMIN DASHBOARD ===");
        System.out.println("1. Manage Users (Add/Delete)");
        System.out.println("2. Manage Officers (Add/Delete)");
        System.out.println("3. View All Complaints");
        System.out.println("4. Advanced Complaint Analytics");
        System.out.println("5. System Performance Dashboard");
        System.out.println("6. View Officer Workload & Assign");
        System.out.println("7. Export Advanced Complaints Report");
        System.out.println("8. Export Resolved Complaints");
        System.out.println("9. Manage Data Structures");
        System.out.println("10. Generate Comprehensive Report");
        System.out.println("11. Logout");
        System.out.println("12. Reassign Overdue Complaints");
        System.out.println("13. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static void officerModule() {
        System.out.println("\n=== OFFICER MODULE ===");

        if (!officer.isLoggedIn()) {
            if (!officer.login()) {
                return; // Login failed
            }
        }

        while (officer.isLoggedIn()) {
            displayOfficerMenu();
            int choice = getValidChoice(1, 7);

            try {
                switch (choice) {
                    case 1:
                        officer.viewAssignedComplaints();
                        break;
                    case 2: {
                        String complaintId;
                        while (true) {
                            System.out.print("Enter Complaint ID to update (or 'back' to return): ");
                            complaintId = sc.nextLine().trim();
                            
                            if (complaintId.equalsIgnoreCase("back")) {
                                break;
                            }
                            
                            try {
                                // Validate it's a number
                                Integer.parseInt(complaintId);
                                break; // Exit loop if valid number
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Complaint ID must be a number. Please try again or type 'back' to return.");
                            }
                        }
                        
                        if (!complaintId.equalsIgnoreCase("back")) {
                            officer.updateComplaintStatus(complaintId);
                        }
                        break;
                    }
                    case 3:
                        officer.viewLoad();
                        break;
                    case 4:
                        officer.viewComplaintHistory();
                        break;
                    case 5:
                        officer.viewAdvancedComplaintHistory();
                        break;
                    case 6:
                        officer.logout();
                        System.out.println("Logged out successfully!");
                        return;
                    case 7:
                        return; // Back to main menu
                }
            } catch (Exception e) {
                System.err.println("Error in officer operation: " + e.getMessage());
            }

            if (officer.isLoggedIn()) {
                pressEnterToContinue();
            }
        }
    }

    private static void displayOfficerMenu() {
        System.out.println("\n=== OFFICER DASHBOARD ===");
        System.out.println("1. View Assigned Complaints");
        System.out.println("2. Update Complaint Status");
        System.out.println("3. View Current Workload");
        System.out.println("4. View Complaint History (Basic)");
        System.out.println("5. View Advanced Complaint History");
        System.out.println("6. Logout");
        System.out.println("7. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static void userModule() {
        System.out.println("\n=== USER MODULE ===");

        while (true) {
            displayUserMainMenu();
            int choice = getValidChoice(1, 4);

            try {
                switch (choice) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        user.login();
                        if (user.isLoggedIn()) {
                            userDashboard();
                        }
                        break;
                    case 3:
                        forgotPasswordModule();
                        break;
                    case 4:
                        return; // Back to main menu
                }
            } catch (Exception e) {
                System.err.println("Error in user operation: " + e.getMessage());
            }

            pressEnterToContinue();
        }
    }

    private static void displayUserMainMenu() { 
        System.out.println("\n=== USER PORTAL ===");
        System.out.println("1. Register New Account");
        System.out.println("2. Login");
        System.out.println("3. Forgot Password");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static void userDashboard() {
        while (user.isLoggedIn()) {
            displayUserDashboard();
            int choice = getValidChoice(1, 10);

            try {
                switch (choice) {
                    case 1:
                        user.viewProfile();
                        break;
                    case 2:
                        user.LodgeComplaint();
                        break;
                    case 3:
                        user.viewComplaintHistory();
                        break;
                    case 4:
                        user.viewAdvancedComplaintHistory();
                        break;
                    case 5:
                        System.out.print("Enter Complaint ID to track: ");
                        int trackId = sc.nextInt();
                        sc.nextLine(); // consume newline
                        user.trackComplaintStatus(trackId);
                        break;
                    case 6:
                        System.out.print("Enter new area: ");
                        String newArea = sc.nextLine();
                        user.updateArea(newArea);
                        break;
                    case 7:
                        System.out.print("Enter evidence URL: ");
                        String evidenceUrl = sc.nextLine();
                        user.addEvidence(evidenceUrl);
                        break;
                    case 8:
                        System.out.print("Enter Complaint ID to view evidence: ");
                        int evidenceId = sc.nextInt();
                        sc.nextLine(); // consume newline
                        user.viewEvidence(evidenceId);
                        break;
                    case 9:
                        user.listAllEvidence();
                        break;
                    case 10:
                        user.logout();
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error in user dashboard: " + e.getMessage());
            }

            if (user.isLoggedIn()) {
                pressEnterToContinue();
            }
        }
    }

    private static void displayUserDashboard() {
        System.out.println("\n=== USER DASHBOARD ===");
        System.out.println("1. View My Profile");
        System.out.println("2. Lodge a Complaint");
        System.out.println("3. View Complaint History (Basic)");
        System.out.println("4. View Advanced Complaint History");
        System.out.println("5. Track Complaint Status");
        System.out.println("6. Update Area");
        System.out.println("7. Add Evidence");
        System.out.println("8. View Evidence");
        System.out.println("9. List All Evidence");
        System.out.println("10. Logout");
        System.out.print("Enter your choice: ");
    }

    private static void forgotPasswordModule() {
        System.out.println("\n=== PASSWORD RECOVERY ===");
        System.out.print("Enter your username: ");
        String username = sc.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();

        try {
            User tempUser = new User();
            tempUser.setUsername(username);
            tempUser.forgotPassword(newPassword);
        } catch (Exception e) {
            System.err.println("Password reset failed: " + e.getMessage());
        }
    }

    private static int getValidChoice(int min, int max) {
        int choice;
        while (true) {
            try {
                choice = sc.nextInt();
                sc.nextLine(); // consume newline
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Invalid choice. Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (Exception e) {
                System.out.print("Invalid input. Please enter a valid number: ");
                sc.nextLine(); // clear invalid input
            }
        }
    }

    private static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private static void exitApplication() {
        System.out.println("\n=== SHUTTING DOWN APPLICATION ===");

        try {
            if (admin != null) {
                admin.closeConnection();
            }
            if (officer != null) {
                officer.closeConnection();
            }
            if (user != null) {
                user.closeConnection();
            }

            System.out.println("Thank you for using Smart City Complaint Management System!");
            System.out.println("Application closed successfully.");
        } catch (Exception e) {
            System.err.println("Error during application shutdown: " + e.getMessage());
        }
    }

    // Removed evidence table management options from User dashboard
}