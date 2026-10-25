package com.framework.backend.utilities;

import com.framework.common.utilities.DatabaseClient;

public class UserQueries {

    DatabaseClient databaseClient;

    public UserQueries(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public int deleteUserByEmail(String email) {
        String query = "DELETE FROM \"Users\" WHERE email = ?";
        return databaseClient.executeUpdate(query, email);
    }

    public int deleteUserByUsername(String username) {
        String query = "DELETE FROM \"Users\" WHERE username = ?";
        return databaseClient.executeUpdate(query, username);
    }
}
