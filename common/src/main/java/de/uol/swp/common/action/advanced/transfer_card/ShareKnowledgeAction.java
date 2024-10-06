package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.util.Approvable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
public abstract class ShareKnowledgeAction extends AdvancedAction implements Approvable {

    /**
     * The player involved in the knowledge-sharing action.
     */
    private Player targetPlayer;

    /**
     * The {@link CityCard} that is being shared between players.
     */
    private CityCard transferredCard;

    @Setter(AccessLevel.NONE)
    private boolean isApproved;

    /**
     * <p>
     *     Returns {@code true} if there is at least one {@link Player} in the associated {@link Game}
     *     that stands on the same {@link Field} as the executing {@link Player} and
     *     either {@link Player} owns a {@link CityCard} associated with the current {@link Field}.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true} if there are two players on {@link Game} with a {@link CityCard} associated with current {@link Field} of executing {@link Player} on hand, {@code false} otherwise
     * @see Game#getPlayersInTurnOrder()
     * @see #getExecutingPlayer()
     * @see Player#getCurrentField()
     * @see Player#hasSharedCurrentFieldWith(Player)
     * @see Player#hasHandCardOfField(Field)
     */
    @Override
    public boolean isAvailable() {
        final Player executingPlayer = getExecutingPlayer();
        final Field currentField = executingPlayer.getCurrentField();
        final boolean executingPlayerHasHandCardOfCurrentField = executingPlayer.hasHandCardOfField(currentField);

        final List<Player> players = getGame().getPlayersInTurnOrder();
        for (final Player player : players) {
            if (executingPlayer.hasSharedCurrentFieldWith(player) && (executingPlayerHasHandCardOfCurrentField || player.hasHandCardOfField(currentField))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isExecutable() {
        return isApproved &&
                transferredCard.hasField(getExecutingPlayer().getCurrentField()) &&
                getExecutingPlayer().hasSharedCurrentFieldWith(getTargetPlayer()) &&
                getSender().hasHandCard(transferredCard);
    }

    @Override
    public Player getApprovingPlayer() {
        return targetPlayer;
    }

    @Override
    public void approve() {
        this.isApproved = true;
    }

    /**
     * <p>
     *     If this {@link ShareKnowledgeAction} is executable,
     *     removes the {@link #transferredCard} from the sender's hand and adds it to the receiver's.
     * </p>
     *
     * @throws IllegalStateException if the {@link ShareKnowledgeAction} is not executable
     * @see #isExecutable()
     * @see #transferredCard
     * @see #getSender()
     * @see #getReceiver()
     * @see Player#removeHandCard(PlayerCard)
     * @see Player#addHandCard(PlayerCard)
     */
    @Override
    public void execute() {
        if (!isExecutable()) {
            throw new IllegalStateException("This Action may not be executed.");
        }

        getSender().removeHandCard(transferredCard);
        getReceiver().addHandCard(transferredCard);
    }

    /**
     * <p>
     *     Returns the sender of the {@link #transferredCard}.
     * </p>
     *
     * @return sending {@link Player} of {@link #transferredCard}
     */
    protected abstract Player getSender();

    /**
     * <p>
     *     Returns the receiver of the {@link #transferredCard}.
     * </p>
     *
     * @return receiving {@link Player} of {@link #transferredCard}
     */
    protected abstract Player getReceiver();
}
