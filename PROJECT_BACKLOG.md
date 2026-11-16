# Library Management System (ICT 431-2) - Agile Product Backlog & Ticket Document

## 1. Project Overview & Requirements Mapping

- **Institution:** Uva Wellassa University — Faculty of Technological Studies (Dept. of ICT)
- **Course Module:** ICT 431-2 Object Oriented Design Patterns and Principals
- **Domain:** Library Management System (Entities: `Book`, `Member`, `Loan`, `Notification`)
- **Key Pedagogical Requirement:**
  1. Build an **Initial Problematic Design & Java Codebase** deliberately violating all 5 SOLID principles.
  2. Perform a **Step-by-Step Refactoring** cleanly partitioned across 5 team members (one SOLID principle per member).
  3. Deliver a **Final SOLID-Compliant Architecture** with a runnable Java application and **Basic UI** (JavaFX / Swing / Web UI).
  4. Complete the formal **Assignment Report** and prepare for the **Viva / Presentation Defense**.

---

## 2. Sprint & Member Allocation Matrix

| Member | Assigned SOLID Principle | Core Technical Responsibility | Primary Backlog Focus |
| :--- | :--- | :--- | :--- |
| **Member 1** | **SRP** (Single Responsibility) | Decompose God-classes / monolithic services into single-purpose components | Service segregation, persistence decoupling, validator extraction |
| **Member 2** | **OCP** (Open/Closed) | Implement strategy/extension points for fine calculation and loan rules | Strategy patterns for fines, loan duration policies without core edits |
| **Member 3** | **LSP** (Liskov Substitution) | Establish sound class hierarchies without runtime exceptions or broken contracts | Member & Book inheritance refactoring (e.g., ReferenceBook, GuestMember) |
| **Member 4** | **ISP** (Interface Segregation) | Split fat interfaces into fine-grained client-specific contracts | `IBorrowable`, `ISearchable`, `IReservable`, `INotifiable` interfaces |
| **Member 5** | **DIP** (Dependency Inversion) | Decouple business logic from repositories and notification delivery via DI | Abstract repositories, `INotificationService`, Dependency Injection container |
| **All Team** | **UI & Deliverables** | Build Basic UI, integrate flow, draft report, prepare Viva defense | JavaFX/Swing UI, Main runner, Report Sections 1–7, Presentation |

---

## 3. Product Backlog Tickets

### Epic 1: Domain Analysis & Initial "Anti-Pattern" System (Pre-SOLID)
*Goal: Provide the baseline problematic system required by Deliverables 1.1, 1.2, 2.1, 2.2, and 2.3.*

#### Ticket `LMS-EP1-01`: Domain Modeling & Monolithic Class Design
- **Type:** Story / Design
- **Assignee:** Full Team (Coordinated by Member 1)
- **Priority:** High | **Story Points:** 5 | **Sprint:** 1
- **Description:** Design an initial monolithic architecture for the Library Management System (`Book`, `Member`, `Loan`, `Notification`) intentionally containing classic architectural flaws.
- **Specific Anti-Patterns to Include:**
  - `LibraryManager` as a God Class (handles UI, validation, business logic, DB SQL strings, and email sending in one file).
  - Tightly coupled `new EmailSender()` and `new MySQLDatabase()` instantiations.
  - Giant `switch/case` or `if/else` ladders for member type fine calculations.
  - Fat interface `ILibraryItem` with `borrow()`, `reserve()`, `renew()`, `printBarcode()`, `streamAudio()`.
  - Fragile hierarchy where `ReferenceBook` extends `Book` but throws `UnsupportedOperationException` on `borrow()`.
- **Deliverables:**
  - Initial UML Class Diagram (`Section 2.2`).
  - Problem Statement documentation (`Section 2.1`).
- **Acceptance Criteria:**
  - [ ] UML Diagram visually illustrates all 5 SOLID violations clearly.
  - [ ] Every violation is documented with an explicit pointer to which member will refactor it.

#### Ticket `LMS-EP1-02`: Initial Java Codebase Implementation (Demonstrating Flaws)
- **Type:** Development
- **Assignee:** Full Team
- **Priority:** High | **Story Points:** 8 | **Sprint:** 1
- **Description:** Implement runnable Java code for the initial problematic design in a dedicated package (e.g., `com.library.presolid`).
- **Tasks:**
  - Implement `Book`, `Member`, `Loan`, and `Notification` models with mixed responsibilities.
  - Implement `LibraryManager.java` containing hardcoded notification calls, monolithic loan processing, and hardcoded fine algorithms.
  - Create `PreSolidMain.java` demonstrating that while the code works for basic cases, adding a new member type or non-borrowable book causes runtime exceptions or requires editing core logic.
