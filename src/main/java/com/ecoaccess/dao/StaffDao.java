package com.ecoaccess.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.ecoaccess.model.Enums.StaffStatus;
import com.ecoaccess.model.Staff;

/** Persistence operations for staff only. */
public class StaffDao {
    private static final String FIND_BY_EMPLOYEE_ID = "select id,employee_id,name,password_hash,status from staff where lower(employee_id)=lower(?)";
    private static final String FIND_BY_ID = "select id,employee_id,name,password_hash,status from staff where id=?";
    private static final String FIND_ALL = "select id,employee_id,name,password_hash,status from staff where lower(name) like lower(?) or lower(employee_id) like lower(?) order by employee_id";
    private static final String FIND_AVAILABLE = "select id,employee_id,name,password_hash,status from staff where status=? order by employee_id";
    private static final String INSERT = "insert into staff(id,employee_id,name,password_hash,status) values(?,?,?,?,?)";
    private static final String UPDATE = "update staff set name=?,password_hash=?,status=? where id=?";
    private static final String DELETE = "delete from staff where id=?";

    public Optional<Staff> findByEmployeeId(String employeeId) { return findOne(FIND_BY_EMPLOYEE_ID, employeeId); }
    public Optional<Staff> findById(String id) { return findOne(FIND_BY_ID, id); }

    private Optional<Staff> findOne(String sql, String value) {
        Connection connection=null; PreparedStatement statement=null; ResultSet resultSet=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(sql); statement.setString(1,value); resultSet=statement.executeQuery(); return resultSet.next()?Optional.of(map(resultSet)):Optional.empty(); }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(resultSet);Database.close(statement);Database.close(connection);}
    }

    public List<Staff> findAll(String search) {
        Connection connection=null; PreparedStatement statement=null; ResultSet resultSet=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(FIND_ALL); String value=search==null?"":search; statement.setString(1,"%"+value+"%"); statement.setString(2,"%"+value+"%"); resultSet=statement.executeQuery(); List<Staff> staff=new ArrayList<>(); while(resultSet.next())staff.add(map(resultSet)); return staff; }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(resultSet);Database.close(statement);Database.close(connection);}
    }

    public List<Staff> findAvailable() {
        Connection connection=null; PreparedStatement statement=null; ResultSet resultSet=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(FIND_AVAILABLE); statement.setString(1,StaffStatus.AVAILABLE.label()); resultSet=statement.executeQuery(); List<Staff> staff=new ArrayList<>(); while(resultSet.next())staff.add(map(resultSet)); return staff; }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(resultSet);Database.close(statement);Database.close(connection);}
    }

    public void insert(Staff staff) { execute(INSERT, staff, true); }
    public void update(Staff staff) { execute(UPDATE, staff, false); }
    private void execute(String sql, Staff staff, boolean insert) {
        Connection connection=null; PreparedStatement statement=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(sql); int index=1; statement.setString(index++,staff.getId()); if(insert)statement.setString(index++,staff.getEmployeeId()); statement.setString(index++,staff.getName()); statement.setString(index++,staff.getPasswordHash()); statement.setString(index++,staff.getStatus().label()); if(!insert)statement.setString(index,staff.getId()); statement.executeUpdate(); }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(statement);Database.close(connection);}
    }

    public void delete(String id) { Connection connection=null; PreparedStatement statement=null; try{connection=Database.connection();statement=connection.prepareStatement(DELETE);statement.setString(1,id);statement.executeUpdate();}catch(SQLException exception){throw Database.failure(exception);}finally{Database.close(statement);Database.close(connection);}}

    private Staff map(ResultSet resultSet)throws SQLException{return new Staff(resultSet.getString("id"),resultSet.getString("employee_id"),resultSet.getString("name"),resultSet.getString("password_hash"),StaffStatus.valueOf(resultSet.getString("status").toUpperCase()));}
}
