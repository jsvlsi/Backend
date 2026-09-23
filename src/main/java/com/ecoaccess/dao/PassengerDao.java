package com.ecoaccess.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import com.ecoaccess.model.Passenger;

/** Persistence operations for passengers only. */
public class PassengerDao {
    private static final String FIND_BY_MOBILE = "select id,name,mobile,email,password_hash,points from passengers where mobile=?";
    private static final String FIND_BY_ID = "select id,name,mobile,email,password_hash,points from passengers where id=?";
    private static final String INSERT = "insert into passengers(id,name,mobile,email,password_hash,points) values(?,?,?,?,?,?)";
    private static final String UPDATE = "update passengers set name=?,email=?,password_hash=?,points=? where id=?";

    public Optional<Passenger> findByMobile(String mobile) { return findOne(FIND_BY_MOBILE, mobile); }
    public Optional<Passenger> findById(String id) { return findOne(FIND_BY_ID, id); }

    private Optional<Passenger> findOne(String sql, String value) {
        Connection connection = null; PreparedStatement statement = null; ResultSet resultSet = null;
        try {
            connection = Database.connection(); statement = connection.prepareStatement(sql);
            statement.setString(1, value); resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
        } catch (SQLException exception) { throw Database.failure(exception); }
        finally { Database.close(resultSet); Database.close(statement); Database.close(connection); }
    }

    public void insert(Connection connection, Passenger passenger) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(INSERT);
            statement.setString(1, passenger.getId()); statement.setString(2, passenger.getName());
            statement.setString(3, passenger.getMobile()); statement.setString(4, passenger.getEmail());
            statement.setString(5, passenger.getPasswordHash()); statement.setInt(6, passenger.getPoints());
            statement.executeUpdate();
        } finally { Database.close(statement); }
    }

    public void update(Connection connection, Passenger passenger) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(UPDATE);
            statement.setString(1, passenger.getName()); statement.setString(2, passenger.getEmail());
            statement.setString(3, passenger.getPasswordHash()); statement.setInt(4, passenger.getPoints());
            statement.setString(5, passenger.getId()); statement.executeUpdate();
        } finally { Database.close(statement); }
    }

    private Passenger map(ResultSet resultSet) throws SQLException {
        return new Passenger(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("mobile"), resultSet.getString("email"), resultSet.getString("password_hash"), resultSet.getInt("points"));
    }
}
