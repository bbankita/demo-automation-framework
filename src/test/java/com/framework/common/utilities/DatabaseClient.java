package com.framework.common.utilities;

import com.framework.configuration.Database;
import com.framework.exceptions.FrameworkException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.framework.common.utilities.ConfigurationReader.getConfigurations;

public class DatabaseClient {

    public DatabaseClient() {
    }

    private Connection openConnection() throws SQLException {
        Connection connection;
        Database db = getConfigurations().getDatabase();
        LogHelper.logDatabaseConnection(db.getHost(), db.getUsername());
        connection = DriverManager.getConnection(db.getHost(), db.getUsername(), db.getPassword());
        return connection;
    }

    /* For INSERT, UPDATE, DELETE */
    public int executeUpdate(String sql, Object... params) {
        //Used try-with-resources to ensure that the connection, statement, and result set are closed automatically
        try (Connection connection = openConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            bind(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new FrameworkException("Error executing update: " + sql + " " + e.getMessage(), e);
        }
    }


    private void bind(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    /* For SELECT */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        //Used try-with-resources to ensure that the connection, statement, and result set are closed automatically
        try (Connection connection = openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                var metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            throw new FrameworkException("Error executing query: " + e.getMessage(), e);
        }
        return results;
    }
}


