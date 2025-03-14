package de.uol.swp.common.player;

import de.uol.swp.common.user.User;
import lombok.Getter;

/**
 * The UserPlayer class represents a human player in the game, associated with a User account.
 * This class extends the base Player class and links the player's actions and information
 * to a User object, which holds the user's account details.
 */
public class UserPlayer extends Player{

    @Getter
    private User user;

    /**
     * Constructs a UserPlayer instance with the specified last sickness date and user account.
     *
     * @param user the User object associated with this player
     */
    public UserPlayer(User user) {
        this.user = user;
    }

    /**
     * Retrieves the username of the associated User object.
     *
     * @return the username of the user controlling this player
     * @implNote null check relevant for object decoding
     */
    @Override
    public String getName() {
        if(user == null) {
            return Integer.toHexString(System.identityHashCode(this));
        }
        return user.getUsername();
    }

    @Override
    public boolean containsUser(final User user) {
        return this.user.equals(user);
    }
}
