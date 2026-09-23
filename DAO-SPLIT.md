# DAO split

Account persistence is separated by entity:

- `PassengerDao` handles only passenger queries and writes.
- `StaffDao` handles only staff queries and writes.
- `AdminDao` handles only admin queries.
- `AccountService` coordinates authentication and account rules.

All DAO SQL is fixed and parameterized. JDBC resources are closed in `finally` blocks through `Database.close`.

For an existing PostgreSQL database, run `sql/04_remove_job_role.sql` after the base schema. New databases should use the staff definition in `sql/01_schema.sql`, which has no `job_role` column.

The old combined `AccountDao` and `AccountRepository` remain temporarily because the legacy `AuthService`, `BookingService`, `OperationsService`, and console still reference them. They should be removed only after those callers are migrated to the entity-specific DAOs; deleting them immediately would break compilation.
