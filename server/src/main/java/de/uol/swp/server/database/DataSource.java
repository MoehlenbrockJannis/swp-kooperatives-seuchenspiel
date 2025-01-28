package de.uol.swp.server.database;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is responsible for the connection to the database.
 * It uses the HikariCP connection pool.
 */
public class DataSource {

    private static final HikariConfig config = new HikariConfig("hikari.properties");
    private static final HikariDataSource ds = new HikariDataSource(config);

    /**
     * Private constructor to prevent instantiation.
     */
    private DataSource() {
    }

    /**
     * Returns a connection to the database.
     *
     * @return Connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}