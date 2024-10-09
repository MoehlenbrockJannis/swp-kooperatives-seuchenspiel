package de.uol.swp.common.card;

import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.UserDTO;

import java.util.Objects;

public abstract class PlayerCard extends Card{

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerCard playerCard = (PlayerCard) o;
        return Objects.equals(getTitle(), playerCard.getTitle());
    }
}
