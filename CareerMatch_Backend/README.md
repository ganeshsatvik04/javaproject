# CareerMatch Backend (Java)

## Requirements
- JDK 17+
- Maven
- Internet on first Maven build (to download SQLite JDBC)

## Run
Open this folder in IntelliJ IDEA / Eclipse as a Maven project.
Run `com.careermatch.Main`. The SQLite database `careermatch.db` is created in the project working directory.

## Packages
- `model`: User, JobSeeker, Admin, Job and JobMatch classes
- `dao`: JDBC database access and CRUD operations
- `service`: matching, skill gap, learning, readiness, comparison and admin logic
- `util`: database connection and table creation

## Important
This is the backend foundation. Connect the service methods to your Swing screens.
Salary ranges and job data must be entered by the admin and should be displayed as sample estimates unless verified.
For a real deployment, replace plain-text password storage with a strong password-hashing library (such as BCrypt), add input validation, and enforce authorization for admin actions.
