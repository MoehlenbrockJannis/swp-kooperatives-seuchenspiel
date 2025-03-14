package de.uol.swp.server.database;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * This class is responsible for the connection to the database.
 * It uses the HikariCP connection pool.
 */
@SuppressWarnings("SqlSourceToSinkFlow")
@Singleton
public class DataSource {

    private final HikariDataSource ds;

    @Inject
    public DataSource(DataSourceConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        this.ds = new HikariDataSource(hikariConfig);
    }

    /**
     * Returns a connection to the database.
     *
     * @return Connection to the database
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * Returns a ResultSet from a given query.
     *
     * @param query The query to be executed
     * @return ResultSet from the query
     */
    public Optional<ResultSet> getResultSet(final String query) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            return Optional.of(statement.executeQuery());
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    /**
     * Executes a given query.
     *
     * @param query The query to be executed
     * @throws SQLException if a database access error occurs
     */
    public void executeQuery(String query) throws SQLException {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }
}