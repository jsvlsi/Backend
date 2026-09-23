package com.ecoaccess.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.ecoaccess.model.Resource;
import com.ecoaccess.model.Enums.ServiceType;

/** Persistence boundary for station resources. */
public class ResourceDao {
    private static final String FIND_ONE = "select id, station, service, quantity from resources where lower(station)=lower(?) and service=?";
    private static final String FIND_ALL = "select id, station, service, quantity from resources where lower(station) like lower(?) order by station, service";
    private static final String UPDATE = "update resources set quantity=? where id=?";
    private static final String DELETE = "delete from resources where id=?";

    public Optional<Resource> find(String station, ServiceType serviceType) {
        Connection connection = null; PreparedStatement statement = null; ResultSet resultSet = null;
        try {
            connection = Database.connection(); statement = connection.prepareStatement(FIND_ONE);
            statement.setString(1, station); statement.setString(2, serviceType.label()); resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
        } catch (SQLException exception) { throw Database.failure(exception); }
        finally { Database.close(resultSet); Database.close(statement); Database.close(connection); }
    }

    public List<Resource> findAll(String stationSearch) {
        Connection connection = null; PreparedStatement statement = null; ResultSet resultSet = null;
        try {
            connection = Database.connection(); statement = connection.prepareStatement(FIND_ALL);
            statement.setString(1, "%" + stationSearch + "%"); resultSet = statement.executeQuery();
            List<Resource> resources = new ArrayList<>(); while (resultSet.next()) resources.add(map(resultSet)); return resources;
        } catch (SQLException exception) { throw Database.failure(exception); }
        finally { Database.close(resultSet); Database.close(statement); Database.close(connection); }
    }

    /** Uses the PostgreSQL procedure so the upsert logic is compiled and owned by the database. */
    public void addOrMerge(String id, String station, ServiceType serviceType, int quantity) {
        Connection connection = null; CallableStatement statement = null;
        try {
            connection = Database.connection(); statement = connection.prepareCall("{call upsert_resource(?,?,?,?)}");
            statement.setString(1, id); statement.setString(2, station); statement.setString(3, serviceType.label()); statement.setInt(4, quantity); statement.execute();
        } catch (SQLException exception) { throw Database.failure(exception); }
        finally { Database.close(statement); Database.close(connection); }
    }

    public void updateQuantity(String id, int quantity) { executeUpdate(UPDATE, quantity, id); }
    public void delete(String id) { executeUpdate(DELETE, id); }

    private void executeUpdate(String sql, Object... values) {
        Connection connection = null; PreparedStatement statement = null;
        try { connection = Database.connection(); statement = connection.prepareStatement(sql); for (int i=0;i<values.length;i++) statement.setObject(i+1, values[i]); statement.executeUpdate(); }
        catch (SQLException exception) { throw Database.failure(exception); }
        finally { Database.close(statement); Database.close(connection); }
    }

    private Resource map(ResultSet resultSet) throws SQLException {
        return new Resource(resultSet.getString("id"), resultSet.getString("station"), ServiceType.from(resultSet.getString("service")), resultSet.getInt("quantity"));
    }
}
