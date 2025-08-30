-- Insert default admin user
-- Password: admin123 (hashed with BCrypt)
INSERT INTO users (id, email, password_hash, first_name, last_name, role, is_active) VALUES 
(
    '550e8400-e29b-41d4-a716-446655440000',
    'admin@ticketsystem.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'System',
    'Administrator',
    'ADMIN',
    true
);

-- Insert sample support agent
-- Password: agent123 (hashed with BCrypt)
INSERT INTO users (id, email, password_hash, first_name, last_name, role, is_active) VALUES 
(
    '550e8400-e29b-41d4-a716-446655440001',
    'agent@ticketsystem.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'Support',
    'Agent',
    'SUPPORT_AGENT',
    true
);

-- Insert sample regular user
-- Password: user123 (hashed with BCrypt)
INSERT INTO users (id, email, password_hash, first_name, last_name, role, is_active) VALUES 
(
    '550e8400-e29b-41d4-a716-446655440002',
    'user@ticketsystem.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'John',
    'Doe',
    'USER',
    true
);

-- Insert sample tickets
INSERT INTO tickets (id, subject, description, status, priority, created_by_id, assigned_to_id) VALUES 
(
    '660e8400-e29b-41d4-a716-446655440000',
    'Login Issues',
    'I am unable to log into my account. The system keeps showing "Invalid credentials" even though I am sure my password is correct.',
    'OPEN',
    'HIGH',
    '550e8400-e29b-41d4-a716-446655440002',
    '550e8400-e29b-41d4-a716-446655440001'
),
(
    '660e8400-e29b-41d4-a716-446655440001',
    'Email Notifications Not Working',
    'I am not receiving email notifications for ticket updates. Please check the email configuration.',
    'IN_PROGRESS',
    'MEDIUM',
    '550e8400-e29b-41d4-a716-446655440002',
    '550e8400-e29b-41d4-a716-446655440001'
),
(
    '660e8400-e29b-41d4-a716-446655440002',
    'Feature Request: Dark Mode',
    'It would be great to have a dark mode option in the application for better user experience during night time usage.',
    'OPEN',
    'LOW',
    '550e8400-e29b-41d4-a716-446655440002',
    NULL
);

-- Insert sample comments
INSERT INTO comments (content, ticket_id, author_id) VALUES 
(
    'Thank you for reporting this issue. I will investigate and get back to you shortly.',
    '660e8400-e29b-41d4-a716-446655440000',
    '550e8400-e29b-41d4-a716-446655440001'
),
(
    'I have checked the email configuration and found the issue. Working on a fix now.',
    '660e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440001'
),
(
    'This is a great suggestion! I will add it to our feature backlog for consideration.',
    '660e8400-e29b-41d4-a716-446655440002',
    '550e8400-e29b-41d4-a716-446655440001'
);
