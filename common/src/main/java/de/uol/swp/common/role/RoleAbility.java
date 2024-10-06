package de.uol.swp.common.role;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.triggerable.Triggerable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;


@Getter
@AllArgsConstructor
public class RoleAbility implements Serializable {
    private Map<Class<? extends GeneralAction>, Class<? extends RoleAction>> replacedActions;
    private List<Class<? extends RoleAction>> additionalActions;
    private List<Triggerable> triggerables;

    /**
     * <p>
     *     Checks if the {@link #replacedActions} {@link Map} contains the given {@code actionClass} as key.
     *     If it does, returns the associated value.
     *     Otherwise, returns the given {@code actionClass}.
     * </p>
     *
     * @param actionClass {@link Action} {@link Class} to search if it is replaced by another {@link Action} {@link Class} for this {@link RoleAbility}
     * @return value in {@link #replacedActions} associated with {@code actionClass} or {@code actionClass} if it is not used as key
     */
    public Class<? extends Action> getRoleSpecificActionClassOrDefault(final Class<? extends Action> actionClass) {
        if (replacedActions.containsKey(actionClass)) {
            return replacedActions.get(actionClass);
        }
        return actionClass;
    }

    /**
     * <p>
     *     Returns an unmodifiable {@link Set} of all {@link RoleAction} classes that the associated role can execute in addition to the general actions.
     * </p>
     *
     * <p>
     *     This includes both the role actions that replace certain general actions as defined by {@link #replacedActions}
     *     as well as the additional actions specified by {@link #additionalActions}.
     * </p>
     *
     * @return unmodifiable {@link Set} of all available {@link RoleAction} classes the associated role can execute
     * @see #replacedActions
     * @see #additionalActions
     */
    public Set<Class<? extends RoleAction>> getRoleSpecificAdditionallyAvailableActionClasses() {
        final Set<Class<? extends RoleAction>> classes = new HashSet<>(replacedActions.values());
        classes.addAll(additionalActions);
        return Collections.unmodifiableSet(classes);
    }

    /**
     * <p>
     *     Returns an unmodifiable {@link Set} of all {@link GeneralAction} classes that the associated role can no longer execute as they are replace by role actions.
     * </p>
     *
     * @return unmodifiable {@link Set} of all unavailable {@link GeneralAction} classes the associated role cannot execute
     * @see #replacedActions
     */
    public Set<Class<? extends GeneralAction>> getRoleSpecificUnavailableActionClasses() {
        return Collections.unmodifiableSet(replacedActions.keySet());
    }
}
