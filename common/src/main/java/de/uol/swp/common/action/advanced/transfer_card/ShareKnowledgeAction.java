package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * The {@code ShareKnowledgeAction} class represents an advanced action where a player shares
 * a {@link CityCard} with another player. It extends {@link AdvancedAction} and implements
 * the {@link Approvable} interface, meaning it requires approval to be executed.
 * <p>
 * This class handles the sharing of knowledge, typically in the form of a city card, between players.
 * </p>
 */
@Getter
@Setter
public abstract class ShareKnowledgeAction extends AdvancedAction implements Approvable {

    public static final String NAME = "Wissen teilen";

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

    @Override
    public String toString() {
        return NAME;
    }

    /**
     * <p>
     *     Returns {@code true} if there is at least one {@link Player} that a {@link CityCard} can be transferred with.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true} if there is at least one {@link Player} that a {@link CityCard} can be transferred with, {@code false} otherwise
     * @see #getTargetPlayersWithAvailableCardsAssociation()
     */
    @Override
    public boolean isAvailable() {
        return !getTargetPlayersWithAvailableCardsAssociation().isEmpty();
    }

    /**
     * <p>
     *     Returns {@code true} if
     *      this {@link ShareKnowledgeAction} is approved and
     *      the {@link #transferredCard} is transferable with the {@link #targetPlayer} and in the sender's hand.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isExecutable() {
        final Map<Player, List<CityCard>> targetPlayersWithAvailableCardsAssociation = getTargetPlayersWithAvailableCardsAssociation();
        return isApproved &&
                targetPlayersWithAvailableCardsAssociation.containsKey(targetPlayer) &&
                targetPlayersWithAvailableCardsAssociation.get(targetPlayer).contains(transferredCard) &&
                getSender().hasHandCard(transferredCard);
    }

    @Override
    public void initWithGame(final Game game) {
        super.initWithGame(game);
        this.targetPlayer = game.findPlayer(this.targetPlayer).orElseThrow();
        this.transferredCard = getSender().getHandCardForGivenPlayerCard(transferredCard);
    }

    @Override
    public Player getApprovingPlayer() {
        return targetPlayer;
    }

    @Override
    public void approve() {
        this.isApproved = true;
    }

    @Override
    public String getApprovalRequestMessage() {
        return getSender() + " möchte " + getReceiver() + " die Karte " + getTransferredCard() + " vermachen.";
    }

    @Override
    public String getApprovedMessage() {
        return getApprovingPlayer() + " hat angenommen. " +  getSender() + " hat " + getReceiver() + " die Karte " + getTransferredCard() + " vermacht.";
    }

    @Override
    public String getRejectedMessage() {
        return getApprovingPlayer() + " hat abgelehnt. " +  getSender() + " hat " + getReceiver() + " die Karte " + getTransferredCard() + " nicht vermacht.";
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
    public abstract Player getSender();

    /**
     * <p>
     *     Returns the receiver of the {@link #transferredCard}.
     * </p>
     *
     * @return receiving {@link Player} of {@link #transferredCard}
     */
    public abstract Player getReceiver();

    /**
     * <p>
     *     Returns a {@link Map} with every {@link Player} and an associated {@link List} of {@link CityCard}
     *     containing all hand cards that can be transferred from or to that {@link Player}.
     *     No {@link List} associated with a {@link Player} may be empty.
     * </p>
     *
     * @return {@link Map} of {@link Player} with associated hand cards that can be transferred
     */
    public abstract Map<Player, List<CityCard>> getTargetPlayersWithAvailableCardsAssociation();

    /**
     * <p>
     *     Returns a {@link List} of {@link Player} containing all players that a card can be transferred from or to.
     * </p>
     *
     * @return @link List} of all players that a card can be transferred from or to
     * @see Game#getPlayersInTurnOrder()
     * @see #isTransferPossibleWithPlayer(Player)
     */
    protected List<Player> getPlayersWithPossibilityOfTransfer() {
        return getGame().getPlayersInTurnOrder().stream()
                .filter(this::isTransferPossibleWithPlayer)
                .toList();
    }

