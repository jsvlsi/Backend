-- Run against the ecoaccess PostgreSQL database. This script is rerunnable.
create table if not exists passengers (
  id varchar(30) primary key, name varchar(20) not null check (name ~ '^[A-Za-z]+( [A-Za-z]+)*$'), mobile varchar(10) not null unique check (mobile ~ '^[6-9][0-9]{9}$'), email varchar(254) not null,
  password_hash char(64) not null, points integer not null default 0 check (points >= 0)
);

create table if not exists staff (
  id varchar(30) primary key,
  employee_id varchar(30) not null unique,
  name varchar(20) not null,
  password_hash char(64) not null,
  job_role varchar(80) not null default 'Staff' check (job_role in ('Staff','Support Staff')),
  status varchar(20) not null default 'Available' check (status in ('Available','Unavailable'))
);

create table if not exists admins (id varchar(30) primary key, name varchar(80) not null, email varchar(254) not null unique, password_hash char(64) not null);
create unique index if not exists ux_admin_email_lower on admins(lower(email));
create table if not exists tickets (pnr char(10) primary key check(pnr ~ '^[0-9]{10}$'), train char(5) not null check(train ~ '^[0-9]{5}$'), journey_date date not null, journey_time time not null, station varchar(80) not null, platform varchar(20) not null, origin varchar(80) not null, destination varchar(80) not null, coach varchar(10) not null, class_name varchar(20) not null);
create table if not exists station_locations (station varchar(80) not null, location_name varchar(80) not null, primary key(station,location_name));
create table if not exists journey_validations (id varchar(30) primary key, passenger_id varchar(30) not null unique references passengers(id) on delete cascade, pnr char(10) not null references tickets(pnr) on delete cascade, valid boolean not null default true, validated_at timestamp not null default current_timestamp);
create table if not exists resources (id varchar(30) primary key, station varchar(80) not null, service varchar(20) not null check(service in ('Wheelchair','Inter Vehicle')), quantity integer not null check(quantity >= 0), unique(station,service));
create table if not exists bookings (
  id varchar(30) primary key,
  passenger_id varchar(30) not null references passengers(id),
  passenger varchar(80) not null,
  service varchar(20) not null check(service in ('Porter','Wheelchair','Inter Vehicle')),
  station varchar(80) not null,
  platform varchar(20) not null,
  pickup varchar(80) not null,
  drop_location varchar(80) not null,
  service_date date not null,
  service_time time not null,
  fare integer not null check(fare >= 0),
  gross_fare integer not null check(gross_fare >= 0),
  discount integer not null default 0 check(discount >= 0),
  coupon_code varchar(40),
  status varchar(30) not null check(status in ('Booked','Assigned','Accepted','Reached Passenger','Service Started','Completed','Rejected')),
  staff_id varchar(30),
  train varchar(5) not null,
  bags integer,
  weight_rate integer,
  passenger_count integer not null default 1 check(passenger_count >= 1)
);
create index if not exists ix_bookings_passenger on bookings(passenger_id); create index if not exists ix_bookings_staff_active on bookings(staff_id,status); create index if not exists ix_bookings_station_service on bookings(station,service,status);
create table if not exists waste_submissions (id varchar(30) primary key, passenger_id varchar(30) not null references passengers(id), passenger varchar(80) not null, status varchar(10) not null check(status in ('pending','accepted','rejected')), reward_points integer not null default 0, photo_path varchar(255) not null, submitted_at timestamp not null default current_timestamp, reviewed_at timestamp, remark varchar(255), station varchar(80), platform varchar(20));
create index if not exists ix_waste_passenger_date on waste_submissions(passenger_id,submitted_at desc);
create table if not exists coupons (id varchar(30) primary key, code varchar(40) not null unique, passenger_id varchar(30) not null references passengers(id), passenger varchar(80) not null, points_redeemed integer not null default 0, value integer not null, remaining integer not null default 0, status varchar(10) not null check(status in ('Active','Used','Expired')), created_at timestamp not null default current_timestamp, expires_at timestamp not null, used_at timestamp, used_for varchar(80), used_on varchar(80));
create index if not exists ix_coupons_passenger on coupons(passenger_id,status);
create table if not exists redemptions (id varchar(30) primary key, passenger_id varchar(30) not null references passengers(id), passenger varchar(80) not null, reward_name varchar(100) not null, points integer not null, coupon_code varchar(40), coupon_value integer not null, status varchar(20) not null default 'Pending' check(status in ('Pending','Approved','Rejected')), redemption_date date not null default current_date, created_at timestamp not null default current_timestamp, expires_at timestamp not null);
create table if not exists cases (id varchar(30) primary key, type varchar(10) not null check(type in ('Feedback','Complaint')), passenger_id varchar(30) not null references passengers(id), passenger varchar(80) not null, booking_id varchar(30), rating integer check(rating between 1 and 5), subject varchar(80) not null, description varchar(500) not null, status varchar(20) not null default 'Submitted' check(status in ('Submitted','Open','Closed','Resolved')), created_at timestamp not null default current_timestamp);
create index if not exists ix_cases_type_status on cases(type,status);
