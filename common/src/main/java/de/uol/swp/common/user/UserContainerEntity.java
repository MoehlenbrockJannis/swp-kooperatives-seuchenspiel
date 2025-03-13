package de.uol.swp.common.user;

/**
 * Represents an entity that can contain or be associated with a user.
 * <p>
 * This interface provides functionality to check whether a specific {@link User} is associated with the entity
 * and to display a string representation of the entity.
 * </p>
 */
public interface UserContainerEntity {
    boolean containsUser(final User user);
    default String display() {
        return toString();
    }
}
