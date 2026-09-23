# EcoAccess Entity Relationship Diagram

This diagram represents the PostgreSQL structure defined in `sql/01_schema.sql`.

The `staff` table contains only `id`, `employee_id`, `name`, `password_hash`, and `status`. Staff are generic support staff; there is no `job_role` or wheelchair-specific staff category. Wheelchair availability is tracked in `resources` by station and service type.

For existing databases, run `sql/04_remove_job_role.sql` after the base schema migration.
