package de.uol.swp.common.user;

import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.response.RetrieveAllOnlineUsersResponse;

import java.util.Objects;

/**
 * Objects of this class are used to transfer user data between the server and the
 * clients.
 *
 * @see de.uol.swp.common.user.User
 * @see RegisterUserRequest
 * @see RetrieveAllOnlineUsersResponse
 */
public class UserDTO implements User {

    private final String username;
    private final String password;
    private final String eMail;

    /**
     * Constructor
     *
     * @param username username of the user
     * @param password password the user uses
     * @param eMail email address the user is registered to
     */
    public UserDTO(String username, String password, String eMail) {
        if (Objects.nonNull(username) && Objects.nonNull(password)) {
            this.username = username;
            this.password = password;
            this.eMail = eMail;
        }else{
            throw new IllegalArgumentException("Nutzername und Passwort dürfen nicht leer sein");
        }
    }

    /**
     * Copy constructor
     *
     * @param user User object to copy the values of
     * @return UserDTO copy of User object
     */
    public static UserDTO create(User user) {
        return new UserDTO(user.getUsername(), user.getPassword(), user.getEMail());
    }

    /**
     * Copy constructor leaving password variable empty
     * <p>
     * This constructor is used for the user list, because it would be a major security
     * flaw to send all user data including passwords to everyone connected.
     *
     * @param user User object to copy the values of
     * @return UserDTO copy of User object having the password variable left empty
     */
    public static UserDTO createWithoutPassword(User user) {
        return new UserDTO(user.getUsername(), "", user.getEMail());
    }


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getEMail() {
        return eMail;
    }

    @Override
    public User getWithoutPassword() {
        return new UserDTO(username, "", eMail);
    }

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(username, userDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username;
    }
}
