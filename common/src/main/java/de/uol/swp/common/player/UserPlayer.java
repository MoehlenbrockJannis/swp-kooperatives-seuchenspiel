package de.uol.swp.common.player;

import de.uol.swp.common.user.User;

import java.util.Date;

/**
 * The UserPlayer class represents a human player in the game, associated with a User account.
 * This class extends the base Player class and links the player's actions and information
 * to a User object, which holds the user's account details.
 */
public class UserPlayer extends Player{

    private User user;

    /**
     * Constructs a UserPlayer instance with the specified last sickness date and user account.
     *
     * @param lastSick the date when the user was last sick
     * @param user the User object associated with this player
     */
    public UserPlayer(Date lastSick, User user) {
        super(lastSick);
        this.user = user;
    }

    /**
     * Retrieves the username of the associated User object.
     *
     * @return the username of the user controlling this player
     */
    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public boolean containsUser(final User user) {
        return this.user.equals(user);
    }
}
