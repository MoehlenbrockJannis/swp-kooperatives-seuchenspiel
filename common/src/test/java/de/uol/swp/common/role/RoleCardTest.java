package de.uol.swp.common.role;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleCardTest {
    private RoleCard roleCard;
    private String name;
    private Color color;
    private RoleAbility ability;

    @BeforeEach
    void setUp() {
        name = "elor";
        color = new Color(255, 254, 253);
        ability = mock(RoleAbility.class);

        roleCard = new RoleCard(name, color, ability);
    }

    @Test
    @DisplayName("Should return the given name")
    void testToString() {
        assertThat(roleCard)
                .hasToString(name);
    }

    @Test
    @DisplayName("Should return true if objects contain the same name")
    void testEquals_true() {
        final RoleCard equal = new RoleCard(name, new Color(), null);

        assertThat(roleCard.equals(equal))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if objects do not contain the same name")
    void testEquals_falseDifferentObject() {
        final RoleCard notEqual = new RoleCard(name + name, new Color(), null);

        assertThat(roleCard.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if objects are of different types")
    void testEquals_falseDifferentType() {
        final Object notEqual = new Object();

        assertThat(roleCard.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return a hash of the name")
    void testHashCode() {
        assertThat(roleCard.hashCode())
                .isEqualTo(Objects.hashCode(name));
    }

    @Test
    @DisplayName("Should return the given name")
    void getTitle() {
        assertThat(roleCard.getTitle())
                .isEqualTo(name);
    }

    @Test
    @DisplayName("Should return the result of getRoleSpecificActionClassOrDefault() method on associated ability")
    void getRoleSpecificActionClassOrDefault() {
        final Class<? extends Action> input = Action.class;
        final Class<? extends Action> output = RoleAction.class;

        Mockito.<Class<? extends Action>>when(ability.getRoleSpecificActionClassOrDefault(input))
                .thenReturn(output);

        assertThat(roleCard.getRoleSpecificActionClassOrDefault(input))
                .isEqualTo(output);
    }

    @Test
    @DisplayName("Should return the result of getRoleSpecificAdditionallyAvailableActionClasses() method on associated ability")
    void getRoleSpecificAdditionallyAvailableActionClasses() {
        final Set<Class<? extends RoleAction>> output = Set.of(
                RoleAction.class
        );

        when(ability.getRoleSpecificAdditionallyAvailableActionClasses())
                .thenReturn(output);

        assertThat(roleCard.getRoleSpecificAdditionallyAvailableActionClasses())
                .isEqualTo(output);
    }

    @Test
    @DisplayName("Should return the result of getRoleSpecificUnavailableActionClasses() method on associated ability")
    void getRoleSpecificUnavailableActionClasses() {
        final Set<Class<? extends GeneralAction>> output = Set.of(
                GeneralAction.class
        );

        when(ability.getRoleSpecificUnavailableActionClasses())
                .thenReturn(output);

        assertThat(roleCard.getRoleSpecificUnavailableActionClasses())
                .isEqualTo(output);
    }

    @Test
    @DisplayName("Should return the given name")
    void getName() {
        assertThat(roleCard.getName())
                .isEqualTo(name);
    }

    @Test
    @DisplayName("Should return the given color")
    void getColor() {
        assertThat(roleCard.getColor())
                .usingRecursiveComparison()
                .isEqualTo(color);
    }

    @Test
    @DisplayName("Should return the given ability")
    void getAbility() {
        assertThat(roleCard.getAbility())
                .isEqualTo(ability);
    }

    @Test
    @DisplayName("Should set the given name")
    void setName() {
        assertThat(roleCard.getName())
                .isEqualTo(name);

        final String newName = "new name";
        roleCard.setName(newName);

        assertThat(roleCard.getName())
                .isEqualTo(newName);
    }

    @Test
    @DisplayName("Should set the given color")
    void setColor() {
        assertThat(roleCard.getColor())
                .usingRecursiveComparison()
                .isEqualTo(color);

        final Color newColor = new Color();
        roleCard.setColor(newColor);

        assertThat(roleCard.getColor())
                .usingRecursiveComparison()
                .isEqualTo(newColor);
    }

    @Test
    @DisplayName("Should set the given ability")
    void setAbility() {
        assertThat(roleCard.getAbility())
                .isEqualTo(ability);

        final RoleAbility newAbility = mock(RoleAbility.class);
        roleCard.setAbility(newAbility);

        assertThat(roleCard.getAbility())
                .isEqualTo(newAbility);
    }
}