-- Add test users
INSERT INTO users (email, name, created_at, role, password, status)
VALUES 
    ('customer@example.com', 'Test Customer', CURRENT_TIMESTAMP, 'CUSTOMER', 'password123', 'ACTIVE'),
    ('staff@example.com', 'Test Staff', CURRENT_TIMESTAMP, 'STAFF', 'password123', 'ACTIVE'),
    ('admin@example.com', 'Test Admin', CURRENT_TIMESTAMP, 'ADMIN', 'password123', 'ACTIVE'); 

