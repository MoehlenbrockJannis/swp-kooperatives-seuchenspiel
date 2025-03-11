package de.uol.swp.common.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final User secondsUser = new UserDTO("marco2", "marco", "marco@grawunder.de");

    @Test
    void createUserWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new UserDTO(null, "", ""));
    }

    @Test
    void createUserWithEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> new UserDTO("", null, ""));
    }

    @Test
    void createWithExistingUser() {

        User newUser = UserDTO.create(defaultUser);

        assertEquals(defaultUser, newUser);

        assertEquals(defaultUser.getUsername(), newUser.getUsername());
        assertEquals(defaultUser.getPassword(), newUser.getPassword());
        assertEquals(defaultUser.getEMail(), newUser.getEMail());
    }

    @Test
    void createWithExistingUserWithoutPassword() {
        User newUser = UserDTO.createWithoutPassword(defaultUser);

        assertEquals(defaultUser.getUsername(), newUser.getUsername());
        assertEquals("", newUser.getPassword());
        assertEquals( defaultUser.getEMail(), newUser.getEMail());

        assertEquals(defaultUser, newUser);
    }

    @Test
    void getWithoutPassword() {
        User userWithoutPassword = defaultUser.getWithoutPassword();

        assertEquals("", userWithoutPassword.getPassword());
        assertEquals(defaultUser.getUsername(), userWithoutPassword.getUsername());
    }

    @Test
    void usersNotEquals_User() {
        assertNotEquals(defaultUser, secondsUser);
    }

    @Test
    void userCompare() {
        assertEquals(defaultUser.compareTo(secondsUser), -1);
    }

    @Test
    void testHashCode() {
        User newUser = UserDTO.create(defaultUser);
        assertEquals(newUser.hashCode(), defaultUser.hashCode());

    }
}