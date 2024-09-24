package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.Approvable;
import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The {@code ShareKnowledgeAction} class represents an advanced action where a player shares
 * a {@link CityCard} with another player. It extends {@link AdvancedAction} and implements
 * the {@link Approvable} interface, meaning it requires approval to be executed.
 * <p>
 * This class handles the sharing of knowledge, typically in the form of a city card, between players.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
@Setter
@NoArgsConstructor
public class ShareKnowledgeAction extends AdvancedAction implements Approvable {

    /**
     * The player involved in the knowledge-sharing action.
     */
    private Player player;

    /**
     * The {@link CityCard} that is being shared between players.
     */
    private CityCard cityCard;
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public Player getApprovingPlayer() {
        return null;
    }

    @Override
    public boolean isApproved() {
        return false;
    }

    @Override
    public void approve() {

    }

    @Override
    public void execute() {

    }
}
