package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.server.database.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    }

    @Test
    @DisplayName("Test if a user can be found by username and password")
    void findUser() throws SQLException {
        String username = "test";
        String password = "test";

        try (MockedStatic<DataSource> mockedDataSource = mockStatic(DataSource.class)) {
            when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("username")).thenReturn(username);
            when(resultSet.getString("password")).thenReturn(password);
            when(resultSet.getString("email")).thenReturn("test@example.com");

            Optional<User> user = userStore.findUser(username, password);

            assertThat(user).isPresent();
            assertThat(user.get().getUsername()).isEqualTo(username);
        }

    }

    @Test
    @DisplayName("Test if a user can be found by username")
    void testFindUser() throws SQLException {
        String username = "test";
        String password = "test";

        try (MockedStatic<DataSource> mockedDataSource = mockStatic(DataSource.class)) {
            when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("username")).thenReturn(username);
            when(resultSet.getString("password")).thenReturn(password);
            when(resultSet.getString("email")).thenReturn("test@example.com");

            Optional<User> user = userStore.findUser(username);

            assertThat(user).isPresent();
            assertThat(user.get().getUsername()).isEqualTo(username);
        }
    }

    @Test
    @DisplayName("Create a user successfully")
    void createUserSuccessfully() {
        String username = "test";
        String password = "test";
        String email = "test@example.com";

        try (MockedStatic<DataSource> mockedDataSource = mockStatic(DataSource.class)) {
            mockedDataSource.when(() -> dataSource.executeQuery(anyString())).thenAnswer(invocation -> null);

            User user = userStore.createUser(username, password, email);

            mockedDataSource.verify(() -> dataSource.executeQuery(anyString()), times(1));
            assertThat(user).isNotNull();
            assertThat(user.getUsername()).isEqualTo(username);
        }

    }

    @Test
    @DisplayName("Update a user successfully")
    void updateUser() {
        String username = "test";
        String password = "test";
        String email = "test@example.com";

        try (MockedStatic<DataSource> mockedDataSource = mockStatic(DataSource.class)) {
            mockedDataSource.when(() -> dataSource.executeQuery(anyString())).thenAnswer(invocation -> null);

            User user = userStore.updateUser(username, password, email);

            mockedDataSource.verify(() -> dataSource.executeQuery(anyString()), times(1));
            assertThat(user).isNotNull();
            assertThat(user.getUsername()).isEqualTo(username);
        }
    }

    @Test
    @DisplayName("Remove a user successfully")
    void removeUser() {
        String username = "test";

        try (MockedStatic<DataSource> mockedDataSource = mockStatic(DataSource.class)) {
            mockedDataSource.when(() -> dataSource.executeQuery(anyString())).thenAnswer(invocation -> null);

            userStore.removeUser(username);

            mockedDataSource.verify(() -> dataSource.executeQuery(anyString()), times(1));
        }
    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() throws SQLException {
        try (MockedStatic<DataSource> mockedDataSource = mockStatic(DataSource.class)) {
            when(dataSource.getResultSet(anyString())).thenReturn(Optional.of(resultSet));
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("username")).thenReturn("test");
            when(resultSet.getString("password")).thenReturn("test");
            when(resultSet.getString("email")).thenReturn("test@example.com");

            assertThat(userStore.getAllUsers()).isNotNull();
        }
    }
}