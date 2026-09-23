# Separated resource operations

Resource inventory is now isolated behind `ResourceDao` and `ResourceService`.

- `ResourceService` contains validation and application-level rules.
- `ResourceDao` contains parameterized SQL and resource mapping only.
- `sql/03_resource_procedures.sql` defines the PostgreSQL `upsert_resource` procedure.
- `ResourceDao.addOrMerge` calls the procedure through `CallableStatement`.
- JDBC resources are closed in `finally` blocks through `Database.close`.

Run the procedure script after the base schema and before using the separated resource service:

```text
psql -U postgres -d ecoaccess -f sql/03_resource_procedures.sql
```

The existing `OperationsDao` and `OperationsService` remain available during migration. The console can be switched to `ResourceService` in a later step without changing the resource schema.
