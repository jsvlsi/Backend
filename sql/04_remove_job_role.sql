-- Migration for existing installations. Run after the base schema.
-- The fresh schema should define staff without job_role; this removes it from old databases.
alter table if exists staff drop column if exists job_role;
