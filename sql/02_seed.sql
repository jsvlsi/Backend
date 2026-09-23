-- SHA-256 hash for Test@123.
insert into passengers(id,name,mobile,email,password_hash,points) values
 ('P1001','Ashish Sharma','9876543210','ashish@gmail.com','8776f108e247ab1e2b323042c049c266407c81fbad41bde1e8dfc1bb66fd267e',320),
 ('P1002','Yogesh Bhangale','9988776655','yogesh@gmail.com','8776f108e247ab1e2b323042c049c266407c81fbad41bde1e8dfc1bb66fd267e',180) on conflict (id) do nothing;
insert into staff(id,employee_id,name,password_hash,status) values
 ('STF1','STF1001','Aditya Chavan','8776f108e247ab1e2b323042c049c266407c81fbad41bde1e8dfc1bb66fd267e','Available'),
 ('STF2','STF1002','Neeraj','8776f108e247ab1e2b323042c049c266407c81fbad41bde1e8dfc1bb66fd267e','Available') on conflict (id) do nothing;
insert into admins(id,name,email,password_hash) values ('ADM1','EcoAccess Admin','admin@ecoaccess.com','8776f108e247ab1e2b323042c049c266407c81fbad41bde1e8dfc1bb66fd267e') on conflict (id) do nothing;
insert into tickets values
 ('4521987630','12951','2026-09-20','10:30','Mumbai Central','4','Mumbai Central','New Delhi','B2','3A'),
 ('6109873421','11010','2026-09-22','12:15','Thane','2','Thane','Nagpur','S4','Sleeper'),
 ('8234561907','12127','2026-09-25','16:00','Pune Junction','1','Pune Junction','Mumbai Central','C1','CC') on conflict (pnr) do nothing;
insert into station_locations values ('Mumbai Central','Main Entrance'),('Mumbai Central','Platform 1'),('Mumbai Central','Platform 4'),('Thane','Main Entrance'),('Thane','Platform 2'),('Pune Junction','Main Entrance'),('Pune Junction','Platform 1') on conflict do nothing;
insert into resources values ('RS-WC-MUM','Mumbai Central','Wheelchair',8),('RS-IV-MUM','Mumbai Central','Inter Vehicle',6),('RS-WC-THA','Thane','Wheelchair',6),('RS-IV-THA','Thane','Inter Vehicle',5) on conflict (id) do nothing;
insert into bookings(id,passenger_id,passenger,service,station,platform,pickup,drop_location,service_date,service_time,fare,gross_fare,discount,status,staff_id,train,passenger_count) values
 ('BK-240101','P1001','Ashish Sharma','Wheelchair','Mumbai Central','4','Main Entrance','Platform 4','2026-09-20','10:30',53,53,0,'Assigned','STF1002','12951',1),
 ('BK-240102','P1002','Yogesh Bhangale','Porter','Thane','2','Main Entrance','Platform 2','2026-09-22','12:15',53,53,0,'Booked',null,'11010',1) on conflict (id) do nothing;