    /**
     * <p>
     *     Determines whether a transfer is possible between given {@code targetPlayer} and {@link #getExecutingPlayer()}.
     * </p>
     *
     * <p>
     *     Returns {@code true} if given {@code targetPlayer} is not equal to {@link #getExecutingPlayer()} and
     *     both share the same current {@link Field}.
     * </p>
     *
     * @param targetPlayer {@link Player} to determine transfer possibility for
     * @return {@code true} if given {@code targetPlayer} and {@link #getExecutingPlayer()} are not equal but share the same current {@link Field}
     * @see Player#equals(Object)
     * @see Player#hasSharedCurrentFieldWith(Player)
     */
    protected boolean isTransferPossibleWithPlayer(final Player targetPlayer) {
        return !targetPlayer.equals(getExecutingPlayer()) && getExecutingPlayer().hasSharedCurrentFieldWith(targetPlayer);
    }

    /**
     * <p>
     *     Returns the {@link Class} object of the class that the given {@link Player} would use to send a {@link CityCard}.
     * </p>
     *
     * @param player {@link Player} to get {@link SendCardAction} of
     * @return the {@link SendCardAction} class for the sending opponent
     */
    @SuppressWarnings("unchecked")
    protected Class<? extends SendCardAction> getSendCardActionClass(final Player player) {
        return (Class<? extends SendCardAction>) player.getRoleSpecificActionClassOrDefault(SendCardAction.class);
    }

    /**
     * <p>
     *     Returns the {@link Class} object of the class that the given {@link Player} would use to receive a {@link CityCard}.
     * </p>
     *
     * @param player {@link Player} to get {@link ReceiveCardAction} of
     * @return the {@link ReceiveCardAction} class for the receiving opponent
     */
    @SuppressWarnings("unchecked")
    protected Class<? extends ReceiveCardAction> getReceiveCardActionClass(final Player player) {
        return (Class<? extends ReceiveCardAction>) player.getRoleSpecificActionClassOrDefault(ReceiveCardAction.class);
    }

    /**
     * <p>
     *     Returns a {@link ShareKnowledgeAction} of type {@code T} with the executing {@link Player} and {@link Game} set.
     * </p>
     *
     * @param clazz the {@link Class} of the created {@link ShareKnowledgeAction}
     * @param executingPlayer the executing {@link Player} of the created {@link ShareKnowledgeAction}
     * @param game the {@link Game} of the created {@link ShareKnowledgeAction}
     * @return a {@link ShareKnowledgeAction} of type {@code T}
     * @param <T> the type of the created {@link ShareKnowledgeAction}
     * @see #createShareKnowledgeAction(Class)
     * @see ShareKnowledgeAction#setExecutingPlayer(Player)
     * @see ShareKnowledgeAction#setGame(Game)
     */
    protected <T extends ShareKnowledgeAction> T createShareKnowledgeAction(final Class<T> clazz, final Player executingPlayer, final Game game) {
        final T action = createShareKnowledgeAction(clazz);
        action.setExecutingPlayer(executingPlayer);
        action.setGame(game);
        return action;
    }

    /**
     * <p>
     *     Returns a {@link ShareKnowledgeAction} of type {@code T}.
     * </p>
     *
     * <p>
     *     Creates the {@link ShareKnowledgeAction} using a {@code public} default constructor.
     *     Throws an {@link IllegalStateException} if any {@link Exception} occurs during instantiation.
     * </p>
     *
     * @param clazz the {@link Class} of the created {@link ShareKnowledgeAction}
     * @return a {@link ShareKnowledgeAction} of type {@code T}
     * @param <T> the type of the created {@link ShareKnowledgeAction}
     * @throws IllegalStateException if an {@link Exception} occurs during instantiation
     */
    private <T extends ShareKnowledgeAction> T createShareKnowledgeAction(final Class<T> clazz) throws IllegalStateException {
        if (clazz == null) {
            throw new IllegalArgumentException("The class parameter cannot be null");
        }
        try {
            return clazz.getConstructor().newInstance();
        } catch (final Exception e) {
            throw new IllegalStateException("Error creating instance of " + clazz.getName(), e);
        }
    }
}
