package de.uol.swp.server.usermanagement.store;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DatabaseBasedUserStore extends AbstractStore implements UserStore, DatabaseStore {

    private DataSource dataSource;

    @Inject
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        try {
            ResultSet resultSet = this.dataSource.getResultSet(query).orElseThrow();
            if (resultSet.next()) {
                return getOptionalUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        try {
            ResultSet resultSet = this.dataSource.getResultSet(query).orElseThrow();
            if (resultSet.next()) {
                return getOptionalUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return Optional.empty();
    }

    @Override
    public User createUser(String username, String password, String eMail) {
        String query = "INSERT INTO users (username, password, email, registered ) VALUES ('" + username + "', '" + password + "', '" + eMail + "', '" + getLocalDateTime() + "')";
        try {
            this.dataSource.executeQuery(query);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return new UserDTO(username, password, eMail);
    }

    @Override
    public User updateUser(String username, String password, String eMail) {
        String query = "UPDATE users SET password = '" + password + "', email = '" + eMail + "' WHERE username = '" + username + "'";
        try {
            this.dataSource.executeQuery(query);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return new UserDTO(username, password, eMail);
    }

    @Override
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username = '" + username + "'";
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
            ResultSet resultSet = this.dataSource.getResultSet(query).orElseThrow();
            while (resultSet.next()) {
                User newUser = new UserDTO(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
                users.add(newUser.getWithoutPassword());
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

        return users;
    }

    private String getLocalDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private Optional<User> getOptionalUserFromResultSet(ResultSet resultSet) throws SQLException {
        return Optional.of(new UserDTO(
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("email")
        ).getWithoutPassword());
    }
}
