package de.uol.swp.common.roles;

import lombok.*;

import java.awt.*;


/**
 * This class is used to define a RoleCard.
 */

@Getter
@AllArgsConstructor
@ToString
public class RoleCard {
    private String name;
    private Color color;
    private Image image;
    private RoleAbility ability;
}
