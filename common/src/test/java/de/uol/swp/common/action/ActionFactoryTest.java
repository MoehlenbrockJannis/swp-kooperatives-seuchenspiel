package de.uol.swp.common.action;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.ReducedCostBuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.cure_plague.CurePlagueAction;
import de.uol.swp.common.action.advanced.cure_plague.IncreasedEffectivenessCurePlagueAction;
import de.uol.swp.common.action.advanced.discover_antidote.DiscoverAntidoteAction;
import de.uol.swp.common.action.advanced.discover_antidote.ReducedCostDiscoverAntidoteAction;
import de.uol.swp.common.action.advanced.transfer_card.NoLimitsSendCardAction;
import de.uol.swp.common.action.advanced.transfer_card.ReceiveCardAction;
import de.uol.swp.common.action.advanced.transfer_card.SendCardAction;
import de.uol.swp.common.action.advanced.transfer_card.ShareKnowledgeAction;
import de.uol.swp.common.action.simple.*;
import de.uol.swp.common.action.simple.car.CarAction;
import de.uol.swp.common.action.simple.car.CarActionForAlly;
import de.uol.swp.common.action.simple.charter_flight.CharterFlightAction;
import de.uol.swp.common.action.simple.charter_flight.CharterFlightActionForAlly;
import de.uol.swp.common.action.simple.direct_flight.DirectFlightAction;
import de.uol.swp.common.action.simple.direct_flight.DirectFlightActionForAlly;
import de.uol.swp.common.action.simple.shuttle_flight.ShuttleFlightAction;
import de.uol.swp.common.action.simple.shuttle_flight.ShuttleFlightActionForAlly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The following fields need to be updated accordingly when new action classes are added:
 * <ul>
 *     <li>{@link #instantiableNonRoleActionClassesWithPublicDefaultConstructor}</li>
 *     <li>{@link #instantiableRoleActionClassesWithPublicDefaultConstructor}</li>
 *     <li>{@link #nonInstantiableActionClasses}</li>
 * </ul>
 */
class ActionFactoryTest {
    private ActionFactory factory;
    private final Set<Class<? extends Action>> instantiableNonRoleActionClassesWithPublicDefaultConstructor = Set.of(
            BuildResearchLaboratoryAction.class,
            CurePlagueAction.class,
            DiscoverAntidoteAction.class,
            ReceiveCardAction.class,
            SendCardAction.class,

            CarAction.class,
            CharterFlightAction.class,
            DirectFlightAction.class,
            ShuttleFlightAction.class,
            WaiveAction.class
    );
    private final Set<Class<? extends Action>> instantiableRoleActionClassesWithPublicDefaultConstructor = Set.of(
            ReducedCostBuildResearchLaboratoryAction.class,
            IncreasedEffectivenessCurePlagueAction.class,
            ReducedCostDiscoverAntidoteAction.class,
            NoLimitsSendCardAction.class,

            CarActionForAlly.class,
            CharterFlightActionForAlly.class,
            DirectFlightActionForAlly.class,
            ShuttleFlightActionForAlly.class,

            MoveAllyToAllyAction.class
    );
    private final Set<Class<? extends Action>> nonInstantiableActionClasses = Set.of(
            ShareKnowledgeAction.class,

            AdvancedAction.class,

            MoveAction.class,
            MoveAllyAction.class,
            SimpleAction.class,

            DiscardCardsAction.class,
            GeneralAction.class,
            RoleAction.class
    );

    @BeforeEach
    void setUp() {
        factory = new ActionFactory();
    }

    @Test
    @DisplayName("Should create a list of instances of all action classes not extending RoleAction except for the given excludedGeneralActions and including the given includedRoleActions")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions() {
        final Collection<Class<? extends GeneralAction>> excludedGeneralActions = List.of(
                BuildResearchLaboratoryAction.class
        );
        final Collection<Class<? extends RoleAction>> includedRoleActions = List.of(
                ReducedCostBuildResearchLaboratoryAction.class,
                MoveAllyToAllyAction.class
        );

        final List expectedClasses = List.of(
                ReducedCostBuildResearchLaboratoryAction.class,
                CurePlagueAction.class,
                DiscoverAntidoteAction.class,
                ReceiveCardAction.class,
                SendCardAction.class,

                CarAction.class,
                CharterFlightAction.class,
                DirectFlightAction.class,
                ShuttleFlightAction.class,
                WaiveAction.class,

                MoveAllyToAllyAction.class
        );

        final List<Action> actual = factory.createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(excludedGeneralActions, includedRoleActions);
        final List actualClasses = actual.stream()
                .map(Action::getClass)
                .toList();

        assertThat(actualClasses)
                .containsExactlyInAnyOrderElementsOf(expectedClasses);
    }

    @Test
    @DisplayName("Should return a set of all instantiable action classes not extending RoleAction that have a public default constructor")
    void getAllInstantiableNonRoleActionClassesWithPublicDefaultConstructor() {
        assertThat(factory.getAllInstantiableNonRoleActionClassesWithPublicDefaultConstructor())
                .containsExactlyInAnyOrderElementsOf(instantiableNonRoleActionClassesWithPublicDefaultConstructor);
    }

    @Test
    @DisplayName("Should return a set of all instantiable action classes that have a public default constructor")
    void getAllInstantiableActionClassesWithPublicDefaultConstructor() {
        final Set<Class<? extends Action>> instantiableActionClasses = new HashSet<>();
        instantiableActionClasses.addAll(instantiableNonRoleActionClassesWithPublicDefaultConstructor);
        instantiableActionClasses.addAll(instantiableRoleActionClassesWithPublicDefaultConstructor);

        assertThat(factory.getAllInstantiableActionClassesWithPublicDefaultConstructor())
                .containsExactlyInAnyOrderElementsOf(instantiableActionClasses);
    }

    @Test
    @DisplayName("Should return a set of all instantiable action classes")
    void getAllInstantiableActionClasses() {
        final Set<Class<? extends Action>> instantiableActionClasses = new HashSet<>();
        instantiableActionClasses.addAll(instantiableNonRoleActionClassesWithPublicDefaultConstructor);
        instantiableActionClasses.addAll(instantiableRoleActionClassesWithPublicDefaultConstructor);

        assertThat(factory.getAllInstantiableActionClasses())
                .containsExactlyInAnyOrderElementsOf(instantiableActionClasses);
    }

    @Test
    @DisplayName("Should return a set of all public action classes")
    void getPublicActionClasses() {
        final Set<Class<? extends Action>> publicActionClasses = new HashSet<>();
        publicActionClasses.addAll(instantiableNonRoleActionClassesWithPublicDefaultConstructor);
        publicActionClasses.addAll(instantiableRoleActionClassesWithPublicDefaultConstructor);
        publicActionClasses.addAll(nonInstantiableActionClasses);

        assertThat(factory.getPublicActionClasses())
                .containsExactlyInAnyOrderElementsOf(publicActionClasses);
    }

    @Test
    @DisplayName("Should return a list of instances of the given action classes")
    void createActions() {
        final Collection<Class<? extends Action>> actionClasses = List.of(
                CharterFlightAction.class,
                DirectFlightAction.class,
                ShuttleFlightAction.class
        );

        final List<Action> actions = List.of(
                new CharterFlightAction(),
                new DirectFlightAction(),
                new ShuttleFlightAction()
        );

        assertThat(factory.createActions(actionClasses))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(actions);
    }

    @Test
    @DisplayName("Should return a list of instances of the given action classes ignoring action classes that cannot be instantiated")
    void createActions_withError() {
        final Collection<Class<? extends Action>> actionClasses = List.of(
                CharterFlightAction.class,
                DirectFlightAction.class,
                ShuttleFlightAction.class,

                Action.class
        );

        final List<Action> actions = List.of(
                new CharterFlightAction(),
                new DirectFlightAction(),
                new ShuttleFlightAction()
        );

        assertThat(factory.createActions(actionClasses))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(actions);
    }

    @Test
    @DisplayName("Should return an instance of the given action class")
    void createAction() {
        final Class<? extends Action> actionClass = CarAction.class;

        final Action action = new CarAction();

        assertThat(factory.createAction(actionClass))
                .usingRecursiveComparison()
                .isEqualTo(action);
    }

    @Test
    @DisplayName("Should throw an exception if an exception is throw when trying to instantiate an object of the given action class")
    void createAction_error() {
        final Class<? extends Action> actionClass = Action.class;

        assertThatThrownBy(() -> factory.createAction(actionClass))
                .isInstanceOf(RuntimeException.class);
    }
}