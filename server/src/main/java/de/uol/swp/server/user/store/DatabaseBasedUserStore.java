package de.uol.swp.server.user.store;

import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.database.DataSource;
import de.uol.swp.server.store.AbstractStore;
import de.uol.swp.server.store.DatabaseStore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class DatabaseBasedUserStore extends AbstractStore implements UserStore, DatabaseStore {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    private DataSource dataSource;

    @Inject
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findUser(String username, String password) {
        String query = "SELECT * FROM users WHERE "+ USERNAME +" = '" + username + "' AND " + PASSWORD + " = '" + password + "'";
        return getOneUserByQuery(query);
    }

    @Override
    public Optional<User> findUser(String username) {
        String query = "SELECT * FROM users WHERE " + USERNAME + " = '" + username + "'";
        return getOneUserByQuery(query);
    }

    @Override
    public User createUser(String username, String password, String eMail) {
        String query = "INSERT INTO users (" + USERNAME + ", " + PASSWORD + ", " + EMAIL + ", registered ) VALUES ('" + username + "', '" + password + "', '" + eMail + "', '" + getLocalDateTime() + "')";
        try {
            this.dataSource.executeQuery(query);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return this.findUser(username).orElse(null);
    }

    @Override
    public User updateUser(String username, String password, String eMail) {
        String query = "UPDATE users SET " + PASSWORD + " = '" + password + "', " + EMAIL + " = '" + eMail + "' WHERE " + USERNAME + "= '" + username + "'";
        try {
            this.dataSource.executeQuery(query);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return this.findUser(username).orElse(null);
    }

    @Override
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE " + USERNAME + " = '" + username + "'";
        try {
            this.dataSource.executeQuery(sql);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try {
            ResultSet resultSet = this.dataSource.getResultSet(query).orElse(null);
            while (resultSet != null && resultSet.next()) {
                User newUser = getUserFromResultSet(resultSet);
                users.add(newUser);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

        return users;
    }

    /**
     * Returns the current date and time as a formatted string in the format "yyyy-MM-dd HH:mm:ss".
     *
     * @return the current date and time as a formatted string
     */
    private String getLocalDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Returns a user object from a given ResultSet.
     *
     * @param resultSet the ResultSet to get the user from
     * @return the user object
     * @throws SQLException if a database access error occurs
     */
    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        return new UserDTO(
                resultSet.getString(USERNAME),
                resultSet.getString(PASSWORD),
                resultSet.getString(EMAIL)
        ).getWithoutPassword();
    }

    /**
     * Returns a user object from a given query.
     *
     * @param query the query to get the user from
     * @return the user object
     */
    private Optional<User> getOneUserByQuery(String query) {
        try {
            ResultSet resultSet = this.dataSource.getResultSet(query).orElse(null);
            if (resultSet != null && resultSet.next()) {
                return Optional.of(getUserFromResultSet(resultSet));
            }
        } catch (SQLException e ) {
            throw new IllegalArgumentException(e);
        }
        return Optional.empty();
    }

    @Override
    protected Set<Integer> getIds() {
        return Collections.emptySet();
    }
}