- **Deliverables:**
  - Source code under `src/main/java/com/library/presolid/` (`Section 2.3`).
  - Code commentary highlighting exact lines where SOLID principles are violated.
- **Acceptance Criteria:**
  - [ ] Code compiles and executes demonstration scripts.
  - [ ] Flaw identification table completed for Report Section 2.3.

---

### Epic 2: Step-by-Step SOLID Refactoring

#### Ticket `LMS-EP2-01`: Refactoring for Single Responsibility Principle (SRP)
- **Assigned Lead:** **Member 1**
- **Type:** Refactoring / Core Logic
- **Priority:** High | **Story Points:** 8 | **Sprint:** 2
- **Problem in Baseline:** `LibraryManager` handled user input, loan processing, member validation, report generation, and database simulation.
- **Target Architecture:**
  - Extract `LoanService` (manages loan state transitions).
  - Extract `MemberValidator` (validates membership status and borrowing eligibility).
  - Extract `BookInventoryService` (tracks book stock and availability).
  - Extract `ReceiptPrinter` / `ReportGenerator` (handles data presentation).
- **Tasks:**
  - Refactor monolithic methods from `LibraryManager` into dedicated classes with single reasons to change.
  - Produce intermediate UML Class Diagram before and after SRP refactoring.
  - Write test cases verifying independent unit behavior.
- **Deliverables:**
  - Code in `com.library.solid.srp` or core domain.
  - Report draft for `Section 3.1: Single Responsibility Principle (SRP)`.
- **Acceptance Criteria:**
  - [ ] No single class performs both business calculation, persistence, and formatting.
  - [ ] Intermediate class diagram and before/after explanation attributed to Member 1.

#### Ticket `LMS-EP2-02`: Refactoring for Open/Closed Principle (OCP)
- **Assigned Lead:** **Member 2**
- **Type:** Refactoring / Architecture
- **Priority:** High | **Story Points:** 8 | **Sprint:** 2
- **Problem in Baseline:** Hardcoded `switch-case` in loan duration and fine calculation (e.g., `if (type == STUDENT) return days * 10; else if (type == FACULTY) ...`). Adding a new tier (`Guest` or `Researcher`) required modifying core `Loan` class.
- **Target Architecture:**
  - Create strategy interface `FineCalculationStrategy` with implementations:
    - `StandardFineStrategy`
    - `StudentDiscountFineStrategy`
    - `FacultyExemptFineStrategy`
  - Create extensible loan duration policy `LoanPolicy` allowing pluggable borrowing durations.
- **Tasks:**
  - Replace conditional logic in `LoanService` with strategy injection.
  - Add a new policy (e.g., `WeekendFinePolicy` or `LateReturnPenaltyPolicy`) demonstrating extension without modifying existing code.
  - Produce intermediate UML Class Diagram for OCP.
- **Deliverables:**
  - Code under `com.library.solid.ocp.strategy`.
  - Report draft for `Section 3.2: Open/Closed Principle (OCP)`.
- **Acceptance Criteria:**
  - [ ] New fine calculation rule can be registered without altering a single line of `LoanService.java`.
  - [ ] Intermediate class diagram and rationale completed for Member 2.

#### Ticket `LMS-EP2-03`: Refactoring for Liskov Substitution Principle (LSP)
- **Assigned Lead:** **Member 3**
- **Type:** Refactoring / Hierarchy Design
- **Priority:** High | **Story Points:** 8 | **Sprint:** 2
- **Problem in Baseline:** `ReferenceBook` extended `Book`, but threw `UnsupportedOperationException("Reference books cannot be loaned")` inside `borrow()`. Similarly, `SuspendedMember` broke base `Member` assumptions.
- **Target Architecture:**
  - Redesign class hierarchy:
    - `Item` (Base: ID, Title, Author, Location).
    - `BorrowableItem` (implements borrowing capabilities) vs `ReferenceItem` (read-only in library).
    - Abstract `Member` base class with clear contracts; separate active borrower capabilities so subtypes can be substituted without runtime errors.
- **Tasks:**
  - Remove offensive throwing overrides that violate behavioral subtyping.
  - Implement polymorphic loan execution where client code operates safely on base types.
  - Produce intermediate UML Class Diagram for LSP.
- **Deliverables:**
  - Code under `com.library.solid.lsp`.
  - Report draft for `Section 3.3: Liskov Substitution Principle (LSP)`.
- **Acceptance Criteria:**
  - [ ] A collection of `Item` or `Member` objects can be processed in a loop without catching unexpected exceptions or using `instanceof` checks.
  - [ ] Intermediate class diagram and defense points documented for Member 3.

