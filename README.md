# Library Management System - SOLID Refactoring & Design Patterns

An Object-Oriented Design Patterns and Principles project developed for **ICT 431-2** (University of Uva Wellassa).

## Overview
This project models a Library Management System focusing on four core entities:
- **Book**: Physical and reference library catalog items.
- **Member**: Undergraduate Student, Faculty Member, and Guest patrons.
- **Loan**: Borrowing lifecycle, overdue tracking, and fine calculations.
- **Notification**: Loan and overdue alert delivery via multi-channel dispatch (Email, SMS).

## Architecture & SOLID Principles Demonstration
The project is organized in two progressive stages:
1. **Pre-SOLID Baseline (`presolid`):** Initial monolithic architecture intentionally illustrating flaws and violations across all 5 SOLID principles.
2. **SOLID Refactored System (`solid`):** Step-by-step refactoring attributed across 5 team members:
   - **Member 1 (SRP):** God-class decomposition into single-purpose domain services and validators.
   - **Member 2 (OCP):** Pluggable fine calculation and loan duration strategies without editing core services.
   - **Member 3 (LSP):** Robust subtyping for borrowable vs. reference items avoiding unexpected runtime errors.
   - **Member 4 (ISP):** Splitting fat interfaces into fine-grained client contracts (`IBorrowable`, `ISearchable`, etc.).
   - **Member 5 (DIP):** Decoupling high-level business services from data persistence and notifications using Dependency Injection.

## Project Structure
- `docs/`: System design documentation, initial UML class diagrams, and refactoring notes.
- `database/`: DataGrip / SQL schema scripts and seed data (`library_system_datagrip.sql`).
- `PROJECT_BACKLOG.md`: Complete Agile ticket backlog and sprint roadmap.
