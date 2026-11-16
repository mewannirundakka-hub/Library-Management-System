-- =====================================================================
-- University of Uva Wellassa - Department of ICT
-- Module: ICT 431-2 Object Oriented Design Patterns and Principals
-- Project: Library Management System
-- File: library_system_datagrip.sql
-- Compatible with: DataGrip / MySQL / MariaDB / PostgreSQL
-- Purpose: Complete database schema, relational constraints, 
--          and test seed data for the 4 core entities:
--          Book, Member, Loan, and Notification.
-- =====================================================================

-- Drop tables if existing (in reverse dependency order)
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;

-- =====================================================================
-- 1. BOOKS TABLE
-- Represents physical catalog items, including borrowable and reference items
-- =====================================================================
CREATE TABLE books (
    book_id         VARCHAR(36) PRIMARY KEY,
    isbn            VARCHAR(20) NOT NULL UNIQUE,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    category        VARCHAR(100) NOT NULL,
    is_reference    BOOLEAN NOT NULL DEFAULT FALSE,      -- Used in LSP demonstration (Reference books cannot be loaned)
    is_issued       BOOLEAN NOT NULL DEFAULT FALSE,
    rack_location   VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
-- 2. MEMBERS TABLE
-- Represents patrons (Students, Faculty, and Guests)
-- =====================================================================
CREATE TABLE members (
    member_id           VARCHAR(36) PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    email               VARCHAR(150) NOT NULL UNIQUE,
    phone               VARCHAR(30),
    member_type         VARCHAR(50) NOT NULL,            -- 'STUDENT', 'FACULTY', 'GUEST' (Used in OCP fine calculation)
    max_borrow_limit    INT NOT NULL DEFAULT 3,
    total_fines_pending DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'SUSPENDED'
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
-- 3. LOANS TABLE
-- Represents the borrowing lifecycle and fine tracking
-- =====================================================================
CREATE TABLE loans (
    loan_id         VARCHAR(36) PRIMARY KEY,
    book_id         VARCHAR(36) NOT NULL,
    member_id       VARCHAR(36) NOT NULL,
    issue_date      DATE NOT NULL,
    due_date        DATE NOT NULL,
    return_date     DATE DEFAULT NULL,
    fine_amount     DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'RETURNED', 'OVERDUE'
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books (book_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_loans_member FOREIGN KEY (member_id) REFERENCES members (member_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =====================================================================
-- 4. NOTIFICATIONS TABLE
-- Records alerts dispatched via communication channels (Email, SMS)
-- =====================================================================
CREATE TABLE notifications (
    notification_id VARCHAR(36) PRIMARY KEY,
    member_id       VARCHAR(36) NOT NULL,
    loan_id         VARCHAR(36) DEFAULT NULL,
    channel         VARCHAR(20) NOT NULL,               -- 'EMAIL', 'SMS' (Used in DIP channel injection)
    recipient       VARCHAR(150) NOT NULL,
    message         TEXT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'SENT', -- 'PENDING', 'SENT', 'FAILED'
    sent_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_member FOREIGN KEY (member_id) REFERENCES members (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_notif_loan FOREIGN KEY (loan_id) REFERENCES loans (loan_id) ON DELETE SET NULL
);

-- =====================================================================
-- INDEXES FOR PERFORMANCE & FOREIGN KEY LOOKUPS
-- =====================================================================
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_members_type ON members(member_type);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_due_date ON loans(due_date);
CREATE INDEX idx_notif_status ON notifications(status);

-- =====================================================================
-- SAMPLE SEED DATA
-- Designed for demonstration of all 5 SOLID scenarios
-- =====================================================================

-- Insert Books (Including both borrowable and reference books for LSP testing)
INSERT INTO books (book_id, isbn, title, author, category, is_reference, is_issued, rack_location) VALUES
('B001', '978-0132350884', 'Clean Code', 'Robert C. Martin', 'Software Engineering', FALSE, TRUE, 'Shelf-A1'),
('B002', '978-0201633610', 'Design Patterns: Elements of Reusable OO Software', 'Gang of Four', 'Computer Science', FALSE, FALSE, 'Shelf-A2'),
('B003', '978-0134685991', 'Effective Java (3rd Edition)', 'Joshua Bloch', 'Programming', FALSE, FALSE, 'Shelf-B1'),
('B004', '978-0131103627', 'The C Programming Language (Encyclopedia Edition)', 'Brian Kernighan', 'Reference Materials', TRUE, FALSE, 'Ref-Desk-1');

-- Insert Members (Different membership tiers for OCP fine policy testing)
INSERT INTO members (member_id, name, email, phone, member_type, max_borrow_limit, total_fines_pending, status) VALUES
('M001', 'Kasun Perera', 'kasun.p@std.uwu.ac.lk', '+94712345678', 'STUDENT', 3, 0.00, 'ACTIVE'),
('M002', 'Dr. Nimal Silva', 'nimal.silva@uwu.ac.lk', '+94779876543', 'FACULTY', 10, 0.00, 'ACTIVE'),
('M003', 'John Doe', 'john.doe@guest.com', '+94751122334', 'GUEST', 1, 150.00, 'ACTIVE');

-- Insert Loans (Active loan and overdue loan for fine calculation demonstration)
INSERT INTO loans (loan_id, book_id, member_id, issue_date, due_date, return_date, fine_amount, status) VALUES
('L001', 'B001', 'M001', '2026-11-01', '2026-11-15', NULL, 50.00, 'OVERDUE');

-- Insert Notification History (Demonstrating DIP communication log)
INSERT INTO notifications (notification_id, member_id, loan_id, channel, recipient, message, status, sent_at) VALUES
('N001', 'M001', 'L001', 'EMAIL', 'kasun.p@std.uwu.ac.lk', 'Your loan for Clean Code was issued successfully. Due date: 2026-11-15.', 'SENT', '2026-11-01 10:00:00'),
('N002', 'M001', 'L001', 'SMS', '+94712345678', 'Reminder: Clean Code is overdue. Fine accumulated: LKR 50.00.', 'SENT', '2026-11-16 08:30:00');

-- =====================================================================
-- USEFUL DATA VISUALIZATION / DATAGRIP CONSOLE QUERIES
-- =====================================================================

-- 1. View all active loans with joined book and member details
SELECT 
    l.loan_id,
    b.title AS book_title,
    b.isbn,
    m.name AS member_name,
    m.member_type,
    l.issue_date,
    l.due_date,
    l.status,
    l.fine_amount
FROM loans l
JOIN books b ON l.book_id = b.book_id
JOIN members m ON l.member_id = m.member_id;

-- 2. View all reference materials (restricted from loaning - LSP)
SELECT book_id, title, author, category, rack_location
FROM books
WHERE is_reference = TRUE;

-- 3. Audit notifications dispatched by member
SELECT 
    n.notification_id,
    m.name AS recipient_name,
    n.channel,
    n.recipient,
    n.message,
    n.status,
    n.sent_at
FROM notifications n
JOIN members m ON n.member_id = m.member_id
ORDER BY n.sent_at DESC;