#### Ticket `LMS-EP2-04`: Refactoring for Interface Segregation Principle (ISP)
- **Assigned Lead:** **Member 4**
- **Type:** Refactoring / Interface Design
- **Priority:** High | **Story Points:** 8 | **Sprint:** 2
- **Problem in Baseline:** Monolithic interface `ILibraryOperations` forced classes to implement unused methods (e.g., `DigitalMedia` forced to implement `renewPhysicalCopy()` and `cleanCover()`).
- **Target Architecture:**
  - Segregate into cohesive, role-based interfaces:
    - `IBorrowable` (`borrowItem()`, `returnItem()`, `renewLoan()`)
    - `ISearchable` (`searchByTitle()`, `searchByAuthor()`, `searchByCategory()`)
    - `IReservable` (`reserve()`, `cancelReservation()`)
    - `IDigitalAccess` (`download()`, `stream()`)
- **Tasks:**
  - Split fat interfaces into fine-grained client contracts.
  - Refactor concrete classes to implement only the interfaces they genuinely use.
  - Produce intermediate UML Class Diagram for ISP.
- **Deliverables:**
  - Code under `com.library.solid.isp`.
  - Report draft for `Section 3.4: Interface Segregation Principle (ISP)`.
- **Acceptance Criteria:**
  - [ ] No dummy or stubbed `throw new NotSupportedException()` in any implementing class.
  - [ ] Intermediate class diagram showing clean interface segregation for Member 4.

#### Ticket `LMS-EP2-05`: Refactoring for Dependency Inversion Principle (DIP)
- **Assigned Lead:** **Member 5**
- **Type:** Refactoring / Inversion of Control
- **Priority:** High | **Story Points:** 8 | **Sprint:** 2
- **Problem in Baseline:** High-level `LoanManager` directly depended on concrete low-level implementations (`new EmailNotification()`, `new MySQLDatabaseConnection()`).
- **Target Architecture:**
  - Create abstractions:
    - `IBookRepository`, `IMemberRepository`, `ILoanRepository`.
    - `INotificationChannel` / `INotificationService`.
  - Implement concrete adapters: `InMemoryBookRepository`, `DatabaseBookRepository`, `EmailNotificationChannel`, `SmsNotificationChannel`.
  - Implement Constructor Dependency Injection in `LoanService`.
  - Provide a lightweight Service Locator / DI Registry (`AppContainer`).
- **Tasks:**
  - Decouple all high-level business logic from low-level infrastructure.
  - Allow mock/in-memory implementations to be swapped for persistence and notifications without code changes in domain services.
  - Produce intermediate UML Class Diagram for DIP.
- **Deliverables:**
  - Code under `com.library.solid.dip`.
  - Report draft for `Section 3.5: Dependency Inversion Principle (DIP)`.
- **Acceptance Criteria:**
  - [ ] High-level services only accept interface types in their constructors.
  - [ ] Intermediate class diagram and DIP defense writeup completed for Member 5.

---

### Epic 3: User Interface & Complete System Integration
*Goal: Provide a clean, user-friendly UI to demonstrate all features and SOLID flows during the viva presentation.*

#### Ticket `LMS-EP3-01`: UI Architecture & Technology Setup
- **Type:** Frontend / Architecture
- **Assignee:** Full Team (UI Lead: Member 1 & 4)
- **Priority:** High | **Story Points:** 5 | **Sprint:** 3
- **Description:** Set up a clean, modern GUI using **JavaFX** (or Swing / Lightweight Spring Boot Web UI).
- **Key Views:**
  1. **Dashboard:** Summary of available books, registered members, active loans, and overdue alerts.
  2. **Book Catalog View:** Search, view availability, distinguish regular vs reference books.
  3. **Member Management View:** Register, view member types (Student, Faculty, Guest).
  4. **Loan & Return View:** Issue loans, return books, trigger fine calculation dynamically.
  5. **Notification Console / Log View:** Live display of dispatched notifications (Email, SMS) showing DIP in action.
- **Acceptance Criteria:**
  - [ ] UI is fully decoupled from business logic (UI calls service interfaces only).
  - [ ] Easy setup and runnable via standard `mvn compile exec:java` or `gradle run`.

#### Ticket `LMS-EP3-02`: End-to-End System Demonstration Flow (`Main.java`)
- **Type:** Integration & Testing
- **Assignee:** Full Team
- **Priority:** High | **Story Points:** 5 | **Sprint:** 3
- **Description:** Build both a headless runnable `Main.java` (for automated console demonstration and grading verification) and the UI launch runner.
- **Demonstration Scenarios:**
  - Scenario A: Register book and member -> issue loan -> generate notification.
  - Scenario B: Return overdue book -> OCP fine calculation strategy executes and calculates accurate fine.
  - Scenario C: Attempt to loan reference item -> LSP-safe handling without crashes.
  - Scenario D: Switch notification provider from Email to SMS via DIP configuration without modifying business logic.
