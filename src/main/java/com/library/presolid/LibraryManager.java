package com.library.presolid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * [GOD CLASS ANTI-PATTERN]
 * Demonstrates multiple SOLID violations:
 * - SRP VIOLATION (Member 1): Combines domain logic, SQL persistence, email dispatch, fine logic, and reporting.
 * - OCP VIOLATION (Member 2): Hardcoded cascading switch/if-else for fine calculation and loan durations.
 * - DIP VIOLATION (Member 5): Directly instantiates concrete MySQLDatabaseConnection and SmtpEmailSender.
 */
public class LibraryManager {
    // In-memory collections mixed with persistence responsibilities
    private List<Book> books = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();

    // [DIP VIOLATION - Member 5]: Direct instantiation of concrete low-level infrastructure
    private MySQLDatabaseConnection db = new MySQLDatabaseConnection();
    private SmtpEmailSender emailSender = new SmtpEmailSender();

    // -------------------------------------------------------------
    // Inventory & Member Management (SRP Violation: Mixed concerns)
    // -------------------------------------------------------------
    public void addBook(Book book) {
        books.add(book);
        db.executeInsert("INSERT INTO books VALUES ('" + book.getId() + "', '" + book.getTitle() + "')");
    }

    public void registerMember(Member member) {
        members.add(member);
        db.executeInsert("INSERT INTO members VALUES ('" + member.getId() + "', '" + member.getName() + "')");
    }

    public Book findBookById(String id) {
        for (Book b : books) {
            if (b.getId().equalsIgnoreCase(id)) return b;
        }
        return null;
    }

    public Member findMemberById(String id) {
        for (Member m : members) {
            if (m.getId().equalsIgnoreCase(id)) return m;
        }
        return null;
    }

