package de.uol.swp.server.database;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * This class is responsible for the connection to the database.
 * It uses the HikariCP connection pool.
 */
public class DataSource {

    private static final String PATH_TO_ENV = "src/main/resources";

    private static final Dotenv dotenv = Dotenv.configure()
            .directory(PATH_TO_ENV)
            .load();
    private static final HikariConfig config = new HikariConfig();

    static {
        String url = "jdbc:" + dotenv.get("DB_TYP") + "://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/" + dotenv.get("DB_NAME");
        config.setJdbcUrl(url);
        config.setUsername(dotenv.get("DB_USER"));
        config.setPassword(dotenv.get("DB_PASSWORD"));
    }

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


    /**
     * Returns a ResultSet from a given query.
     *
     * @param query The query to be executed
     * @return ResultSet from the query
     */
    public static Optional<ResultSet> getResultSet(final String query) {
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
    public static void executeQuery(String query) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }



}