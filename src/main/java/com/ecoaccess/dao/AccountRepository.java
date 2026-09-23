package com.ecoaccess.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.ecoaccess.model.Admin;
import com.ecoaccess.model.Passenger;
import com.ecoaccess.model.Staff;
import com.ecoaccess.model.Enums.StaffStatus;

/** Dedicated persistence boundary for authentication and account data. */
public class AccountRepository {
    private static final String FIND_PASSENGER_BY_MOBILE = "select id,name,mobile,email,password_hash,points from passengers where mobile=?";
    private static final String FIND_PASSENGER_BY_ID = "select id,name,mobile,email,password_hash,points from passengers where id=?";
    private static final String INSERT_PASSENGER = "insert into passengers(id,name,mobile,email,password_hash,points) values(?,?,?,?,?,?)";
    private static final String UPDATE_PASSENGER = "update passengers set name=?,email=?,password_hash=?,points=? where id=?";
    private static final String FIND_STAFF_BY_EMPLOYEE_ID = "select id,employee_id,name,password_hash,job_role,status from staff where lower(employee_id)=lower(?)";
    private static final String FIND_STAFF_BY_ID = "select id,employee_id,name,password_hash,job_role,status from staff where id=?";
    private static final String FIND_STAFF = "select id,employee_id,name,password_hash,job_role,status from staff where lower(name) like lower(?) or lower(employee_id) like lower(?) order by employee_id";
    private static final String FIND_AVAILABLE_STAFF = "select id,employee_id,name,password_hash,job_role,status from staff where status=? order by employee_id";
    private static final String INSERT_STAFF = "insert into staff(id,employee_id,name,password_hash,job_role,status) values(?,?,?,?,?,?)";
    private static final String UPDATE_STAFF = "update staff set name=?,password_hash=?,job_role=?,status=? where id=?";
    private static final String DELETE_STAFF = "delete from staff where id=?";
    private static final String FIND_ADMIN_BY_EMAIL = "select id,name,email,password_hash from admins where lower(email)=lower(?)";

    public Optional<Passenger> findPassengerByMobile(String mobile) { return findPassenger(FIND_PASSENGER_BY_MOBILE, mobile); }
    public Optional<Passenger> findPassengerById(String id) { return findPassenger(FIND_PASSENGER_BY_ID, id); }

    private Optional<Passenger> findPassenger(String sql, String value) {
        Connection connection = null; PreparedStatement statement = null; ResultSet resultSet = null;
        try { connection=Database.connection(); statement=connection.prepareStatement(sql); statement.setString(1,value); resultSet=statement.executeQuery(); return resultSet.next()?Optional.of(mapPassenger(resultSet)):Optional.empty(); }
        catch (SQLException exception) { throw Database.failure(exception); }
        finally { Database.close(resultSet); Database.close(statement); Database.close(connection); }
    }

    public void insertPassenger(Connection connection, Passenger passenger) throws SQLException {
        PreparedStatement statement=null;
        try { statement=connection.prepareStatement(INSERT_PASSENGER); statement.setString(1,passenger.getId()); statement.setString(2,passenger.getName()); statement.setString(3,passenger.getMobile()); statement.setString(4,passenger.getEmail()); statement.setString(5,passenger.getPasswordHash()); statement.setInt(6,passenger.getPoints()); statement.executeUpdate(); }
        finally { Database.close(statement); }
    }

    public void updatePassenger(Connection connection, Passenger passenger) throws SQLException {
        PreparedStatement statement=null;
        try { statement=connection.prepareStatement(UPDATE_PASSENGER); statement.setString(1,passenger.getName()); statement.setString(2,passenger.getEmail()); statement.setString(3,passenger.getPasswordHash()); statement.setInt(4,passenger.getPoints()); statement.setString(5,passenger.getId()); statement.executeUpdate(); }
        finally { Database.close(statement); }
    }

