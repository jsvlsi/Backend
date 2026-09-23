# EcoAccess Code Walkthrough

This guide explains how to read, present, and extend the project. Read it after the setup instructions in `README.md`.

## 1. The big picture

EcoAccess is a console application with four deliberately separate layers:

```text
Console menu and user input
        ↓
Service: validation, authorization, rules, calculations, transactions
        ↓
DAO: prepared SQL statements and result mapping
        ↓
PostgreSQL tables
```

The menu never writes SQL directly. A DAO never decides whether a user is allowed to perform a business action. This is the main design point to explain in an interview: presentation, business logic, and persistence have separate responsibilities.

## 2. Start here

The application starts in `src/main/java/com/ecoaccess/ConsoleApplication.java`:

```text
main()
  → run()
    → public menu: Register / Login / Reset Password / Exit
      → passengerMenu(), staffMenu(), or adminMenu()
```

`ConsoleApplication` owns the menu loop, gathers input through `ConsoleInput`, calls services, and prints success or safe error messages. It catches `ApplicationException` so a bad menu selection, a validation failure, or a business-rule failure does not terminate the program.

`ConsoleInput` is the only class that reads from `Scanner`. Its `number`, `date`, and `time` methods keep prompting after invalid input. This avoids scattered `Scanner` code and `NumberFormatException` crashes.

## 3. Folder map

| Folder | Responsibility | Important files |
|---|---|---|
| `console` | Read inputs and display menus only | `ConsoleInput.java` |
| project root package | Application startup and role menus | `ConsoleApplication.java` |
| `model` | Immutable business data and fixed-value enums | `Entities.java`, `Enums.java` |
| `dao` | JDBC connections, prepared SQL, `ResultSet` → model mapping | `Database.java`, `AccountDao.java`, `BookingDao.java`, `CatalogDao.java`, `OperationsDao.java` |
| `service` | Business rules, authorization, calculation, transactions | `AuthService.java`, `BookingService.java`, `OperationsService.java` |
| `util` | Reusable helpers | `Validation.java`, `Passwords.java`, `Ids.java`, `DatabaseConfig.java` |
| `exception` | Expected application errors with user-safe messages | `AppExceptions.java` |
| `sql` | PostgreSQL database definition and demo data | `01_schema.sql`, `02_seed.sql` |
| `src/test` | JUnit unit tests | `ValidationTest.java`, `CalculationTest.java` |

## 4. Models and enums

`Entities.java` uses Java records. A record is a short immutable data class: Java automatically creates its constructor, accessors, `equals`, `hashCode`, and `toString`.

For example, `Entities.Booking` represents one row from the `bookings` table. Its accessors look like `booking.id()`, `booking.service()`, and `booking.status()`.

`Enums.java` prevents arbitrary strings from spreading through the code. The important enums are:

| Enum | Values |
|---|---|
| `Role` | `PASSENGER`, `STAFF`, `ADMIN` |
| `ServiceType` | Porter, Wheelchair, Inter Vehicle |
| `BookingStatus` | Booked → Assigned → Accepted → Reached Passenger → Service Started → Completed; also Rejected |
| `StaffStatus` | Available, Unavailable |
| `WasteStatus` | pending, accepted, rejected |
| `CouponStatus` | Active, Used, Expired |
| `ComplaintType` | Feedback, Complaint |

Each enum has a display label. The database stores the same labels defined by the requirements, while Java uses the enum for safe comparisons.

## 5. Database layer

### Connection lifecycle

`Database.connection()` opens a PostgreSQL JDBC connection using `DatabaseConfig`. Every DAO operation uses try-with-resources:

```java
try (Connection connection = Database.connection();
     PreparedStatement statement = connection.prepareStatement(sql)) {
    // bind values, execute, map results
}
```

Therefore connections, statements, and result sets close even when an exception occurs. User input is always supplied with `PreparedStatement` parameters, never concatenated into SQL.

`Database.failure(...)` converts a low-level `SQLException` to a `DatabaseException`. The menu displays a generic safe message rather than PostgreSQL connection details.

### DAO responsibilities

