package com.ecoaccess.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.ecoaccess.exception.AppExceptions.DatabaseException;
import com.ecoaccess.util.DatabaseConfig;

public final class Database {
    private Database() { }

    public static Connection connection() {
        try {
            return DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to connect to the database. Check PostgreSQL configuration.", exception);
        }
    }

    public static void close(AutoCloseable resource) {
        if (resource == null) return;
        try { resource.close(); } catch (Exception ignored) { }
    }

    public static DatabaseException failure(SQLException exception) {
        return new DatabaseException("Unable to complete the operation due to a database error.", exception);
    }
}
