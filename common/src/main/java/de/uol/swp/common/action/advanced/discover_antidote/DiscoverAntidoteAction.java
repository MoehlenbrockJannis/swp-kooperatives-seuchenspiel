package de.uol.swp.common.action.advanced.discover_antidote;

import de.uol.swp.common.action.DiscardCardsAction;
import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.marker.AntidoteMarker;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** The {@code DiscoverAntidoteAction} class represents an advanced action where a player attempts to discover an antidote
 * by discarding a specified number of {@link CityCard}s. It extends {@link AdvancedAction}.
 * <p>
 * The action requires a certain number of cards to be discarded, which is tracked and managed through the methods in this class.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
public class DiscoverAntidoteAction extends AdvancedAction implements DiscardCardsAction {

    /**
     * The number of cards that must be discarded to complete the action.
     */
    private final int requiredAmountOfDiscardedCards;

    /**
     * The list of {@link CityCard}s that have been discarded so far.
     */
    private List<CityCard> discardedCards;

    @Setter
    private Plague plague;

    public DiscoverAntidoteAction() {
        this(5);
    }

    /**
     * Constructs a new {@code DiscoverAntidoteAction} with the required number of discarded cards.
     *
     * @param requiredAmountOfDiscardedCards the number of cards that must be discarded to discover the antidote
     */
    protected DiscoverAntidoteAction(int requiredAmountOfDiscardedCards) {
        this.requiredAmountOfDiscardedCards = requiredAmountOfDiscardedCards;
        this.discardedCards = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Gegenmittel entdecken";
    }

    /**
     * Adds a {@link CityCard} to the list of discarded cards if the required amount has not been reached.
     *
     * @param card the {@link CityCard} to be discarded
     * @throws IllegalStateException if the card is already discarded
     * @throws IllegalStateException if the card is not of the same {@link Plague} as required
     * @throws IllegalStateException if the required amount of discarded cards is already reached
     */
    public void addDiscardedCard(CityCard card) {
        if (discardedCards.contains(card)) {
            throw new IllegalStateException("Card is already discarded.");
        }
        if (discardedCards.size() < requiredAmountOfDiscardedCards) {
            if (card.hasPlague(plague)) {
                discardedCards.add(card);
            } else {
                throw new IllegalStateException("The given Card is not of the same Plague as required.");
            }
        } else {
            throw new IllegalStateException("Maximum number of discarded cards reached.");
        }
    }

    /**
     * <p>
     *     Returns {@code true} if
     *      the current field of the executing {@link Player} has a {@link ResearchLaboratory} and
     *      the executing {@link Player} has greater than or equal to {@link #requiredAmountOfDiscardedCards} hand cards of any {@link Plague} on {@link Game} and
     *      the {@link AntidoteMarker} for that {@link Plague} is not discovered yet.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true} if executing {@link Player} is on {@link Field} with {@link ResearchLaboratory} and has enough hand cards of a {@link Plague} and the {@link AntidoteMarker} to that {@link Plague} has not been discovered yet, {@code false} otherwise
     * @see #getExecutingPlayer()
     * @see Player#getCurrentField()
     * @see Player#getHandCards()
     * @see Field#hasResearchLaboratory()
     * @see Game#getPlagues()
     * @see Game#hasAntidoteMarkerForPlague(Plague)
     * @see CityCard#hasPlague(Plague)
     */
    @Override
    public boolean isAvailable() {
        final Player player = getExecutingPlayer();
        final Field currentField = player.getCurrentField();
        if (!currentField.hasResearchLaboratory()) {
            return false;
        }

        return !getAvailablePlagues().isEmpty();
    }

    /**
     * This method returns a list of plagues that are currently available based on the number of
     * discardable cards required to activate a plague.
     *
     * @return A list of plagues that are available.
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    public List<Plague> getAvailablePlagues() {
        List<Plague> plaguesFromGame = getGame().getPlagues();
        return plaguesFromGame.stream()
                .filter(plague -> getDiscardableCardsForPlague(plague).size() >= requiredAmountOfDiscardedCards)
                .toList();
    }

    /**
     * This method returns a list of CityCard objects that can be discarded to activate a specific plague.
     *
     * @param plague The plague for which the discardable cards are being retrieved.
     * @return A list of CityCards that can be discarded for the given plague.
     *
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    public List<CityCard> getDiscardableCardsForPlague(final Plague plague) {
        List<PlayerCard> handCardsFromExecutingPlayer = getExecutingPlayer().getHandCards();
        return handCardsFromExecutingPlayer.stream()
                .filter(card -> card instanceof CityCard cityCard && cityCard.hasPlague(plague))
                .map(CityCard.class::cast)
                .toList();
    }

    /**
     * <p>
     *     A {@link DiscoverAntidoteAction} is executable if
     *      it is available and
     *      the amount of discarded cards equals {@link #requiredAmountOfDiscardedCards} and
     *      the associated {@link Game} does not have an {@link AntidoteMarker} to {@link #plague}.
     * </p>
     *
     * @return {@code true} if
     *   is available and
     *   the required amount of cards has been discarded and
     *   the {@link Game} does not have an {@link AntidoteMarker} for {@link #plague},
     *  {@code false} otherwise
     * @see #isAvailable()
     * @see #discardedCards
     * @see #requiredAmountOfDiscardedCards
     * @see #plague
     * @see Game#hasAntidoteMarkerForPlague(Plague)
     */
    @Override
    public boolean isExecutable() {
        return isAvailable() && discardedCards.size() == requiredAmountOfDiscardedCards && !getGame().hasAntidoteMarkerForPlague(plague);
    }

    /**
     * <p>
     *     If this {@link DiscoverAntidoteAction} is executable,
     *     adds an {@link AntidoteMarker} to {@link #plague} to the associated {@link Game}.
     * </p>
     *
     * @throws IllegalStateException if the {@link DiscoverAntidoteAction} is not executable
     * @see #isExecutable()
     * @see AntidoteMarker
     * @see Game#addAntidoteMarker(AntidoteMarker)
     */
    @Override
    public void execute() {
        if (!isExecutable()) {
            throw new IllegalStateException("This Action may not be executed.");
        }

        getGame().addAntidoteMarker(new AntidoteMarker(plague));
    }

    @Override
    public void initWithGame(Game game) {
        super.initWithGame(game);
        this.discardedCards = getUpdatedDiscardedCards();
    }

    private List<CityCard> getUpdatedDiscardedCards() {
        return discardedCards.stream()
                .map(card -> getExecutingPlayer().getHandCardForGivenPlayerCard(card))
                .toList();
    }
}
