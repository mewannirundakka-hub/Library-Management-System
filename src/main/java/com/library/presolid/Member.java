package com.library.presolid;

/**
 * Member Entity
 * Represents patrons with different types (STUDENT, FACULTY, GUEST).
 */
public class Member {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String memberType; // "STUDENT", "FACULTY", "GUEST"
    private int maxBorrowLimit;
    private double totalFinesPending;

    public Member(String id, String name, String email, String phone, String memberType, int maxBorrowLimit) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.memberType = memberType;
        this.maxBorrowLimit = maxBorrowLimit;
        this.totalFinesPending = 0.0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMemberType() { return memberType; }
    public int getMaxBorrowLimit() { return maxBorrowLimit; }
    public double getTotalFinesPending() { return totalFinesPending; }

    public void addFine(double amount) {
        this.totalFinesPending += amount;
    }

    public void payFine(double amount) {
        this.totalFinesPending = Math.max(0, this.totalFinesPending - amount);
    }
}