    // -------------------------------------------------------------
    // Core Transaction: Issue Loan
    // -------------------------------------------------------------
    public Loan issueLoan(String bookId, String memberId) {
        Book book = findBookById(bookId);
        Member member = findMemberById(memberId);

        if (book == null || member == null) {
            System.out.println("[ERROR] Book or Member not found!");
            return null;
        }

        if (book.isIssued()) {
            System.out.println("[ERROR] Book is already checked out!");
            return null;
        }

        // Check member borrow limit
        int currentLoans = 0;
        for (Loan l : loans) {
            if (l.getMember().getId().equals(memberId) && "ACTIVE".equals(l.getStatus())) {
                currentLoans++;
            }
        }

        if (currentLoans >= member.getMaxBorrowLimit()) {
            System.out.println("[ERROR] Member " + member.getName() + " reached maximum borrowing limit of " + member.getMaxBorrowLimit());
            return null;
        }

        // [OCP VIOLATION - Member 2]: Loan duration hardcoded by member type
        int loanDays = 14;
        if ("FACULTY".equalsIgnoreCase(member.getMemberType())) {
            loanDays = 30; // Faculty gets 30 days
        } else if ("STUDENT".equalsIgnoreCase(member.getMemberType())) {
            loanDays = 14; // Student gets 14 days
        } else if ("GUEST".equalsIgnoreCase(member.getMemberType())) {
            loanDays = 7;  // Guest gets only 7 days
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(loanDays);

        // [LSP Trigger]: Calling borrowItem() on Book.
        // If book is a ReferenceBook, this throws UnsupportedOperationException!
        book.borrowItem(memberId);

        String loanId = "LOAN-" + (loans.size() + 1001);
        Loan loan = new Loan(loanId, book, member, issueDate, dueDate);
        loans.add(loan);

        // [SRP & DIP Violation]: Direct SQL generation & execution
        db.executeInsert("INSERT INTO loans (loan_id, book_id, member_id, issue_date, due_date) VALUES ('"
                + loanId + "', '" + bookId + "', '" + memberId + "', '" + issueDate + "', '" + dueDate + "')");

        // [SRP & DIP Violation]: Direct email dispatch
        emailSender.sendEmail(member.getEmail(), "Loan Confirmation: " + book.getTitle(),
                "Dear " + member.getName() + ", you have borrowed '" + book.getTitle() + "'. Due date: " + dueDate);

        return loan;
    }

    // -------------------------------------------------------------
    // Core Transaction: Return Book & Calculate Fines
    // -------------------------------------------------------------
    public void returnBook(String loanId, LocalDate actualReturnDate) {
        Loan targetLoan = null;
        for (Loan l : loans) {
            if (l.getLoanId().equalsIgnoreCase(loanId)) {
                targetLoan = l;
                break;
            }
        }

        if (targetLoan == null) {
            System.out.println("[ERROR] Loan record not found!");
            return;
        }

        targetLoan.setReturnDate(actualReturnDate);
        targetLoan.getBook().returnItem();

        // Calculate fine
        double fine = calculateFine(targetLoan, actualReturnDate);
        targetLoan.setFineAmount(fine);
        targetLoan.setStatus("RETURNED");

        if (fine > 0) {
            targetLoan.getMember().addFine(fine);
            // [SRP & DIP]: Direct notification on fine
            emailSender.sendEmail(targetLoan.getMember().getEmail(), "Overdue Fine Incurred",
                    "You returned '" + targetLoan.getBook().getTitle() + "' overdue by " 
                    + targetLoan.getOverdueDays(actualReturnDate) + " days. Fine: LKR " + fine);
        }

        // [SRP & DIP]: Direct DB update
        db.executeUpdate("UPDATE loans SET return_date = '" + actualReturnDate + "', fine_amount = " 
                + fine + ", status = 'RETURNED' WHERE loan_id = '" + loanId + "'");
        
        // [SRP Violation]: Receipt formatting inside business transaction
        printReturnReceipt(targetLoan);
    }

    /**
     * [OCP VIOLATION - ASSIGNED TO MEMBER 2]
     * Hardcoded fine calculation logic using switch/case and if-else ladders.
     * To add a new member tier (e.g., RESEARCHER or SENIOR_CITIZEN),
     * a developer MUST modify this existing method!
     */
    public double calculateFine(Loan loan, LocalDate returnDate) {
        long overdueDays = loan.getOverdueDays(returnDate);
        if (overdueDays <= 0) {
            return 0.0;
        }

        String type = loan.getMember().getMemberType();
        double fine = 0.0;

        switch (type.toUpperCase()) {
            case "STUDENT":
                // Students pay 20 LKR per day
                fine = overdueDays * 20.0;
                break;
            case "FACULTY":
                // Faculty members pay a discounted rate of 5 LKR per day
                fine = overdueDays * 5.0;
                break;
            case "GUEST":
                // Guests pay a steep penalty of 50 LKR per day
                fine = overdueDays * 50.0;
                break;
            default:
                // Standard default rate
                fine = overdueDays * 15.0;
                break;
        }

        return fine;
    }

    // [SRP VIOLATION - Member 1]: Formatter / Printer mixed inside Manager
    private void printReturnReceipt(Loan loan) {
        System.out.println("==========================================");
        System.out.println("         LIBRARY RETURN RECEIPT           ");
        System.out.println("==========================================");
        System.out.println("Loan ID     : " + loan.getLoanId());
        System.out.println("Book Title  : " + loan.getBook().getTitle());
        System.out.println("Member Name : " + loan.getMember().getName() + " (" + loan.getMember().getMemberType() + ")");
        System.out.println("Due Date    : " + loan.getDueDate());
        System.out.println("Return Date : " + loan.getReturnDate());
        System.out.println("Fine Amount : LKR " + loan.getFineAmount());
        System.out.println("==========================================\n");
    }

    // [SRP VIOLATION - Member 1]: Report generator mixed inside Manager
    public String generateOverdueAuditReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- DAILY OVERDUE AUDIT REPORT ---\n");
        for (Loan l : loans) {
            if ("ACTIVE".equals(l.getStatus()) && LocalDate.now().isAfter(l.getDueDate())) {
                sb.append("Loan: ").append(l.getLoanId())
                  .append(" | Member: ").append(l.getMember().getName())
                  .append(" | Book: ").append(l.getBook().getTitle())
                  .append(" | Due: ").append(l.getDueDate()).append("\n");
            }
        }
        return sb.toString();
    }
}