- **Deliverables:**
  - `src/main/java/com/library/Main.java` (`Section 4.3`).
  - Terminal output logs and UI screenshots.
- **Acceptance Criteria:**
  - [ ] Runs with 0 errors on any standard Java 17+ environment.
  - [ ] Demonstrates all four entities and all 5 refactored principles.

---

### Epic 4: Final Documentation, Report & Viva Defense
*Goal: Satisfy all report structure guidelines and viva presentation requirements.*

#### Ticket `LMS-EP4-01`: Final Unified Class Diagram & Architecture Spec
- **Type:** Documentation / Modeling
- **Assignee:** Full Team (Lead: Member 5)
- **Priority:** High | **Story Points:** 5 | **Sprint:** 3
- **Deliverables:**
  - Final SOLID-Compliant Class Diagram (`Section 4.1`).
  - Architecture overview documentation showing packages, interfaces, and design patterns used.
- **Acceptance Criteria:**
  - [ ] Diagram is high-resolution, standard UML, cleanly showing all interfaces, inheritance, and dependencies.

#### Ticket `LMS-EP4-02`: Assignment Report Compilation (Sections 1 through 7)
- **Type:** Report / Academic
- **Assignee:** Full Team (Assigned sections per brief)
- **Priority:** High | **Story Points:** 8 | **Sprint:** 4
- **Report Outline Compliance:**
  - Cover Page / Title Page with Member Allocation Table & Signatures.
  - 1. Executive Summary / Introduction.
  - 2. Initial System Design (2.1 Domain, 2.2 Initial Diagram, 2.3 Initial Code & Flaws).
  - 3. Step-by-Step Refactoring (3.1 SRP - M1, 3.2 OCP - M2, 3.3 LSP - M3, 3.4 ISP - M4, 3.5 DIP - M5).
  - 4. Final Architecture (4.1 Final Diagram, 4.2 Refactored Code, 4.3 Test Output & UI screenshots).
  - 5. Group Member Self-Evaluation & Presentation Defense Sheet.
  - 6. Conclusion & Lessons Learned.
  - 7. References / Bibliography.
- **Acceptance Criteria:**
  - [ ] Formatted according to Uva Wellassa University guidelines.
  - [ ] Submission-ready PDF package.

#### Ticket `LMS-EP4-03`: Viva / Presentation Defense Preparation
- **Type:** Presentation / Defense
- **Assignee:** Full Team
- **Priority:** High | **Story Points:** 5 | **Sprint:** 4
- **Description:** Prepare slide deck and individual defense flashcards for the viva panel.
- **Tasks:**
  - Create presentation slides (10–15 slides covering Problem, 5 SOLID Refactorings, UI Demo, Conclusion).
  - Prepare 3–5 targeted viva defense Q&A per member (e.g., M1: why is SRP not just one-method-per-class; M2: how does OCP relate to Strategy pattern; M3: explain LSP circle-ellipse or reference book problem; M4: how ISP prevents interface bloat; M5: difference between Dependency Inversion and Dependency Injection).
- **Acceptance Criteria:**
  - [ ] Each member has a rehearseable script defending their specific assigned principle.

---

## 4. Suggested Project Timeline (Target Submission: 25th Dec)

```mermaid
gantt
    title Library Management System Development & Refactoring Timeline
    dateFormat  YYYY-MM-DD
    section Phase 1: Planning & Baseline
    Domain Modeling & Anti-Pattern Design (LMS-EP1-01) :2026-11-18, 5d
    Initial Problematic Codebase (LMS-EP1-02)          :2026-11-23, 6d
    section Phase 2: SOLID Refactoring
    Member 1: SRP Refactoring (LMS-EP2-01)            :2026-11-29, 6d
    Member 2: OCP Refactoring (LMS-EP2-02)            :2026-11-29, 6d
    Member 3: LSP Refactoring (LMS-EP2-03)            :2026-12-05, 6d
    Member 4: ISP Refactoring (LMS-EP2-04)            :2026-12-05, 6d
    Member 5: DIP Refactoring (LMS-EP2-05)            :2026-12-10, 6d
    section Phase 3: UI & System Integration
    UI Implementation & Views (LMS-EP3-01)            :2026-12-11, 7d
    Main Flow & Verification (LMS-EP3-02)             :2026-12-16, 4d
    section Phase 4: Report & Viva Prep
    Final Report Compilation (LMS-EP4-02)             :2026-12-18, 5d
    Viva Defense Prep & Slides (LMS-EP4-03)           :2026-12-21, 3d
    Final Polish & Submission (25th Dec 1:00 PM)      :2026-12-24, 2d
```