| DAO | Tables / purpose |
|---|---|
| `AccountDao` | `passengers`, `staff`, `admins`; account lookups and profile/account updates |
| `CatalogDao` | `tickets`, `station_locations`, `journey_validations`; PNR lookup and one current journey per passenger |
| `BookingDao` | `bookings`; list, find, insert, availability demand, staff-busy checks, status updates |
| `OperationsDao` | `resources`, `waste_submissions`, `coupons`, `redemptions`, `cases`; CRUD-style operational persistence |

DAOs may map rows, but they do not calculate fares or decide whether a staff member is allowed to change a status.

## 6. Service layer

### `AuthService`

Responsible for registration, login, password reset, and passenger profile persistence.

```text
Register
  validate name, mobile, email, password and OTP
  → check mobile uniqueness
  → hash password
  → insert passenger with zero points

Login
  role determines identity lookup
  → compare SHA-256 hash
  → return Passenger, Staff, or Admin object
```

The demo OTP is intentionally `123456` because that is required for frontend parity. Passwords are stored as SHA-256 hashes rather than plaintext.

### `BookingService`

This is the core booking business service. It handles:

- PNR lookup and journey validation
- journey gate before booking
- wheelchair, vehicle, and porter availability
- fare, GST, discount, and payable calculations
- coupon validation and spending
- matching an available staff member by job-role text
- booking creation and auto-assignment
- passenger/staff/admin booking lists

`quote(...)` is intentionally easy to test. It calculates the base, tax, gross amount, coupon discount, and payable amount without a database call.

### `OperationsService`

This service contains the remaining workflows:

- Staff acceptance, rejection, progress, and availability toggle
- Waste submission and four-hour cooldown
- Waste review and reward-point credit
- Reward redemption and train coupon application
- Feedback / complaint creation and complaint resolution/closure
- Admin staff management and station inventory management
- Pending redemption decision support

## 7. End-to-end flows

### A. Passenger registration and login

```text
ConsoleApplication.register()
  → AuthService.register(...)
    → Validation.name/mobile/email/password(...)
    → AccountDao.passengerByMobile(...)
    → Passwords.hash(...)
    → AccountDao.insertPassenger(...)
  → passengerMenu(passenger)
```

Duplicate mobile numbers are rejected at both levels: the service checks first for a clear message and PostgreSQL also enforces the unique constraint.

### B. Journey validation

```text
Passenger menu
  → BookingService.validateJourney(passenger, pnr)
    → CatalogDao.ticket(pnr)
    → CatalogDao.upsertJourney(...)
```

There is one `journey_validations` row per passenger because `passenger_id` is unique. A new validated PNR replaces the earlier one.

### C. Booking and coupon payment

```text
Passenger booking prompts
  → BookingService.create(passenger, bookingRequest)
    → require valid journey
    → validate date, locations, service-specific count, payment data
    → check live availability
    → validate coupon and calculate quote
    → find first matching available, non-busy staff member
    → BEGIN TRANSACTION
        insert booking
        spend coupon, if supplied
      COMMIT
```

If either insert or coupon spending fails, the transaction rolls back. That prevents a coupon being consumed without a booking, or a booking being created without the selected coupon reduction.

Fare calculation:

```text
Porter base       = bags × chosen rate (₹50 / ₹60 / ₹70 / ₹80)
Wheelchair base   = ₹50 × passenger count
Vehicle base      = ₹70 × passenger count
GST               = round(base × 0.05)
Gross             = base + GST
Discount          = min(coupon remaining, gross)
Payable           = max(0, gross - discount)
```

Availability ignores completed and rejected bookings. Porter availability is the count of available matching porter staff who are not already occupied. Wheelchair and vehicle availability is station stock minus active demand.

### D. Staff fulfillment workflow

```text
Staff accepts a Booked/Assigned booking
  → OperationsService.staffAction(...)
  → status Accepted and staff employee ID saved

Staff advances their own booking
  → OperationsService.advance(...)
  → Accepted → Reached Passenger → Service Started → Completed
```

Acceptance and advancement require staff availability. Rejection is allowed from `Booked` or `Assigned`, even when the staff member is unavailable. The service rejects skipped or invalid transitions.

### E. Waste reward flow

