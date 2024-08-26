package de.uol.swp.common.roles;

import lombok.*;

import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleCard {
    private String name;
    private Color color;
    private Image image;
    private RoleAbility ability;
}
