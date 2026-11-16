package com.library.presolid;

/**
 * [DIP VIOLATION - ASSIGNED TO MEMBER 5]
 * Concrete low-level email sender.
 * Tightly coupled to SMTP transport with no abstraction for SMS, Push, etc.
 */
public class SmtpEmailSender {
    private String smtpHost = "smtp.uwu.ac.lk";

    public void sendEmail(String toEmail, String subject, String body) {
        System.out.println("[SMTP EMAIL SENT] To: " + toEmail + " | Subject: " + subject + " | Body: " + body);
    }
}
