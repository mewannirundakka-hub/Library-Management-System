package com.library.presolid;

/**
 * [DIP VIOLATION - ASSIGNED TO MEMBER 5]
 * Concrete low-level database driver.
 * High-level business logic directly instantiates this with 'new' keyword.
 */
public class MySQLDatabaseConnection {
    private String connectionUrl = "jdbc:mysql://localhost:3306/library_db";

    public void executeInsert(String sql) {
        System.out.println("[SQL EXECUTE - Direct MySQL Driver] " + sql);
    }

    public void executeUpdate(String sql) {
        System.out.println("[SQL UPDATE - Direct MySQL Driver] " + sql);
    }
}
