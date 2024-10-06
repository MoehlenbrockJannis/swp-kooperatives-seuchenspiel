package de.uol.swp.common.action;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ActionFactory {
    /**
     * {@link Reflections} object for the package {@link Action} is in
     */
    private final Reflections actionPackageReflections = new Reflections(Action.class.getPackageName());

    /**
     * <p>
     *     Creates a {@link List} of actions by instantiating one object each of every {@link GeneralAction} class
     *     except the ones provided in {@code excludedGeneralActions}.
     *     Also creates one instance each of every role action in {@code includedRoleActions}.
     * </p>
     *
     * @param excludedGeneralActions {@link Collection} of general actions of which no instance is created
     * @param includedRoleActions {@link Collection} of role actions of which an instance is created
     * @return {@link List} of actions
     * @see #getAllInstantiableNonRoleActionClassesWithPublicDefaultConstructor()
     * @see #createActions(Collection)
     */
    public List<Action> createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(
            final Collection<Class<? extends GeneralAction>> excludedGeneralActions,
            final Collection<Class<? extends RoleAction>> includedRoleActions
    ) {
        final Set<Class<? extends Action>> actionClasses = new HashSet<>(
                getAllInstantiableNonRoleActionClassesWithPublicDefaultConstructor()
        );

        actionClasses.removeAll(excludedGeneralActions);

        actionClasses.addAll(includedRoleActions);

        return createActions(actionClasses);
    }

    /**
     * <p>
     *     Returns a {@link Set} of {@link Class} objects for {@link Action} classes
     *     that do not implement the {@link RoleAction} {@code interface}.
     * </p>
     *
     * <p>
     *     All of the returned classes must be instantiable via a public default constructor.
     * </p>
     *
     * @return {@link Set} of {@link Class} objects for {@link Action} classes not implementing {@link RoleAction}
     * @see #getAllInstantiableActionClassesWithPublicDefaultConstructor()
     */
    public Set<Class<? extends Action>> getAllInstantiableNonRoleActionClassesWithPublicDefaultConstructor() {
        return getAllInstantiableActionClassesWithPublicDefaultConstructor().stream()
                .filter(actionClass -> !RoleAction.class.isAssignableFrom(actionClass))
                .collect(Collectors.toSet());
    }

    /**
     * <p>
     *     Returns a {@link Set} of all {@link Class} objects for {@link Action} classes
     *     that can be instantiated and have a public default constructor.
     * </p>
     *
     * @return {@link Set} of all {@link Class} objects for instantiable {@link Action} classes that have a public default constructor
     * @see #getAllInstantiableActionClasses()
     */
    public Set<Class<? extends Action>> getAllInstantiableActionClassesWithPublicDefaultConstructor() {
        return getAllInstantiableActionClasses().stream()
                .filter(actionClass -> Arrays.stream(actionClass.getDeclaredConstructors())
                        .anyMatch(constructor ->
                                constructor.getParameterCount() == 0 &&
                                Modifier.isPublic(constructor.getModifiers())
                        )
                )
                .collect(Collectors.toSet());
    }

    /**
     * <p>
     *     Returns a {@link Set} of all {@link Class} objects for {@link Action} classes
     *     that can be instantiated (i.e. not {@code abstract} or an {@code interface}).
     * </p>
     *
     * @return {@link Set} of all {@link Class} objects for instantiable {@link Action} classes
     * @see #getPublicActionClasses()
     */
    public Set<Class<? extends Action>> getAllInstantiableActionClasses() {
        return getPublicActionClasses().stream()
                .filter(actionClass ->
                        !Modifier.isAbstract(actionClass.getModifiers()) &&
                        !Modifier.isInterface(actionClass.getModifiers())
                )
                .collect(Collectors.toSet());
    }

    /**
     * <p>
     *     Returns a {@link Set} of all {@link Class} objects for public subtypes of {@link Action}.
     *     Both {@code class} and {@code interface} Types are included in that {@link Set}.
     * </p>
     *
     * @return {@link Set} of all {@link Class} objects for public subtypes of {@link Action}
     */
    public Set<Class<? extends Action>> getPublicActionClasses() {
        return actionPackageReflections.getSubTypesOf(Action.class).stream()
                .filter(actionClass -> Modifier.isPublic(actionClass.getModifiers()))
                .collect(Collectors.toSet());
    }

    /**
     * <p>
     *     Creates a {@link List} of instances of every {@link Class} in give {@link Collection} of {@link Action} classes.
     *     One instance is created for every {@link Class} in {@code actionClasses}.
     * </p>
     *
     * <p>
     *     If any {@link Exception} occurs during the creation of an instance of a {@link Class} object,
     *     that {@link Class} object is ignored and the rest are instantiated.
     * </p>
     *
     * @param actionClasses {@link Collection} of {@link Action} classes to create instances from
     * @return {@link List} of instances of subtypes of {@link Action}
     * @see #createActions(Collection)
     */
    public List<Action> createActions(final Collection<Class<? extends Action>> actionClasses) {
        final List<Action> actions = new ArrayList<>();

        for (final Class<? extends Action> actionClass : actionClasses) {
            try {
                actions.add(createAction(actionClass));
            } catch (final Exception e) {
                // ignore
            }
        }

        return  actions;
    }

    /**
     * <p>
     *     Creates an instance of the given {@link Action} {@link Class} by calling a public default constructor.
     *     After creation, the instance is returned.
     * </p>
     *
     * <p>
     *     If any {@link Exception} occurs, a new {@link RuntimeException} is created and thrown.
     * </p>
     *
     * @param actionClass {@link Class} object of the {@link Action} to be instantiated
     * @return new instance of {@code actionClass}
     * @param <T> Type of {@link Action} to be created
     * @throws RuntimeException if an error occurs during instantiation of action
     */
    public <T extends Action> T createAction(final Class<T> actionClass) {
        try {
            return actionClass.getDeclaredConstructor().newInstance();
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
