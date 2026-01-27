-- MentorBridge Database Schema for XAMPP MySQL
-- Run this in phpMyAdmin or MySQL command line

CREATE DATABASE IF NOT EXISTS mentorbridge_db;
USE mentorbridge_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User tokens table for authentication
CREATE TABLE IF NOT EXISTS user_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Mentors table
CREATE TABLE IF NOT EXISTS mentors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(100) NOT NULL,
    expertise VARCHAR(100) NOT NULL,
    bio TEXT,
    experience INT DEFAULT 0,
    hourly_rate DECIMAL(10, 2) DEFAULT 0.00,
    approval_status ENUM('pending', 'approved', 'rejected') DEFAULT 'approved',
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_expertise (expertise),
    INDEX idx_approval_status (approval_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sessions table
CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mentor_id INT NOT NULL,
    user_id INT NOT NULL,
    session_date DATE NOT NULL,
    session_time TIME NOT NULL,
    topic VARCHAR(255),
    status ENUM('pending', 'confirmed', 'completed', 'cancelled') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentor_id) REFERENCES mentors(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_mentor_id (mentor_id),
    INDEX idx_date (session_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mentor_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentor_id) REFERENCES mentors(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_mentor_id (mentor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Mentees table
CREATE TABLE IF NOT EXISTS mentees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Availability table for mentor schedules
CREATE TABLE IF NOT EXISTS availability (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mentor_id INT NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentor_id) REFERENCES mentors(id) ON DELETE CASCADE,
    INDEX idx_mentor_id (mentor_id),
    INDEX idx_day_time (day_of_week, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data - 10 users total
-- 1 Admin, 5 Mentors, 4 Mentees
-- All passwords: password (hashed)
INSERT INTO users (name, email, password) VALUES
-- Admin User (ID: 1)
('Admin User', 'admin@mentorbridge.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),

-- Mentor Users (ID: 2-6)
('Sarah Chen', 'sarah.chen@mentorbridge.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('Michael Rodriguez', 'michael.rodriguez@mentorbridge.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('Emma Thompson', 'emma.thompson@mentorbridge.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('David Kim', 'david.kim@mentorbridge.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('Lisa Anderson', 'lisa.anderson@mentorbridge.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),

-- Mentee Users (ID: 7-10)
('Alex Johnson', 'alex.johnson@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('Priya Patel', 'priya.patel@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('Marcus Williams', 'marcus.williams@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('Sofia Martinez', 'sofia.martinez@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');

-- Insert 5 mentors (one per category) with user_id references
INSERT INTO mentors (user_id, name, expertise, bio, experience, hourly_rate, approval_status, image_url) VALUES
-- Mentor 1: Software Development (user_id: 2)
(2, 'Sarah Chen', 'Software Development', 'Senior software engineer with 10+ years of experience in full-stack development. Specializing in Java, Python, and mobile app development. Former tech lead at Google.', 10, 75.00, 'approved', 'https://randomuser.me/api/portraits/women/1.jpg'),

-- Mentor 2: Data Science (user_id: 3)
(3, 'Michael Rodriguez', 'Data Science', 'Data scientist and ML engineer with PhD in Computer Science. Expert in Python, TensorFlow, and statistical analysis. Published researcher with 8 years industry experience at Microsoft.', 8, 85.00, 'approved', 'https://randomuser.me/api/portraits/men/2.jpg'),

-- Mentor 3: UI/UX Design (user_id: 4)
(4, 'Emma Thompson', 'UI/UX Design', 'Creative UI/UX designer with a passion for user-centered design. Lead designer at Adobe for 7 years. Expert in Figma, Adobe XD, and design thinking methodologies.', 7, 65.00, 'approved', 'https://randomuser.me/api/portraits/women/3.jpg'),

-- Mentor 4: Cloud Architecture (user_id: 5)
(5, 'David Kim', 'Cloud Architecture', 'AWS and Azure certified cloud architect with 12 years experience. Former Solutions Architect at Amazon. Helping businesses migrate to cloud and optimize infrastructure.', 12, 95.00, 'approved', 'https://randomuser.me/api/portraits/men/4.jpg'),

-- Mentor 5: Career Coaching (user_id: 6)
(6, 'Lisa Anderson', 'Career Coaching', 'Career development coach specializing in tech careers. Former Senior Tech Recruiter at LinkedIn for 15 years. Expert in resume building, interview prep, and career transitions.', 15, 60.00, 'approved', 'https://randomuser.me/api/portraits/women/5.jpg'),

-- Test Pending Mentor (user_id: 7 - Alex Johnson mentee wants to become a mentor)
(7, 'Alex Johnson', 'Development', 'Aspiring mentor with 3 years of coding experience. Wants to help beginners learn programming.', 3, 50.00, 'pending', NULL);

-- Insert default mentee (Shokor) and existing mentees
-- Password for shokor@gmail.com: shkrsmart (hashed with bcrypt)
INSERT INTO users (name, email, password) VALUES
('Shokor', 'shokor@gmail.com', '$2y$10$E9m9IecBov.vphO38uEq/uoBd91DfR.eizio/Eicnkqadf0gMohq.');

-- Insert into mentees table
INSERT INTO mentees (user_id, full_name, email) VALUES
-- Shokor (user_id will be auto-incremented, but should be 11 after existing inserts)
(11, 'Shokor', 'shokor@gmail.com'),
-- Existing mentees from users table (IDs 7-10 except 7 who is pending mentor)
(8, 'Priya Patel', 'priya.patel@example.com'),
(9, 'Marcus Williams', 'marcus.williams@example.com'),
(10, 'Sofia Martinez', 'sofia.martinez@example.com');

-- Insert realistic sessions (15 completed, 5 upcoming, 3 cancelled)
-- Past completed sessions (January 2026)
INSERT INTO sessions (mentor_id, user_id, session_date, session_time, topic, status) VALUES
-- Completed sessions - Week 1
(1, 7, '2026-01-06', '10:00:00', 'Android App Development Basics', 'completed'),
(2, 8, '2026-01-07', '14:00:00', 'Introduction to Machine Learning', 'completed'),
(3, 9, '2026-01-08', '16:00:00', 'UI/UX Portfolio Review', 'completed'),
(4, 10, '2026-01-09', '11:00:00', 'AWS Fundamentals', 'completed'),
(5, 7, '2026-01-10', '15:00:00', 'Resume Review and Optimization', 'completed'),

-- Completed sessions - Week 2
(1, 8, '2026-01-13', '09:00:00', 'RESTful API Design', 'completed'),
(2, 9, '2026-01-14', '13:00:00', 'Data Analysis with Python', 'completed'),
(3, 10, '2026-01-15', '10:30:00', 'Mobile App Design Principles', 'completed'),
(4, 7, '2026-01-16', '14:30:00', 'Cloud Migration Strategy', 'completed'),
(5, 8, '2026-01-17', '16:00:00', 'Interview Preparation Workshop', 'completed'),

-- Completed sessions - Week 3
(1, 9, '2026-01-20', '11:00:00', 'Database Design Best Practices', 'completed'),
(2, 10, '2026-01-21', '15:00:00', 'Deep Learning Fundamentals', 'completed'),
(3, 7, '2026-01-22', '09:30:00', 'User Research Methods', 'completed'),
(4, 8, '2026-01-23', '13:00:00', 'Docker and Kubernetes Basics', 'completed'),
(5, 9, '2026-01-24', '10:00:00', 'Career Path Planning', 'completed'),

-- Upcoming sessions (February 2026)
(1, 10, '2026-02-03', '10:00:00', 'Advanced Android Features', 'confirmed'),
(2, 7, '2026-02-04', '14:00:00', 'Neural Networks Workshop', 'confirmed'),
(3, 8, '2026-02-05', '16:00:00', 'Design System Creation', 'pending'),
(4, 9, '2026-02-06', '11:00:00', 'Serverless Architecture', 'pending'),
(5, 10, '2026-02-07', '15:00:00', 'Salary Negotiation Tips', 'confirmed'),

-- Cancelled sessions
(1, 7, '2026-01-11', '14:00:00', 'Code Review Session', 'cancelled'),
(3, 9, '2026-01-18', '10:00:00', 'Wireframing Workshop', 'cancelled'),
(5, 8, '2026-01-25', '13:00:00', 'LinkedIn Profile Optimization', 'cancelled');

-- Insert sample availability for all 5 approved mentors
-- NOTE: Availability slots that match confirmed/completed sessions should NOT be included
-- because they would have been removed after payment
INSERT INTO availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES
-- Sarah Chen (Mentor ID: 1) - Software Development
-- Excluded: Monday 10:00 (Feb 3 confirmed session)
(1, 'Monday', '09:00:00', '10:00:00', 1),
-- (1, 'Monday', '10:00:00', '11:00:00', 1),  -- REMOVED: booked Feb 3
(1, 'Tuesday', '14:00:00', '15:00:00', 1),
(1, 'Tuesday', '15:00:00', '16:00:00', 1),
(1, 'Wednesday', '09:00:00', '10:00:00', 1),
(1, 'Wednesday', '10:00:00', '11:00:00', 1),
(1, 'Thursday', '14:00:00', '15:00:00', 1),
(1, 'Thursday', '15:00:00', '16:00:00', 1),
(1, 'Friday', '09:00:00', '10:00:00', 1),
(1, 'Friday', '10:00:00', '11:00:00', 1),

-- Michael Rodriguez (Mentor ID: 2) - Data Science
-- Excluded: Tuesday 14:00 (Feb 4 confirmed session)
(2, 'Monday', '10:00:00', '11:00:00', 1),
(2, 'Monday', '14:00:00', '15:00:00', 1),
(2, 'Tuesday', '09:00:00', '10:00:00', 1),
-- (2, 'Tuesday', '14:00:00', '15:00:00', 1),  -- REMOVED: booked Feb 4 (14:00 session)
(2, 'Tuesday', '15:00:00', '16:00:00', 1),
(2, 'Wednesday', '10:00:00', '11:00:00', 1),
(2, 'Wednesday', '14:00:00', '15:00:00', 1),
(2, 'Thursday', '09:00:00', '10:00:00', 1),
(2, 'Thursday', '15:00:00', '16:00:00', 1),
(2, 'Friday', '10:00:00', '11:00:00', 1),
(2, 'Friday', '14:00:00', '15:00:00', 1),

-- Emma Thompson (Mentor ID: 3) - UI/UX Design
-- Wednesday 16:00 disabled (pending session Feb 5)
(3, 'Monday', '09:00:00', '10:00:00', 1),
(3, 'Tuesday', '09:00:00', '10:00:00', 1),
(3, 'Tuesday', '10:00:00', '11:00:00', 1),
(3, 'Wednesday', '14:00:00', '15:00:00', 1),
(3, 'Wednesday', '15:00:00', '16:00:00', 1),
(3, 'Wednesday', '16:00:00', '17:00:00', 0),  -- DISABLED: pending session Feb 5
(3, 'Thursday', '09:00:00', '10:00:00', 1),
(3, 'Thursday', '10:00:00', '11:00:00', 1),
(3, 'Friday', '14:00:00', '15:00:00', 1),
(3, 'Friday', '15:00:00', '16:00:00', 1),

-- David Kim (Mentor ID: 4) - Cloud Architecture
-- Thursday 11:00 disabled (pending session Feb 6)
(4, 'Monday', '11:00:00', '12:00:00', 1),
(4, 'Monday', '13:00:00', '14:00:00', 1),
(4, 'Tuesday', '11:00:00', '12:00:00', 1),
(4, 'Tuesday', '13:00:00', '14:00:00', 1),
(4, 'Wednesday', '11:00:00', '12:00:00', 1),
(4, 'Wednesday', '13:00:00', '14:00:00', 1),
(4, 'Thursday', '11:00:00', '12:00:00', 0),  -- DISABLED: pending session Feb 6
(4, 'Thursday', '13:00:00', '14:00:00', 1),
(4, 'Friday', '11:00:00', '12:00:00', 1),
(4, 'Friday', '13:00:00', '14:00:00', 1),

-- Lisa Anderson (Mentor ID: 5) - Career Coaching
-- Excluded: Friday 15:00 (Feb 7 confirmed session)
(5, 'Monday', '10:00:00', '11:00:00', 1),
(5, 'Monday', '15:00:00', '16:00:00', 1),
(5, 'Tuesday', '10:00:00', '11:00:00', 1),
(5, 'Tuesday', '15:00:00', '16:00:00', 1),
(5, 'Wednesday', '10:00:00', '11:00:00', 1),
(5, 'Wednesday', '15:00:00', '16:00:00', 1),
(5, 'Thursday', '10:00:00', '11:00:00', 1),
(5, 'Thursday', '15:00:00', '16:00:00', 1),
(5, 'Friday', '10:00:00', '11:00:00', 1);
-- (5, 'Friday', '15:00:00', '16:00:00', 1);  -- REMOVED: booked Feb 7

-- Insert realistic reviews (15 total - one for each completed session)
INSERT INTO reviews (mentor_id, user_id, rating, comment) VALUES
-- Sarah Chen (Software Development) - 3 reviews, avg 4.7
(1, 7, 5, 'Sarah is an excellent mentor! Her explanations are clear and she provides great real-world examples. Highly recommend!'),
(1, 8, 5, 'Very knowledgeable about API design. Gave me practical tips I could implement immediately.'),
(1, 9, 4, 'Great session on database design. Would have liked more time for Q&A.'),

-- Michael Rodriguez (Data Science) - 3 reviews, avg 4.7
(2, 8, 5, 'Michael made machine learning concepts easy to understand. Best intro session I\'ve had!'),
(2, 9, 4, 'Solid understanding of data analysis. Helpful Python tips and resources.'),
(2, 10, 5, 'Deep learning session was outstanding! Very patient and thorough.'),

-- Emma Thompson (UI/UX Design) - 3 reviews, avg 5.0
(3, 9, 5, 'Emma gave incredible feedback on my portfolio. Her design expertise is top-notch!'),
(3, 10, 5, 'Learned so much about mobile design principles. Emma is amazing!'),
(3, 7, 5, 'User research methods session was eye-opening. Highly professional!'),

-- David Kim (Cloud Architecture) - 3 reviews, avg 4.3
(4, 10, 4, 'Good AWS fundamentals session. David knows his stuff!'),
(4, 7, 5, 'Cloud migration strategy was exactly what I needed. Very helpful!'),
(4, 8, 4, 'Docker and Kubernetes session was informative. Could use more hands-on examples.'),

-- Lisa Anderson (Career Coaching) - 3 reviews, avg 4.7
(5, 7, 5, 'Lisa transformed my resume! Got 3 interviews within a week. Worth every penny!'),
(5, 8, 5, 'Interview prep was fantastic. Lisa\'s tips helped me land my dream job!'),
(5, 9, 4, 'Good career planning session. Gave me clear direction for my career path.');
