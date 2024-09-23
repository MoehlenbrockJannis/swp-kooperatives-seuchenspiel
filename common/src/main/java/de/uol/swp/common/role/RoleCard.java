package de.uol.swp.common.role;

import de.uol.swp.common.card.Card;
import de.uol.swp.common.util.Color;
import lombok.*;

import java.util.Objects;

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

}
