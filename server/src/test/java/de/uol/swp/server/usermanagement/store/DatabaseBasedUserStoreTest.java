package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.database.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DatabaseBasedUserStoreTest {

    private DatabaseBasedUserStore userStore;
    private ResultSet resultSet;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = mock(DataSource.class);
        userStore = new DatabaseBasedUserStore();
        resultSet = mock(ResultSet.class);
        userStore.setDataSource(dataSource);

    }

    @Test
    @DisplayName("Test if a user can be found by username and password")
    void findUser() throws SQLException {
        String username = "test";
        String password = "test";

        when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password")).thenReturn(password);
        when(resultSet.getString("email")).thenReturn("test@example.com");

        Optional<User> user = userStore.findUser(username, password);

        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("Test if a user can be found by username")
    void testFindUser() throws SQLException {
        String username = "test";
        String password = "test";

        when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password")).thenReturn(password);
        when(resultSet.getString("email")).thenReturn("test@example.com");

        Optional<User> user = userStore.findUser(username);

        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("Create a user successfully")
    void createUserSuccessfully() throws SQLException {
        String username = "test";
        String password = "test";
        String email = "test@example.com";


        doNothing().when(dataSource).executeQuery(anyString());
        when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password")).thenReturn(password);
        when(resultSet.getString("email")).thenReturn(email);

        User user = userStore.createUser(username, password, email);

        verify(dataSource, times(1)).executeQuery(anyString());
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("Update a user successfully")
    void updateUser() throws SQLException {
        String username = "test";
        String password = "test";
        String email = "test@example.com";

        doNothing().when(dataSource).executeQuery(anyString());
        when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password")).thenReturn(password);
        when(resultSet.getString("email")).thenReturn(email);

        User user = userStore.updateUser(username, password, email);

        verify(dataSource, times(1)).executeQuery(anyString());
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("Remove a user successfully")
    void removeUser() throws SQLException {
        String username = "test";

        doNothing().when(dataSource).executeQuery(anyString());

        userStore.removeUser(username);

        verify(dataSource, times(1)).executeQuery(anyString());

    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() throws SQLException {
        String username = "test";
        String password = "";
        String email = "test@example.com";
        User user = new UserDTO(username, password, email);

        when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password")).thenReturn(password);
        when(resultSet.getString("email")).thenReturn(email);

        List<User> userList = userStore.getAllUsers();
        assertThat(userList).contains(user);

    }
}