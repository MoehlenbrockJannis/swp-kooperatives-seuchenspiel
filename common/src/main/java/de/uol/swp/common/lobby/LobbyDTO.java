package de.uol.swp.common.lobby;

import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserContainerEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Object to transfer the information of a game lobby
 *
 * <p>
 * This object is used to communicate the current state of game lobbies between
 * the server and clients. It contains information about the Name of the lobby,
 * who owns the lobby and who joined the lobby.
 * </p>
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */

@ToString
public class LobbyDTO implements Lobby {
    @Getter
    @Setter
    private int id;
    @Getter
    private final String name;
    @Getter
    private User owner;
    private Set<Player> players = new HashSet<>();
    @Setter
    private LobbyStatus status;
    @Getter
    private int minPlayers;
    @Getter
    private int maxPlayers;

    /**
     * Constructor
     *
     * @param name    The name the lobby should have
     * @param creator The user who created the lobby and therefore shall be the
     *                owner
     * @param minPlayers The minimum number of players that can join the lobby
     * @param maxPlayers The maximum number of players that can join the lobby
     * @since 2019-10-08
     */
    public LobbyDTO(String name, User creator, int minPlayers, int maxPlayers) {
        this.name = name;
        this.owner = creator;
        joinUser(creator);
        this.status = LobbyStatus.OPEN;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public LobbyDTO(Lobby lobby) {
        this.name = lobby.getName();
        this.owner = lobby.getOwner();
        this.status = lobby.getStatus();
        this.minPlayers = lobby.getMinPlayers();
        this.maxPlayers = lobby.getMaxPlayers();
        this.players = lobby.getPlayers();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Lobby lobby) {
            return id == lobby.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public void joinUser(User user) {
        addPlayer(new UserPlayer(user));
    }

    @Override
    public void leaveUser(User user) {
        final Set<User> users = getUsers();
        if (users.size() == 1) {
            throw new IllegalArgumentException("Lobby must contain at least one user!");
        }
        if (users.contains(user)) {
            removePlayer(getPlayerForUser(user));
            updateOwnerIfLeavingUserIsOwner(user);
        }
    }

    private void updateOwnerIfLeavingUserIsOwner(UserContainerEntity leavingUser) {
        if (leavingUser.containsUser(this.owner)) {
            updateOwner(getUsers().iterator().next());
        }
    }

    @Override
    public void updateOwner(User user) {
        if (!this.getUsers().contains(user)) {
            throw new IllegalArgumentException("User " + user.getUsername() + "not found. Owner must be member of lobby!");
        }
        this.owner = user;
    }

    @Override
    public Set<User> getUsers() {
        return getPlayers().stream()
                .filter(UserPlayer.class::isInstance)
                .map(UserPlayer.class::cast)
                .map(UserPlayer::getUser)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean containsUser(final User user) {
        for (final User userInLobby : getUsers()) {
            if (userInLobby.equals(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    @Override
    public void addPlayer(Player player) {
        players.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
        updateOwnerIfLeavingUserIsOwner(player);
    }

    @Override
    public Player getPlayerForUser(User user) {
        if(user != null) {
            for (final Player player : players) {
                if(player.containsUser(user)) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public LobbyStatus getStatus() {
        determineLobbyStatus();
        return status;
    }

    private void determineLobbyStatus() {
        final Set<Player> players = getPlayers();
        if (players.size() < this.maxPlayers && !status.equals(LobbyStatus.RUNNING)) {
            status = LobbyStatus.OPEN;
        } else if (players.size() == this.maxPlayers && !status.equals(LobbyStatus.RUNNING)) {
            status = LobbyStatus.FULL;
        } else {
            status = LobbyStatus.RUNNING;
        }
    }

}
