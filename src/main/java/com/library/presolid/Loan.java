package com.library.presolid;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Loan Entity
 * Tracks the borrowing transaction and fine calculation.
 */
public class Loan {
    private String loanId;
    private Book book;
    private Member member;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;
    private String status; // "ACTIVE", "RETURNED", "OVERDUE"

    public Loan(String loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.book = book;
        this.member = member;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fineAmount = 0.0;
        this.status = "ACTIVE";
    }

    public String getLoanId() { return loanId; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getOverdueDays(LocalDate currentDate) {
        LocalDate checkDate = (returnDate != null) ? returnDate : currentDate;
        if (checkDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, checkDate);
        }
        return 0;
    }
}
