-- Drop the procedure if it already exists
DROP PROCEDURE IF EXISTS AddEvidence;

-- Create the procedure
DELIMITER //
CREATE PROCEDURE AddEvidence(
    IN p_complain_id INT,
    IN p_username VARCHAR(50),
    IN p_evidence_type VARCHAR(50),
    IN p_evidence_url TEXT,
    IN p_description TEXT
)
BEGIN
    INSERT INTO evidence (
        complain_id,
        user_username,
        evidence_type,
        evidence_url,
        description
    ) VALUES (
        p_complain_id,
        p_username,
        p_evidence_type,
        p_evidence_url,
        p_description
    );
    
    SELECT '✅ Evidence added successfully' AS message;
END //
DELIMITER ;
