package de.uol.swp.common.lobby;

import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * Interface to unify lobby objects
 *
 * This is an Interface to allow for multiple types of lobby objects since it is
 * possible that not every client has to have every information of the lobby.
 *
 * @author Marco Grawunder
 * @see LobbyDTO
 * @since 2019-10-08
 */
public interface Lobby extends Serializable {

    /**
     * Getter for the lobby's name
     *
     * @return A String containing the name of the lobby
     * @since 2019-10-08
     */
    String getName();

    /**
     * Changes the owner of the lobby
     *
     * @param user The user who should be the new owner
     * @since 2019-10-08
     */
    void updateOwner(User user);

    /**
     * Getter for the current owner of the lobby
     *
     * @return A User object containing the owner of the lobby
     * @since 2019-10-08
     */
    User getOwner();

    /**
     * Adds a new user to the lobby
     *
     * @param user The new user to add to the lobby
     * @since 2019-10-08
     */
    void joinUser(User user);

    /**
     * Removes an user from the lobby
     *
     * @param user The user to remove from the lobby
     * @since 2019-10-08
     */
    void leaveUser(User user);

    /**
     * Getter for all users in the lobby
     *
     * @return A Set containing all user in this lobby
     * @since 2019-10-08
     */
    Set<User> getUsers();

    /**
     * Checks whether a given user is in this lobby
     *
     * @param user The user to check
     * @return true if user is in lobby, false otherwise
     * @since 2024-08-29
     */
    boolean containsUser(User user);

    /**
     * Getter for all players in the lobby
     *
     * @return A Set containing all players in this lobby
     * @since 2024-09-13
     */
    Set<Player> getPlayers();

    /**
     * Adds a new Player to the player Set
     *
     * @param player The new player to add to the player Set
     * @since 2024-09-13
     */
    void addPlayer(Player player);

    /**
     * Removes a player from the lobby
     *
     * @param player The player to remove from the lobby
     * @since 2019-10-08
     */
    void removePlayer(Player player);

    /**
     * Gets the Player for the User<br>
     * Returns null if there's no player for the user
     *
     * @param user user of the returned {@link de.uol.swp.common.player.UserPlayer}
     * @since 2019-10-08
     */
    Player getPlayerForUser(User user);

    /**
     * Getter for the current status of the lobby
     *
     * @return The current status of the lobby
     */
    LobbyStatus getStatus();

    /**
     * Sets the status of the lobby
     *
     * @param status The new status of the lobby
     */
    void setStatus(LobbyStatus status);
}
