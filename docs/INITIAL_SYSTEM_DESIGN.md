# Initial System Design (Before SOLID Application)
**ICT 431-2 Object Oriented Design Patterns and Principals**  
**Assessment:** Group Project (Library Management System)  
**Ticket:** `LMS-EP1-01: Domain Modeling & Monolithic Class Design`  
**Target Report Sections:** Section 2.1 & Section 2.2

---

## 2.1 Problem Description & Business Domain

### 2.1.1 Domain Overview
The system models a university library handling four core business entities:
1. **Book:** Represents physical and reference library materials with metadata (ISBN, Title, Author, Category, Borrowing Status).
2. **Member:** Represents registered patrons (e.g., Undergraduate Student, Faculty Member, and Guest/Visitor).
3. **Loan:** Tracks the borrowing lifecycle (Issue Date, Due Date, Return Date, Overdue Calculation, and Fine Incurred).
4. **Notification:** Communicates alerts (Loan issued confirmation, Due date reminders, and Overdue fine warnings) via communication channels (Email, SMS).

### 2.1.2 Problem Statement & Architectural Flaws
In the initial un-refactored system, the system was designed following procedural, ad-hoc object-oriented practices commonly found in naive legacy systems. While the system appears superficially operational for basic happy-path cases, it severely violates every core object-oriented design principle:

1. **Monolithic God Class (`LibraryManager`):**  
   Contains user interaction, business transaction logic, direct SQL string persistence, and hardcoded notification dispatches. Changing any one detail forces recompilation and risks breaking completely unrelated features (**SRP Violation**).
2. **Hardcoded Branching Logic:**  
   Fine calculations and loan durations are computed via cascaded `if-else` and `switch` blocks inspecting raw member type strings or integers. Introducing a new membership tier (e.g., `PostgraduateResearcher`) requires editing and re-testing core calculation routines (**OCP Violation**).
3. **Fragile Inheritance & Broken Subtyping:**  
   `ReferenceBook` inherits from `Book`, but reference materials cannot legally be borrowed. Rather than separating borrowing capabilities, `ReferenceBook.borrowItem()` throws `UnsupportedOperationException`. Any polymorphic iteration over books that calls `borrowItem()` will abruptly crash at runtime (**LSP Violation**).
4. **Fat, Coercive Interfaces (`ILibraryOperations`):**  
   A single interface forces all library items and entities to implement methods they have no use for (e.g., `borrow()`, `reserve()`, `renew()`, `printBarcode()`, `streamAudio()`, `exportToPdf()`). Implementing classes must resort to throwing dummy exceptions or providing blank bodies (**ISP Violation**).
5. **Direct Coupling to Low-Level Infrastructure:**  
   High-level business operations instantiate low-level concrete drivers directly (`new EmailService()`, `new MySQLDatabase()`) using `new` operators inside methods. It is impossible to unit-test the business logic with mocks, switch database providers, or introduce alternative notification channels without modifying high-level code (**DIP Violation**).

---

## 2.2 Initial Class Diagram (Before SOLID)

The following Mermaid UML diagram depicts the initial un-factored architecture, highlighting the tight coupling, bloated interfaces, and improper inheritance.

