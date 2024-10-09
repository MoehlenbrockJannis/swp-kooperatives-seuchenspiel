package de.uol.swp.common.role;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.ReducedCostBuildResearchLaboratoryAction;
import de.uol.swp.common.action.simple.MoveAllyToAllyAction;
import de.uol.swp.common.action.simple.car.CarAction;
import de.uol.swp.common.triggerable.Triggerable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RoleAbilityTest {
    private Map<Class<? extends GeneralAction>, Class<? extends RoleAction>> replacedActions;
    private List<Class<? extends RoleAction>> additionalActions;
    private List<Triggerable> triggerables;
    private RoleAbility roleAbility;

    @BeforeEach
    void setUp() {
        replacedActions = new HashMap<>();
        replacedActions.put(BuildResearchLaboratoryAction.class, ReducedCostBuildResearchLaboratoryAction.class);

        additionalActions = new ArrayList<>();
        additionalActions.add(MoveAllyToAllyAction.class);

        triggerables = new ArrayList<>();
        triggerables.add(mock(Triggerable.class));

        roleAbility = new RoleAbility(replacedActions, additionalActions, triggerables);
    }

    @Test
    @DisplayName("Should return the specified replacement class object if given class object is in replacedActions")
    void getRoleSpecificActionClassOrDefault_isIn() {
        final Class<? extends Action> input = BuildResearchLaboratoryAction.class;
        final Class<? extends Action> output = ReducedCostBuildResearchLaboratoryAction.class;

        assertThat(roleAbility.getRoleSpecificActionClassOrDefault(input))
                .isEqualTo(output);
    }

    @Test
    @DisplayName("Should return the given class object if it is not in replacedActions")
    void getRoleSpecificActionClassOrDefault_isNotIn() {
        final Class<? extends Action> input = CarAction.class;

        assertThat(roleAbility.getRoleSpecificActionClassOrDefault(input))
                .isEqualTo(input);
    }

    @Test
    @DisplayName("Should return a set of RoleAction class objects that are values of either replacedActions or additionalActions")
    void getRoleSpecificAdditionallyAvailableActionClasses() {
        assertThat(roleAbility.getRoleSpecificAdditionallyAvailableActionClasses())
                .containsExactlyInAnyOrderElementsOf(Set.of(
                        ReducedCostBuildResearchLaboratoryAction.class,
                        MoveAllyToAllyAction.class
                ));
    }

    @Test
    @DisplayName("Should return a set of Action class objects that are keys of replacedActions")
    void getRoleSpecificUnavailableActionClasses() {
        assertThat(roleAbility.getRoleSpecificUnavailableActionClasses())
                .containsExactlyInAnyOrderElementsOf(Set.of(
                        BuildResearchLaboratoryAction.class
                ));
    }

    @Test
    @DisplayName("Should return specified replacedActions Map")
    void getReplacedActions() {
        assertThat(roleAbility.getReplacedActions())
                .usingRecursiveComparison()
                .isEqualTo(replacedActions);
    }

    @Test
    @DisplayName("Should return specified additionalActions List")
    void getAdditionalActions() {
        assertThat(roleAbility.getAdditionalActions())
                .usingRecursiveComparison()
                .isEqualTo(additionalActions);
    }

    @Test
    @DisplayName("Should return specified triggerables List")
    void getTriggerables() {
        assertThat(roleAbility.getTriggerables())
                .usingRecursiveComparison()
                .isEqualTo(triggerables);
    }
}