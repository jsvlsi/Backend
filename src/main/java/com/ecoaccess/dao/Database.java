package com.ecoaccess.dao;

import java.sql.*;
import com.ecoaccess.exception.AppExceptions.DatabaseException;
import com.ecoaccess.util.DatabaseConfig;

public final class Database {
    private Database() { }
    public static Connection connection() {
        try { return DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD); }
        catch (SQLException e) { throw new DatabaseException("Unable to connect to the database. Check PostgreSQL configuration.", e); }
    }
    public static DatabaseException failure(SQLException e) { return new DatabaseException("Unable to complete the operation due to a database error.", e); }
}