```mermaid
classDiagram
    direction TB

    %% Fat Interface (ISP Violation)
    class ILibraryOperations {
        <<interface>>
        +borrowItem(memberId: String) void
        +returnItem() void
        +renewItem() void
        +reserveItem(memberId: String) void
        +printBarcodeLabel() void
        +streamAudioFile() void
        +exportMetadataToPdf() void
    }

    %% God Class (SRP & DIP Violation)
    class LibraryManager {
        -connectionString: String
        -smtpHost: String
        -books: List~Book~
        -members: List~Member~
        -loans: List~Loan~
        +issueLoan(bookId: String, memberId: String) void
        +returnBook(bookId: String) void
        +calculateFine(loan: Loan) double
        +saveLoanToDatabase(loan: Loan) void
        +sendEmailNotification(memberEmail: String, message: String) void
        +sendSmsNotification(phone: String, message: String) void
        +generateDailyOverdueReport() String
        +renderConsoleMenu() void
    }

    %% Core Entity Book
    class Book {
        -id: String
        -title: String
        -author: String
        -isbn: String
        -isIssued: boolean
        +borrowItem(memberId: String) void
        +returnItem() void
        +renewItem() void
        +reserveItem(memberId: String) void
        +printBarcodeLabel() void
        +streamAudioFile() void
        +exportMetadataToPdf() void
    }

    %% Improper Subtype (LSP Violation)
    class ReferenceBook {
        -readingRoomNumber: String
        +borrowItem(memberId: String) void <<throws UnsupportedOperationException>>
        +renewItem() void <<throws UnsupportedOperationException>>
    }

    %% Member Entity
    class Member {
        -id: String
        -name: String
        -email: String
        -phone: String
        -memberType: String
        -totalFinesPending: double
        +canBorrow() boolean
    }

    class GuestMember {
        +canBorrow() boolean <<throws UnsupportedOperationException>>
    }

    %% Loan Entity
    class Loan {
        -loanId: String
        -book: Book
        -member: Member
        -issueDate: Date
        -dueDate: Date
        -returnDate: Date
        -fineAmount: double
        +calculateOverdueDays() int
        +computeFine() double
    }

    %% Concrete Low-Level Services (DIP & SRP Violations)
    class MySQLDatabaseConnection {
        +connect() void
        +executeUpdate(sql: String) void
        +executeQuery(sql: String) ResultSet
    }

    class SmtpEmailSender {
        +sendMail(to: String, subject: String, body: String) void
    }

    %% Relationships
    ILibraryOperations <|.. Book : implements (forced fat contract)
    Book <|-- ReferenceBook : inherits (breaks LSP)
    Member <|-- GuestMember : inherits (breaks LSP)

    LibraryManager ..> Book : directly manipulates
    LibraryManager ..> Member : directly validates
    LibraryManager ..> Loan : manages & calculates
    LibraryManager ..> MySQLDatabaseConnection : tightly coupled with 'new'
    LibraryManager ..> SmtpEmailSender : tightly coupled with 'new'
    Loan o-- Book
    Loan o-- Member
```

---

## 2.3 Intentional Design Flaws & Member Refactoring Matrix

| SOLID Principle | Violated Component(s) | Specific Architectural Flaw | Assigned Lead |
| :--- | :--- | :--- | :--- |
| **SRP** (Single Responsibility) | `LibraryManager` | Acts as a God Class combining business orchestration, persistence SQL execution, notification sending, report formatting, and input parsing. Has at least 5 distinct reasons to change. | **Member 1** |
| **OCP** (Open/Closed) | `LibraryManager.calculateFine()`, `Loan.computeFine()` | Hardcoded nested `switch(memberType)` / `if-else` blocks for calculating loan periods and overdue fines. Adding a new member tier requires modifying core existing classes. | **Member 2** |
| **LSP** (Liskov Substitution) | `ReferenceBook` extending `Book`, `GuestMember` extending `Member` | `ReferenceBook.borrowItem()` throws `UnsupportedOperationException`. Substituting `ReferenceBook` where `Book` is expected breaks client code at runtime. | **Member 3** |
| **ISP** (Interface Segregation) | `ILibraryOperations` | Fat interface forcing physical books, digital materials, and reference books to implement unused methods (`streamAudioFile()`, `printBarcodeLabel()`). | **Member 4** |
| **DIP** (Dependency Inversion) | `LibraryManager` direct instantiations | High-level module depends directly on low-level concrete classes (`new SmtpEmailSender()`, `new MySQLDatabaseConnection()`) instead of abstractions. | **Member 5** |

---

## 2.4 Deliverable Checklist & Implementation Status
- [x] Clear domain specification for `Book`, `Member`, `Loan`, and `Notification` (`LMS-EP1-01`).
- [x] Formal Problem Description authored for Report Section 2.1 (`LMS-EP1-01`).
- [x] Initial UML Class Diagram rendered in Mermaid for Report Section 2.2 (`LMS-EP1-01`).
- [x] Flaw Identification Matrix cleanly dividing responsibilities across all 5 group members (`LMS-EP1-01`).
- [x] Runnable Java implementation under `src/main/java/com/library/presolid/` (`LMS-EP1-02`).
- [x] Demonstration runner `PreSolidMain.java` executing test flows and runtime error reproduction (`LMS-EP1-02`).

