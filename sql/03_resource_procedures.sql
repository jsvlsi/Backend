-- Station resource stored procedure.
-- Run after sql/01_schema.sql. PostgreSQL compiles and stores this routine once.
create or replace procedure upsert_resource(
    resource_id varchar,
    resource_station varchar,
    resource_service varchar,
    resource_quantity integer
)
language plpgsql
as $$
begin
    insert into resources(id, station, service, quantity)
    values (resource_id, resource_station, resource_service, resource_quantity)
    on conflict (station, service)
    do update set quantity = resources.quantity + excluded.quantity;
end;
$$;
