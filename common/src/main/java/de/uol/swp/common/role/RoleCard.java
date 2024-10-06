package de.uol.swp.common.role;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.card.Card;
import de.uol.swp.common.util.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

/**
 * This class represent a RoleCard
 *
 * @author Jannis Moehlenbrock
 * @since 2024-09-06
 */
@Getter
@Setter
@AllArgsConstructor
public class RoleCard extends Card {

    private String name;
    private Color color;
    //private Image image;
    private RoleAbility ability;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RoleCard roleCard) {
            return name.equals(roleCard.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String getTitle() {
        return this.name;
    }

    /**
     * <p>
     *     Delegates to {@link RoleAbility#getRoleSpecificActionClassOrDefault(Class)} of {@link #ability}.
     * </p>
     *
     * @param actionClass {@link Action} {@link Class} to pass to {@link RoleAbility#getRoleSpecificActionClassOrDefault(Class)}
     * @return result of {@link RoleAbility#getRoleSpecificActionClassOrDefault(Class)} called with given {@code actionClass}
     * @see RoleAbility#getRoleSpecificActionClassOrDefault(Class)
     */
    public Class<? extends Action> getRoleSpecificActionClassOrDefault(final Class<? extends Action> actionClass) {
        return ability.getRoleSpecificActionClassOrDefault(actionClass);
    }

    /**
     * <p>
     *     Returns an unmodifiable {@link Set} of all {@link RoleAction} classes that the associated role can execute in addition to the general actions.
     * </p>
     *
     * <p>
     *     Delegates to {@link RoleAbility#getRoleSpecificAdditionallyAvailableActionClasses()} of {@link #ability}.
     * </p>
     *
     * @return unmodifiable {@link Set} of all available {@link RoleAction} classes the associated role can execute
     * @see RoleAbility#getRoleSpecificAdditionallyAvailableActionClasses()
     */
    public Set<Class<? extends RoleAction>> getRoleSpecificAdditionallyAvailableActionClasses() {
        return ability.getRoleSpecificAdditionallyAvailableActionClasses();
    }

    /**
     * <p>
     *     Returns an unmodifiable {@link Set} of all {@link GeneralAction} classes that the associated role can no longer execute as they are replace by role actions.
     * </p>
     *
     * <p>
     *     Delegates to {@link RoleAbility#getRoleSpecificUnavailableActionClasses()} of {@link #ability}.
     * </p>
     *
     * @return unmodifiable {@link Set} of all unavailable {@link GeneralAction} classes the associated role cannot execute
     * @see RoleAbility#getRoleSpecificUnavailableActionClasses()
     */
    public Set<Class<? extends GeneralAction>> getRoleSpecificUnavailableActionClasses() {
        return ability.getRoleSpecificUnavailableActionClasses();
    }
}