---

## 2.5 Pre-SOLID Execution & Demonstration Output (Report Section 2.3)

The following terminal log was generated by running `com.library.presolid.PreSolidMain` on the compiled initial codebase:

```text
==================================================================
   ICT 431-2: LIBRARY MANAGEMENT SYSTEM (PRE-SOLID BASELINE)      
==================================================================

[SQL EXECUTE - Direct MySQL Driver] INSERT INTO books VALUES ('B001', 'Clean Code')
[SQL EXECUTE - Direct MySQL Driver] INSERT INTO books VALUES ('B002', 'Introduction to Algorithms')
[SQL EXECUTE - Direct MySQL Driver] INSERT INTO books VALUES ('B003', 'Oxford English Dictionary (Vol 1)')
[SQL EXECUTE - Direct MySQL Driver] INSERT INTO members VALUES ('M001', 'Kasun Perera')
[SQL EXECUTE - Direct MySQL Driver] INSERT INTO members VALUES ('M002', 'Dr. Nimal Silva')
[SQL EXECUTE - Direct MySQL Driver] INSERT INTO members VALUES ('M003', 'Guest Visitor')

>>> [DEMO 1: HAPPY PATH LOAN & RETURN WITH FINES] <<<
Book 'Clean Code' borrowed by member: M001
[SQL EXECUTE - Direct MySQL Driver] INSERT INTO loans (loan_id, book_id, member_id, issue_date, due_date) VALUES ('LOAN-1001', 'B001', 'M001', '2026-11-16', '2026-11-30')
[SMTP EMAIL SENT] To: kasun@std.uwu.ac.lk | Subject: Loan Confirmation: Clean Code | Body: Dear Kasun Perera, you have borrowed 'Clean Code'. Due date: 2026-11-30
Loan created successfully: LOAN-1001
Simulating book return on: 2026-12-05 (5 days overdue)...
Book 'Clean Code' returned successfully.
[SMTP EMAIL SENT] To: kasun@std.uwu.ac.lk | Subject: Overdue Fine Incurred | Body: You returned 'Clean Code' overdue by 5 days. Fine: LKR 100.0
[SQL UPDATE - Direct MySQL Driver] UPDATE loans SET return_date = '2026-12-05', fine_amount = 100.0, status = 'RETURNED' WHERE loan_id = 'LOAN-1001'
==========================================
         LIBRARY RETURN RECEIPT           
==========================================
Loan ID     : LOAN-1001
Book Title  : Clean Code
Member Name : Kasun Perera (STUDENT)
Due Date    : 2026-11-30
Return Date : 2026-12-05
Fine Amount : LKR 100.0
==========================================


>>> [DEMO 2: ISP FLAW DEMONSTRATION - Member 4] <<<
Physical book forced to implement audio streaming by ILibraryOperations:
[ERROR - ISP VIOLATION] Physical book cannot stream audio! Method forced by ILibraryOperations.

>>> [DEMO 3: LSP FLAW DEMONSTRATION - Member 3] <<<
Attempting to process ReferenceBook through standard loan pipeline...
Loaning book: Oxford English Dictionary (Vol 1) (Type: ReferenceBook)

*** SYSTEM CRASHED (LSP VIOLATION) ***
Caught Expected Error: LSP VIOLATION: Reference book 'Oxford English Dictionary (Vol 1)' cannot be borrowed! Reading room use only.
Reason: ReferenceBook inherited Book's interface but broke its behavioral contract.

==================================================================
Summary of Detected Anti-Patterns:
 - SRP (Member 1): LibraryManager does DB, email, formatting, business.
 - OCP (Member 2): calculateFine() has rigid switch-case statement.
 - LSP (Member 3): ReferenceBook throws UnsupportedOperationException.
 - ISP (Member 4): Physical book forced to implement streamAudioFile().
 - DIP (Member 5): LibraryManager instantiates MySQL & SMTP directly.
==================================================================
```

