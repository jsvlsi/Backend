package com.ecoaccess.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import com.ecoaccess.model.Admin;

/** Persistence operations for administrators only. */
public class AdminDao {
    private static final String FIND_BY_EMAIL = "select id,name,email,password_hash from admins where lower(email)=lower(?)";

    public Optional<Admin> findByEmail(String email) {
        Connection connection=null; PreparedStatement statement=null; ResultSet resultSet=null;
        try { connection=Database.connection(); statement=connection.prepareStatement(FIND_BY_EMAIL); statement.setString(1,email); resultSet=statement.executeQuery(); return resultSet.next()?Optional.of(new Admin(resultSet.getString("id"),resultSet.getString("name"),resultSet.getString("email"),resultSet.getString("password_hash"))):Optional.empty(); }
        catch(SQLException exception){throw Database.failure(exception);} finally {Database.close(resultSet);Database.close(statement);Database.close(connection);}
    }
}
