package com.library.presolid;

import java.time.LocalDate;

/**
 * PreSolidMain
 * Executable demonstrator for the initial un-refactored Library System.
 * Required for Assignment Section 2.3 (Flaw Identification & Pre-SOLID Demonstration).
 */
public class PreSolidMain {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   ICT 431-2: LIBRARY MANAGEMENT SYSTEM (PRE-SOLID BASELINE)      ");
        System.out.println("==================================================================\n");

        LibraryManager manager = new LibraryManager();

        // 1. Setup sample entities
        Book cleanCode = new Book("B001", "978-0132350884", "Clean Code", "Robert C. Martin", "Software Engineering", "Shelf-A1");
        Book dsa = new Book("B002", "978-0262033848", "Introduction to Algorithms", "CLRS", "Computer Science", "Shelf-B2");
        ReferenceBook oxfordDict = new ReferenceBook("B003", "978-0199571123", "Oxford English Dictionary (Vol 1)", "Oxford", "Reference", "Ref-Desk-1");

        Member student = new Member("M001", "Kasun Perera", "kasun@std.uwu.ac.lk", "+94712345678", "STUDENT", 3);
        Member faculty = new Member("M002", "Dr. Nimal Silva", "nimal@uwu.ac.lk", "+94771234567", "FACULTY", 10);
        Member guest = new Member("M003", "Guest Visitor", "guest@mail.com", "+94759998888", "GUEST", 1);

        manager.addBook(cleanCode);
        manager.addBook(dsa);
        manager.addBook(oxfordDict);

        manager.registerMember(student);
        manager.registerMember(faculty);
        manager.registerMember(guest);

        System.out.println("\n>>> [DEMO 1: HAPPY PATH LOAN & RETURN WITH FINES] <<<");
        Loan loan1 = manager.issueLoan("B001", "M001");
        if (loan1 != null) {
            System.out.println("Loan created successfully: " + loan1.getLoanId());
            // Simulate returning the book 5 days late
            LocalDate returnDate = loan1.getDueDate().plusDays(5);
            System.out.println("Simulating book return on: " + returnDate + " (5 days overdue)...");
            manager.returnBook(loan1.getLoanId(), returnDate);
        }

        System.out.println("\n>>> [DEMO 2: ISP FLAW DEMONSTRATION - Member 4] <<<");
        System.out.println("Physical book forced to implement audio streaming by ILibraryOperations:");
        cleanCode.streamAudioFile();

        System.out.println("\n>>> [DEMO 3: LSP FLAW DEMONSTRATION - Member 3] <<<");
        System.out.println("Attempting to process ReferenceBook through standard loan pipeline...");
        try {
            // Polymorphic usage: ReferenceBook treated as Book
            Book bookRef = oxfordDict;
            System.out.println("Loaning book: " + bookRef.getTitle() + " (Type: " + bookRef.getClass().getSimpleName() + ")");
            manager.issueLoan(bookRef.getId(), "M002");
        } catch (UnsupportedOperationException e) {
            System.out.println("\n*** SYSTEM CRASHED (LSP VIOLATION) ***");
            System.out.println("Caught Expected Error: " + e.getMessage());
            System.out.println("Reason: ReferenceBook inherited Book's interface but broke its behavioral contract.\n");
        }

        System.out.println("==================================================================");
        System.out.println("Summary of Detected Anti-Patterns:");
        System.out.println(" - SRP (Member 1): LibraryManager does DB, email, formatting, business.");
        System.out.println(" - OCP (Member 2): calculateFine() has rigid switch-case statement.");
        System.out.println(" - LSP (Member 3): ReferenceBook throws UnsupportedOperationException.");
        System.out.println(" - ISP (Member 4): Physical book forced to implement streamAudioFile().");
        System.out.println(" - DIP (Member 5): LibraryManager instantiates MySQL & SMTP directly.");
        System.out.println("==================================================================");
    }
}
