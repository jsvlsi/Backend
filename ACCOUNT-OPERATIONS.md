# Account/authentication separation

The account subsystem now has standalone top-level model support through `AccountRepository` and `AccountService`.

- `AccountRepository` owns account SQL, mapping, and JDBC cleanup.
- `AccountService` owns validation, authentication, registration, and business rules.
- SQL statements are fixed constants and all values are bound parameters.
- JDBC resources are closed explicitly in `finally` blocks through `Database.close`.
- Existing `AccountDao`/`AuthService` remain as compatibility adapters for the current console and other services.

The new classes use `Passenger`, `Staff`, and `Admin` directly from `com.ecoaccess.model`, avoiding the nested `Entities` imports. The old path can be removed after the console and remaining services are migrated to `AccountService`.
