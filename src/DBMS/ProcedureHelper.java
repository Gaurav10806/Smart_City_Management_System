package DBMS;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcedureHelper implements AutoCloseable {
    private Connection con;
    
    public ProcedureHelper() throws Exception {
        con = DBConnection.getConnection();
    }
    
    @Override
    public void close() throws Exception {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
    
    /**
     * Execute InsertComplaint stored procedure
     */
    public boolean insertComplaint(int complaintId, String userUsername, String area, String category, String description, String status) throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL InsertComplaint(?, ?, ?, ?, ?, ?)}");
        try {
            cst.setInt(1, complaintId);
            cst.setString(2, userUsername);
            cst.setString(3, area);
            cst.setString(4, category);
            cst.setString(5, description);
            cst.setString(6, status);
            
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    
    /**
     * Execute UpdateComplaintStatus stored procedure
     */
    public boolean updateComplaintStatus(int complaintId, String newStatus, String officerUsername) throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL UpdateComplaintStatus(?, ?, ?)}");
        try {
            cst.setInt(1, complaintId);
            cst.setString(2, newStatus);
            cst.setString(3, officerUsername);
            
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    
    /**
     * Execute GetComplaintAnalytics stored procedure
     */
    public Map<String, Integer> getComplaintAnalytics() throws SQLException {
        Map<String, Integer> analytics = new HashMap<>();
        CallableStatement cst = con.prepareCall("{CALL GetComplaintAnalytics()}");
        ResultSet rs = null;
        
        try {
            rs = cst.executeQuery();
            while (rs.next()) {
                analytics.put("total_complaints", rs.getInt("total_complaints"));
                analytics.put("pending_complaints", rs.getInt("pending_complaints"));
                analytics.put("in_progress_complaints", rs.getInt("in_progress_complaints"));
                analytics.put("resolved_complaints", rs.getInt("resolved_complaints"));
                analytics.put("total_users", rs.getInt("total_users"));
                analytics.put("total_officers", rs.getInt("total_officers"));
            }
        } finally {
            if (rs != null) rs.close();
            cst.close();
        }
        
        return analytics;
    }
    
    /**
     * Execute GetOfficerPerformance stored procedure
     */
    public Map<String, Object> getOfficerPerformance(String officerUsername) throws SQLException {
        Map<String, Object> performance = new HashMap<>();
        CallableStatement cst = con.prepareCall("{CALL GetOfficerPerformance(?)}");
        ResultSet rs = null;
        
        try {
            cst.setString(1, officerUsername);
            rs = cst.executeQuery();
            
            if (rs.next()) {
                performance.put("officer_username", rs.getString("officer_username"));
                performance.put("active_complaints", rs.getInt("active_complaints"));
                performance.put("resolved_complaints", rs.getInt("resolved_complaints"));
                performance.put("workload_rank", rs.getInt("workload_rank"));
                performance.put("load_status", rs.getString("load_status"));
            }
        } finally {
            if (rs != null) rs.close();
            cst.close();
        }
        
        return performance;
    }
    
    /**
     * Execute AddEvidence stored procedure
     */
    public boolean addEvidence(int complaintId, String userUsername, String evidenceType, String evidenceUrl, String description) throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL AddEvidence(?, ?, ?, ?, ?)}");
        try {
            cst.setInt(1, complaintId);
            cst.setString(2, userUsername);
            cst.setString(3, evidenceType);
            cst.setString(4, evidenceUrl);
            cst.setString(5, description);
            
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    
    /**
     * Execute InsertUser stored procedure
     */
    public boolean insertUser(String username, String password, String email, String phone, String area) throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL InsertUser(?, ?, ?, ?, ?)}");
        try {
            cst.setString(1, username);
            cst.setString(2, password);
            cst.setString(3, email);
            cst.setString(4, phone);
            cst.setString(5, area);
            
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    
    /**
     * Execute InsertOfficer stored procedure
     */
    public boolean insertOfficer(String username, String password, String area, String category) throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL InsertOfficer(?, ?, ?, ?)}");
        try {
            cst.setString(1, username);
            cst.setString(2, password);
            cst.setString(3, area);
            cst.setString(4, category);
            
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    
    /**
     * Execute DeleteUser stored procedure
     */
    public boolean deleteUser(String username) throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL DeleteUser(?)}");
        try {
            cst.setString(1, username);
            
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    
    /**
     * Execute DeleteOfficer stored procedure
     */
    public boolean deleteOfficer(String username) throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL DeleteOfficer(?)}");
        try {
            cst.setString(1, username);
            
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    

    
    /**
     * Create evidence table using stored procedure
     */
    public boolean createEvidenceTable() throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL CreateEvidenceTable()}");
        try {
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    
    /**
     * Reset evidence table by dropping and recreating it
     */
    public boolean resetEvidenceTable() throws SQLException {
        CallableStatement cst = con.prepareCall("{CALL ResetEvidenceTable()}");
        try {
            return cst.executeUpdate() > 0;
        } finally {
            cst.close();
        }
    }
    

}