    public Optional<Staff> findStaffByEmployeeId(String employeeId) { return findStaffOne(FIND_STAFF_BY_EMPLOYEE_ID, employeeId); }
    public Optional<Staff> findStaffById(String id) { return findStaffOne(FIND_STAFF_BY_ID, id); }

    private Optional<Staff> findStaffOne(String sql, String value) {
        Connection connection=null; PreparedStatement statement=null; ResultSet resultSet=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(sql); statement.setString(1,value); resultSet=statement.executeQuery(); return resultSet.next()?Optional.of(mapStaff(resultSet)):Optional.empty(); }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(resultSet);Database.close(statement);Database.close(connection);}
    }

    public List<Staff> findStaff(String search) { return findStaffList(FIND_STAFF, search==null?"":search); }
    public List<Staff> findAvailableStaff() { return findStaffList(FIND_AVAILABLE_STAFF, StaffStatus.AVAILABLE.label()); }

    private List<Staff> findStaffList(String sql, String value) {
        Connection connection=null; PreparedStatement statement=null; ResultSet resultSet=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(sql); if(sql.equals(FIND_STAFF)){statement.setString(1,"%"+value+"%");statement.setString(2,"%"+value+"%");}else statement.setString(1,value); resultSet=statement.executeQuery(); List<Staff> staff=new ArrayList<>(); while(resultSet.next())staff.add(mapStaff(resultSet)); return staff; }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(resultSet);Database.close(statement);Database.close(connection);}
    }

    public void insertStaff(Staff staff) { executeStaffUpdate(staff, INSERT_STAFF, true); }
    public void updateStaff(Staff staff) { executeStaffUpdate(staff, UPDATE_STAFF, false); }
    private void executeStaffUpdate(Staff staff,String sql,boolean insert) {
        Connection connection=null; PreparedStatement statement=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(sql); int i=1; statement.setString(i++,staff.getId()); if(insert)statement.setString(i++,staff.getEmployeeId()); statement.setString(i++,staff.getName()); statement.setString(i++,staff.getPasswordHash()); statement.setString(i++,staff.getJobRole()); statement.setString(i++,staff.getStatus().label()); if(!insert)statement.setString(i,staff.getId()); statement.executeUpdate(); }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(statement);Database.close(connection);}
    }

    public void deleteStaff(String id) { executeSingleValue(DELETE_STAFF,id); }
    private void executeSingleValue(String sql,String value){Connection connection=null;PreparedStatement statement=null;try{connection=Database.connection();statement=connection.prepareStatement(sql);statement.setString(1,value);statement.executeUpdate();}catch(SQLException exception){throw Database.failure(exception);}finally{Database.close(statement);Database.close(connection);}}

    public Optional<Admin> findAdminByEmail(String email) { Connection connection=null;PreparedStatement statement=null;ResultSet resultSet=null;try{connection=Database.connection();statement=connection.prepareStatement(FIND_ADMIN_BY_EMAIL);statement.setString(1,email);resultSet=statement.executeQuery();return resultSet.next()?Optional.of(new Admin(resultSet.getString("id"),resultSet.getString("name"),resultSet.getString("email"),resultSet.getString("password_hash"))):Optional.empty();}catch(SQLException exception){throw Database.failure(exception);}finally{Database.close(resultSet);Database.close(statement);Database.close(connection);}}

    private Passenger mapPassenger(ResultSet resultSet)throws SQLException{return new Passenger(resultSet.getString("id"),resultSet.getString("name"),resultSet.getString("mobile"),resultSet.getString("email"),resultSet.getString("password_hash"),resultSet.getInt("points"));}
    private Staff mapStaff(ResultSet resultSet)throws SQLException{return new Staff(resultSet.getString("id"),resultSet.getString("employee_id"),resultSet.getString("name"),resultSet.getString("password_hash"),resultSet.getString("job_role"),StaffStatus.valueOf(resultSet.getString("status").toUpperCase()));}
}
