package de.uol.swp.common.user;

public interface UserContainerEntity {
    boolean containsUser(final User user);
    default String display() {
        return toString();
    }
}
