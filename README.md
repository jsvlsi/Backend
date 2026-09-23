# EcoAccess Console Backend

Plain Java 17, JDBC and PostgreSQL implementation of `BACKEND-REQUIREMENTS.md`.
No Maven, Gradle, framework, HTTP server or frontend integration is used.

For a file-by-file explanation and complete execution flows, read [CODE-WALKTHROUGH.md](CODE-WALKTHROUGH.md).
For the PostgreSQL table relationships, read [ER-DIAGRAM.md](ER-DIAGRAM.md).
The presentation-ready PNG version is [EcoAccess-ER-Diagram.png](docs/EcoAccess-ER-Diagram.png).

## Decisions and boundaries

This implements the requirements document as the source of truth. It uses the runtime demo credentials (not the old frontend README): passenger `9876543210` / `Test@123`, passenger `9988776655` / `Test@123`, staff `STF1001` / `Test@123`, staff `STF1002` / `Test@123`, and admin `admin@ecoaccess.com` / `Test@123`.

The deliberately retained prototype behaviour is: demo OTP `123456`, identity-only passenger/staff reset, booking station may differ from the validated journey station, unassigned work can be claimed by any staff member, instant `Issued` redemptions, no booking cancellation, and Pune has no seeded wheelchair/vehicle inventory.

The following specification recommendations are implemented: SHA-256 password hashes, unique database IDs, JDBC transactions for booking/coupon, rewards redemption and waste acceptance, guarded workflow transitions, coupon expiry on access, and journey station/platform copied to waste records. Staff deletion is refused when active work is assigned.

`photo_path` is a local path or the `PHOTO_CAPTURED` console marker; no image is uploaded. UPI/card payment is validation-only and no card data is stored.

## Database setup

1. Install PostgreSQL and create a database called `ecoaccess` (or choose another name).
2. Run [01_schema.sql](sql/01_schema.sql), then [02_seed.sql](sql/02_seed.sql), in that order, against that database. For example in psql:

```text
psql -U postgres -d ecoaccess -f sql/01_schema.sql
psql -U postgres -d ecoaccess -f sql/02_seed.sql
```

3. Set the URL, username and password in [DatabaseConfig.java](src/main/java/com/ecoaccess/util/DatabaseConfig.java), or provide `ECOACCESS_DB_URL`, `ECOACCESS_DB_USER`, and `ECOACCESS_DB_PASSWORD` environment variables when running. Environment variables take priority.

## Required jars and Eclipse

Download the PostgreSQL JDBC driver (`postgresql-42.x.x.jar`) from the PostgreSQL JDBC project and put it in `lib/`. It is required to run the application. Download the JUnit Platform Console Standalone JAR (`junit-platform-console-standalone-1.10.x.jar`) and put it in `lib/`; it is required only to compile/run tests.

In Eclipse: **File → New → Java Project → Create project from existing source**, select this folder, use Java 17, and add both JARs via **Project → Properties → Java Build Path → Libraries → Add JARs**. Mark `src/main/java` as a source folder and `src/test/java` as a test source folder. Do not create a Maven project.

If Eclipse does not show `ConsoleApplication.java`, use **File → Import → General → Existing Projects into Workspace**, select exactly the `EcoAccess-Backend` folder (not its parent `TCS` folder), then click Finish. The included `.project` and `.classpath` files mark `src/main/java` and `src/test/java` as source folders. In Package Explorer, open `src/main/java → com.ecoaccess → ConsoleApplication.java`. If you had already imported the wrong folder, remove that Eclipse project with **Delete project contents on disk unchecked**, then import `EcoAccess-Backend` again.

## Run

Run `com.ecoaccess.ConsoleApplication` as a Java application after the database is seeded and the PostgreSQL driver is on the build path. The opening menu offers register, login and password reset. Role menus cover the operations listed in the specification. Every list accepts a search text and page number (five rows per page).

For command-line compilation on Windows:

```text
javac -cp "lib/*" -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
java -cp "out;lib/*" com.ecoaccess.ConsoleApplication
```

## Tests

The included JUnit tests are unit tests for validation and pricing/reward calculations; they need no database and do not alter data. In Eclipse, right-click `src/test/java` and choose **Run As → JUnit Test**. Database integration should use a separate PostgreSQL database populated with the same schema/seed; never point destructive integration tests at a production database.

## Design

`model` holds entities/enums, `dao` owns prepared-statement persistence, `service` owns rules/transactions, `console` contains menus only, `util` contains input/validation/config helpers, and `exception` contains user-safe application exceptions. Raw SQL errors are wrapped as `DatabaseException` and the console reports their safe message rather than a stack trace.

### Key rules

- GST is `round(base * 0.05)`; wheelchair is ₹50/passenger and inter vehicle ₹70/passenger; porter is bags × selected ₹50/60/70/80 rate.
- Active inventory use excludes `Completed` and `Rejected` bookings.
- Waste is throttled for four hours even after rejection; acceptance credits exactly 20 points.
- Redeeming at least 100 points converts the entire wallet to a 24-hour coupon worth `round(points * .5)`.
- Staff must be Available to accept/progress; workflow is Accepted → Reached Passenger → Service Started → Completed.

## Clarifications recorded

The requirements contain the listed frontend conflicts. The parity choices above resolve them without inventing a payment provider, real OTP/PNR service, ticket entity, cancellation, email uniqueness, staff language, or admin provisioning. The documented frontend’s `Pending` redemption approval path remains supported by the database/service but passenger redemption creates `Issued`, as required.
