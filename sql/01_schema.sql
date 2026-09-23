-- Run against the ecoaccess PostgreSQL database. This script is rerunnable.
create table if not exists passengers (
 id varchar(30) primary key, name varchar(20) not null check (name ~ '^[A-Za-z]+( [A-Za-z]+)*$'), mobile varchar(10) not null unique check (mobile ~ '^[6-9][0-9]{9}$'), email varchar(254) not null, password_hash char(64) not null, points integer not null default 0 check (points >= 0)
);
create table if not exists staff (
 id varchar(30) primary key,
 employee_id varchar(30) not null unique,
 name varchar(20) not null,
 password_hash char(64) not null,
 status varchar(20) not null default 'Available' check (status in ('Available','Unavailable'))
);
create table if not exists admins (id varchar(30) primary key, name varchar(80) not null, email varchar(254) not null unique, password_hash char(64) not null);
create unique index if not exists ux_admin_email_lower on admins(lower(email));
create table if not exists tickets (pnr char(10) primary key check(pnr ~ '^[0-9]{10}$'), train char(5) not null check(train ~ '^[0-9]{5}$'), journey_date date not null, journey_time time not null, station varchar(80) not null, platform varchar(30) not null, origin varchar(80) not null, destination varchar(80) not null, coach varchar(20) not null, class_name varchar(20) not null);
create table if not exists station_locations (station varchar(80) not null, location_name varchar(80) not null, primary key(station,location_name));
create table if not exists journey_validations (id varchar(30) primary key, passenger_id varchar(30) not null unique references passengers(id) on delete cascade, pnr char(10) not null references tickets(pnr), valid boolean not null default true, validated_at timestamp not null default current_timestamp);
create table if not exists resources (id varchar(30) primary key, station varchar(80) not null, service varchar(20) not null check(service in ('Wheelchair','Inter Vehicle')), quantity integer not null check(quantity >= 0), unique(station,service));
create table if not exists bookings (id varchar(30) primary key, passenger_id varchar(30) not null references passengers(id), passenger varchar(80) not null, service varchar(20) not null check(service in ('Porter','Wheelchair','Inter Vehicle')), station varchar(80) not null, platform varchar(30), pickup varchar(80) not null, drop_location varchar(80) not null, service_date date not null, service_time time not null, fare integer not null check(fare >= 0), gross_fare integer not null check(gross_fare >= 0), discount integer not null default 0 check(discount >= 0 and discount <= gross_fare), coupon_code varchar(40), status varchar(30) not null check(status in ('Booked','Assigned','Accepted','Reached Passenger','Service Started','Completed','Rejected')), staff_id varchar(30) references staff(employee_id), train char(5) not null, bags integer check(bags between 1 and 20), weight_rate integer check(weight_rate in (50,60,70,80)), passenger_count integer not null check(passenger_count >= 1), check(lower(pickup) <> lower(drop_location)));
