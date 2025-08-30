-- Development seed data for testing
-- This file contains additional test data for development environment

-- Additional test users
INSERT INTO users (email, password_hash, first_name, last_name, role, is_active) VALUES 
('alice.smith@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Alice', 'Smith', 'USER', true),
('bob.johnson@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Bob', 'Johnson', 'USER', true),
('carol.williams@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Carol', 'Williams', 'SUPPORT_AGENT', true),
('david.brown@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'David', 'Brown', 'SUPPORT_AGENT', true),
('eve.davis@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Eve', 'Davis', 'ADMIN', true);

-- Additional test tickets with various statuses and priorities
INSERT INTO tickets (subject, description, status, priority, created_by_id, assigned_to_id) VALUES 
('Password Reset Not Working', 'The password reset link in the email is not working. When I click it, I get a 404 error.', 'OPEN', 'HIGH', 
    (SELECT id FROM users WHERE email = 'alice.smith@example.com'), 
    (SELECT id FROM users WHERE email = 'carol.williams@example.com')),
    
('Slow Performance on Dashboard', 'The dashboard is loading very slowly, especially when there are many tickets. It takes more than 30 seconds to load.', 'IN_PROGRESS', 'MEDIUM', 
    (SELECT id FROM users WHERE email = 'bob.johnson@example.com'), 
    (SELECT id FROM users WHERE email = 'david.brown@example.com')),
    
('Mobile App Crashes', 'The mobile app crashes when I try to upload an attachment. This happens consistently on iOS devices.', 'RESOLVED', 'HIGH', 
    (SELECT id FROM users WHERE email = 'alice.smith@example.com'), 
    (SELECT id FROM users WHERE email = 'carol.williams@example.com')),
    
('Add Export Feature', 'It would be helpful to have an export feature to download ticket data as CSV or PDF for reporting purposes.', 'OPEN', 'LOW', 
    (SELECT id FROM users WHERE email = 'bob.johnson@example.com'), 
    NULL),
    
('Security Vulnerability Report', 'I found a potential XSS vulnerability in the comment section. Please investigate this security issue.', 'CLOSED', 'URGENT', 
    (SELECT id FROM users WHERE email = 'alice.smith@example.com'), 
    (SELECT id FROM users WHERE email = 'david.brown@example.com')),
    
('Integration with Slack', 'Can we integrate the ticket system with Slack to get notifications in our team channel?', 'OPEN', 'MEDIUM', 
    (SELECT id FROM users WHERE email = 'bob.johnson@example.com'), 
    (SELECT id FROM users WHERE email = 'carol.williams@example.com')),
    
('Bulk Actions for Tickets', 'Need ability to perform bulk actions like closing multiple tickets at once or bulk assignment.', 'IN_PROGRESS', 'MEDIUM', 
    (SELECT id FROM users WHERE email = 'alice.smith@example.com'), 
    (SELECT id FROM users WHERE email = 'david.brown@example.com')),
    
('API Rate Limiting', 'The API should have rate limiting to prevent abuse and ensure fair usage across all users.', 'RESOLVED', 'LOW', 
    (SELECT id FROM users WHERE email = 'bob.johnson@example.com'), 
    (SELECT id FROM users WHERE email = 'carol.williams@example.com'));

-- Add comments to some tickets
INSERT INTO comments (content, ticket_id, author_id) VALUES 
('I can reproduce this issue. Looking into the password reset service logs.', 
    (SELECT id FROM tickets WHERE subject = 'Password Reset Not Working'), 
    (SELECT id FROM users WHERE email = 'carol.williams@example.com')),
    
('Performance analysis shows the issue is with the database queries. Optimizing now.', 
    (SELECT id FROM tickets WHERE subject = 'Slow Performance on Dashboard'), 
    (SELECT id FROM users WHERE email = 'david.brown@example.com')),
    
('Fixed the mobile app crash. The issue was with the file upload library. Please test the latest version.', 
    (SELECT id FROM tickets WHERE subject = 'Mobile App Crashes'), 
    (SELECT id FROM users WHERE email = 'carol.williams@example.com')),
    
('Thank you for the security report. We have patched the vulnerability and deployed the fix.', 
    (SELECT id FROM tickets WHERE subject = 'Security Vulnerability Report'), 
    (SELECT id FROM users WHERE email = 'david.brown@example.com')),
    
('Great suggestion! I will research Slack integration options and provide an estimate.', 
    (SELECT id FROM tickets WHERE subject = 'Integration with Slack'), 
    (SELECT id FROM users WHERE email = 'carol.williams@example.com'));

-- Add some ticket ratings
INSERT INTO ticket_ratings (rating, feedback, ticket_id, rated_by_id) VALUES 
(5, 'Excellent support! The issue was resolved quickly and the agent was very helpful.', 
    (SELECT id FROM tickets WHERE subject = 'Mobile App Crashes'), 
    (SELECT id FROM users WHERE email = 'alice.smith@example.com')),
    
(4, 'Good resolution, but it took a bit longer than expected.', 
    (SELECT id FROM tickets WHERE subject = 'Security Vulnerability Report'), 
    (SELECT id FROM users WHERE email = 'alice.smith@example.com')),
    
(5, 'Perfect! The API rate limiting works exactly as requested.', 
    (SELECT id FROM tickets WHERE subject = 'API Rate Limiting'), 
    (SELECT id FROM users WHERE email = 'bob.johnson@example.com'));
