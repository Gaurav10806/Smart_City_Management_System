-- Check if evidence table exists
SHOW TABLES LIKE 'evidence';

-- Check structure of evidence table if it exists
SHOW CREATE TABLE evidence;

-- Check if complaints table exists
SHOW TABLES LIKE 'complaints';

-- Insert a test record (if tables exist)
INSERT INTO evidence (complain_id, user_username, evidence_type, evidence_url, description) 
VALUES (1, 'testuser', 'image', 'http://example.com/test.jpg', 'Test evidence');

-- Verify the insert
SELECT * FROM evidence;