```text
Passenger submits photo path / PHOTO_CAPTURED
  → OperationsService.submitWaste(...)
    → journey gate
    → enforce 4-hour cooldown
    → create pending waste record

Admin accepts it
  → OperationsService.reviewWaste(...)
    → BEGIN TRANSACTION
        mark waste accepted, reward_points = 20
        add 20 points to passenger wallet
      COMMIT
```

Rejection requires a remark and does not grant points. The cooldown still applies after rejection, matching the specified frontend behavior.

### F. Rewards and coupons

```text
Redeem points (minimum 100)
  → OperationsService.redeem(...)
    → coupon value = round(all points × 0.5)
    → BEGIN TRANSACTION
        set passenger points to 0
        create 24-hour Active coupon
        create Issued redemption record
      COMMIT
```

Coupon expiry is updated whenever coupons are listed. A coupon may be partially used for a booking. Applying it to a train PNR consumes its entire remainder, because the prototype has no train-ticket/fare table.

### G. Feedback and complaints

Feedback and complaints are stored in one `cases` table. A complaint requires a booking owned by the passenger and starts `Open`; feedback starts `Submitted`. Admin can resolve only an open complaint and can close any non-closed complaint.

## 8. Database table map

| Table | Why it exists |
|---|---|
| `passengers` | Passenger account and points wallet |
| `staff` | Staff login, job role, availability |
| `admins` | Seeded admin login |
| `tickets` | Three demo PNR records |
| `station_locations` | Station pickup/drop master data |
| `journey_validations` | Current validated journey for each passenger |
| `resources` | Wheelchair and vehicle stock by station |
| `bookings` | Passenger service bookings and current workflow state |
| `waste_submissions` | Photo-path proof and moderation state |
| `coupons` | Coupon balance, expiry, and usage |
| `redemptions` | Reward conversion history |
| `cases` | Feedback and complaints |

The schema contains primary keys, required fields, check constraints, unique constraints, foreign keys, and indexes for the common lookup/filter paths. Read `sql/01_schema.sql` before changing a model field; model, DAO mapping, SQL insert/update, and DDL need to stay aligned.

## 9. Exceptions and error handling

`AppExceptions.java` contains the application exceptions used by services:

| Exception | Use |
|---|---|
| `ValidationException` | Invalid field or input format |
| `AuthenticationException` | Unknown account or wrong password |
| `AuthorizationException` | User tries to operate outside their role/ownership |
| `NotFoundException` | Requested record does not exist |
| `BusinessRuleException` | Valid input but disallowed by a business rule |
| `DatabaseException` | JDBC/PostgreSQL failure wrapped safely |

The console shows `Reason: ...` from these exceptions and returns to the menu. It does not print stack traces to ordinary users.

## 10. How to add a feature safely

For a new feature, use this checklist:

1. Add a table/column and constraint in `01_schema.sql` if persistence changes.
2. Add or update an entity record in `Entities.java`.
3. Add a DAO method using a prepared statement and try-with-resources.
4. Add business rules, authorization, and transactions in the correct service.
5. Add one focused menu action in `ConsoleApplication`.
6. Add unit tests for calculations/validation and separate integration tests for DAO behavior.
7. Update this guide and the README if the flow or setup changes.

Avoid putting calculations or SQL inside `ConsoleApplication`; that would break the project’s separation of concerns.

## 11. Suggested interview explanation

“This is a plain Java console backend using JDBC and PostgreSQL. The menu layer only collects input. Services enforce rules such as journey validation, inventory availability, fare calculations, role authorization, and legal status transitions. DAOs encapsulate prepared SQL statements and map database rows to immutable Java records. Multi-table operations are JDBC transactions so the wallet, coupon, and booking data cannot become inconsistent. The schema enforces a second layer of integrity with primary keys, foreign keys, unique values, and checks.”

## 12. Reading order

For a quick code review, use this order:

1. `README.md` — setup and scope.
2. `sql/01_schema.sql` and `sql/02_seed.sql` — data design and demo data.
3. `model/Enums.java` and `model/Entities.java` — vocabulary and data shapes.
4. `ConsoleApplication.java` — user-visible features.
5. `service/BookingService.java` — most important business workflow.
6. `service/OperationsService.java` and `service/AuthService.java`.
7. `dao/*` — database implementation details.
8. `src/test/java/*` — executable examples of expected validation and fare behavior.
