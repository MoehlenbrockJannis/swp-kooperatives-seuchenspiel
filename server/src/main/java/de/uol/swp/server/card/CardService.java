package de.uol.swp.server.card;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.card.EpidemicCard;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.card.request.DiscardInfectionCardRequest;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.card.request.DrawInfectionCardRequest;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.card.response.DrawPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDiscardPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDrawInfectionCardResponse;
import de.uol.swp.common.card.response.ReleaseToDrawPlayerCardResponse;
import de.uol.swp.common.card.server_message.DrawInfectionCardServerMessage;
import de.uol.swp.common.card.server_message.EpidemicCardDrawnServerMessage;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.response.AbstractGameResponse;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.player.turn.request.EndPlayerTurnRequest;
import de.uol.swp.common.triggerable.Triggerable;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.chat.message.SystemLobbyMessageServerInternalMessage;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.triggerable.TriggerableService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Service class responsible for handling card-related operations on the server side.
 * <p>
 * This class extends the {@link AbstractService} and provides functionality to handle
 * requests related to drawing player cards, updating game states, and communicating
 * with the lobby service.
 * </p>
 *
 */
@Singleton
public class CardService extends AbstractService {

    private final CardManagement cardManagement;
    private final GameManagement gameManagement;
    private final LobbyService lobbyService;
    private final TriggerableService triggerableService;

    /**
     * Constructor
     *
     * @param eventBus the EvenBus used throughout the server
     * @param cardManagement the {@link CardManagement} to execute operations on cards
     * @param gameManagement the GameManagement used to manage games
     * @param lobbyService the LobbyService used to communicate with the lobby
     * @param triggerableService the {@link TriggerableService} to check if a {@link Triggerable} can be triggered
     * @since 2019-10-08
     */
    @Inject
    public CardService(EventBus eventBus, CardManagement cardManagement, GameManagement gameManagement, LobbyService lobbyService, TriggerableService triggerableService) {
        super(eventBus);
        this.cardManagement = cardManagement;
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
        this.triggerableService = triggerableService;
    }

    /**
     * Handles the DrawPlayerCardRequest.
     * <p>
     * This method processes the DrawPlayerCardRequest by retrieving the game from the GameManagement,
     * drawing a card for the current player, updating the game state, and sending the updated game
     * state to all players in the lobby.
     *
     * @see DrawPlayerCardRequest
     * @param drawPlayerCardRequest the DrawPlayerCardRequest containing the game and player information
     * @since 2024-09-20
     * @author Dominik Horn
     */
    @Subscribe
    public void onDrawPlayerCardRequest(DrawPlayerCardRequest drawPlayerCardRequest) {
        getGameAndPlayer(drawPlayerCardRequest,(game, player) -> {
            if(game.getPlayerDrawStack().isEmpty()) {
                //TODO: Send response that the game has been lost
                return;
            }

            final PlayerCard playerCard = gameManagement.drawPlayerCard(game);

            if(playerCard instanceof EpidemicCard epidemicCard) {
                triggerEpidemic(game, epidemicCard);
            } else {
                player.addHandCard(playerCard);
            }

            final PlayerTurn playerTurn = game.getCurrentTurn();
            playerTurn.reduceNumberOfPlayerCardsToDraw();
            playerTurn.createTriggerables();

            DrawPlayerCardResponse response = new DrawPlayerCardResponse(playerCard, game);
            response.initWithMessage(drawPlayerCardRequest);
            post(response);

            determineFollowingStepAndSendUpdate(game, player, drawPlayerCardRequest);
        });
    }

    /**
     * Handles the DiscardPlayerCardRequest.
     * <p>
     * This method processes the DiscardPlayerCardRequest by retrieving the game and player from the GameManagement,
     * checking if the player has the card in their hand, removing the card from the player's hand, discarding the card,
     * updating the game state, and sending the updated game state to all players in the lobby.
     * </p>
     *
     * @param discardPlayerCardRequest the DiscardPlayerCardRequest containing the game and player card information
     * @since 2024-09-27
     */
    @Subscribe
    public void onDiscardPlayerCardRequest(DiscardPlayerCardRequest<PlayerCard> discardPlayerCardRequest) {
        getGameAndPlayer(discardPlayerCardRequest,(game, player) -> {
            final PlayerCard cardToDiscard = discardPlayerCardRequest.getCard();
            if(player.hasHandCard(cardToDiscard)) {
                final PlayerCard playerCard = player.getHandCardForGivenPlayerCard(cardToDiscard);
                player.removeHandCard(playerCard);
                cardManagement.discardPlayerCard(game, playerCard);

                sendPlayerCardDiscardMessage(playerCard, player, game);
            }

            determineFollowingStepAndSendUpdate(game, player, discardPlayerCardRequest);
        });
    }

    /**
     * Sends a message informing the players of given {@link Game}
     * about the discarding of given {@link PlayerCard} by given {@link Player}.
     *
     * @param playerCard discarded {@link PlayerCard}
     * @param player {@link Player} discarding {@link PlayerCard}
     * @param game {@link Game} in which the {@link PlayerCard} is discarded
     */
    private void sendPlayerCardDiscardMessage(final PlayerCard playerCard, final Player player, final Game game) {
        String message = String.format("%s hat \"%s\" abgeworfen.", player.getName(), playerCard.getTitle());
        if (playerCard instanceof EventCard eventCard) {
            message += " " + eventCard.getEffectMessage();
        }
        SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage =
                new SystemLobbyMessageServerInternalMessage(message, game.getLobby());
        post(systemLobbyMessageServerInternalMessage);
    }

    /**
     * Handles the DrawInfectionCardRequest.
     * <p>
     * This method processes the DrawInfectionCardRequest by retrieving the game and player from the GameManagement,
     * drawing an infection card from the top of the infection draw stack, sending a message to all players in the lobby,
     * and updating the game state.
     * </p>
     *
     * @param drawInfectionCardRequest the DrawInfectionCardRequest containing the game and player information
     */
    @Subscribe
    public void onDrawInfectionCardRequest(DrawInfectionCardRequest drawInfectionCardRequest) {
        getGameAndPlayer(drawInfectionCardRequest,(game, player) -> {
            final PlayerTurn playerTurn = game.getCurrentTurn();
            if (!playerTurn.isInInfectionCardDrawPhase()) {
                determineFollowingStepAndSendUpdate(game, player, drawInfectionCardRequest);
                return;
            }

            InfectionCard infectionCard = cardManagement.drawInfectionCardFromTheTop(game);
            playerTurn.reduceNumberOfInfectionCardsToDraw();
            cardManagement.discardInfectionCard(game, infectionCard);

            DrawInfectionCardServerMessage message = new DrawInfectionCardServerMessage(infectionCard, game);
            lobbyService.sendToAllInLobby(game.getLobby(), message);

            List<Field> infectedFields = new ArrayList<>();
            handleInfectionProcess(game, infectionCard, infectedFields);

            sendGameUpdateMessage(game);
        });
    }

    /**
     * Handles the infection process.
     * <p>
     * This method infects the field associated with the infection card and discards the infection card afterward.
     * </p>
     *
     * @param game the game in which the infection process is taking place
     * @param infectionCard the infection card to be processed
     * @param infectedFields list to track which fields got infected
     */
    private void handleInfectionProcess(Game game, InfectionCard infectionCard, List<Field> infectedFields) {
        Field associatedField = infectionCard.getAssociatedField();
        PlagueCube plagueCube = game.getPlagueCubeOfPlague(associatedField.getPlague());

        processInfection(game, associatedField, plagueCube, infectedFields);

        PlayerTurn currentTurn = game.getCurrentTurn();
        List<List<Field>> infectedFieldsInTurn = currentTurn.getInfectedFieldsInTurn();
        infectedFieldsInTurn.add(infectedFields);
    }

    /**
     * Processes the infection of a field, either directly infecting it or starting an outbreak.
     *
     * @param game the game in which the infection takes place
     * @param field the field to be infected
     * @param plagueCube the plague cube to be placed
     * @param infectedFields list to track which fields got infected
     */
    private void processInfection(Game game, Field field, PlagueCube plagueCube, List<Field> infectedFields) {
        game.getMap().setOutbreakCallback((g, f) -> sendOutbreakMessage(f, g));

        if (field.isInfectable(field.getPlague())) {
            field.infectField(plagueCube, infectedFields);
        } else {
            game.getMap().startOutbreak(field, field.getPlague(), infectedFields);
        }
    }

    /**
     * Handles the DiscardInfectionCardRequest.
     * <p>
     * This method processes the DiscardInfectionCardRequest by retrieving the game and player from the GameManagement,
     * discarding the infection card, updating the game state, and sending the updated game state to all players in the lobby.
     * </p>
     *
     * @param discardInfectionCardRequest the DiscardInfectionCardRequest containing the game and infection card information
     */
    @Subscribe
    public void onDiscardInfectionCardRequest(DiscardInfectionCardRequest discardInfectionCardRequest) {
        getGameAndPlayer(discardInfectionCardRequest,(game, player) -> {
            final InfectionCard infectionCard = discardInfectionCardRequest.getCard();
            cardManagement.discardInfectionCard(game, infectionCard);

            sendGameUpdateMessage(game);

        });
    }

    /**
     * Sends an updated game state message to all players in the lobby.
     * <p>
     * This method updates the game state using the GameManagement service and then creates a
     * RetrieveUpdatedGameMessage. It sends this message to all players in the lobby associated
     * with the game using the LobbyService.
     * </p>
     *
     * @param game the game whose updated state is to be sent
     */
    private void sendGameUpdateMessage(Game game) {
        gameManagement.updateGame(game);

        RetrieveUpdatedGameServerMessage message = new RetrieveUpdatedGameServerMessage(game);
        lobbyService.sendToAllInLobby(game.getLobby(), message);
    }

    /**
     * Sends a response to release the card draw.
     *
     * <p>
     * This method creates a response with the given game and number of cards to draw,
     * initializes it with the provided request message, and posts the response to the event bus.
     * </p>
     *
     * @param <T> the type of the response
     * @param game the game for which the response is being sent
     * @param target the {@link Message} to which the response is being sent
     * @param responseSupplier a supplier that provides the response instance
     */
    public <T extends AbstractGameResponse> void sendReleaseToDrawCardResponse(Game game, Message target, Function<Game, T> responseSupplier) {
        T response = responseSupplier.apply(game);
        response.initWithMessage(target);
        post(response);
    }

    /**
     * Retrieves the game and the current player, then executes the provided callback.
     * <p>
     * This method retrieves the game from the GameManagement service using the game ID from the
     * provided AbstractGameRequest. It then retrieves the current player from the game and
     * executes the provided BiConsumer callback with the game and player as arguments.
     * </p>
     *
     * @param gameRequest the request containing the game ID
     * @param callback the callback to be executed with the retrieved game and player
     */
    private void getGameAndPlayer(AbstractGameRequest gameRequest, BiConsumer<Game, Player> callback) {
        final Optional<Game> gameOptional = gameManagement.getGame(gameRequest.getGame());
        if(gameOptional.isEmpty()) return;
        final Game game = gameOptional.get();

        final Optional<Player> playerOptional = game.findPlayer(gameRequest.getPlayer());
        if (playerOptional.isEmpty()) return;
        final Player player = playerOptional.get();

        if (triggerableService.checkForSendingManualTriggerables(game, gameRequest, player)) {
            return;
        }
        callback.accept(game,player);
    }

    /**
     * Allows the client of given {@code originMessage} to draw or discard a card.
     *
     * @param game {@link Game} with the current {@link PlayerTurn}
     * @param originMessage {@link Message} with the context to send allow drawing or discarding of a card to
     * @param responseMessage the response message to send
     */
    public void allowDrawingOrDiscarding(final Game game, final Message originMessage, Class<? extends AbstractGameResponse> responseMessage) {
        sendReleaseToDrawCardResponse(
                game,
                originMessage,
                g -> {
                    try {
                        return responseMessage.getConstructor(Game.class).newInstance(g);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                }
        );
    }

    /**
     * Returns whether the given {@link Player} needs to discard at least one hand card as defined by the ruleset of the given {@link Game}.
     *
     * @param player {@link Player} to check hand card size of
     * @param game {@link Game} of which the ruleset is to follow
     * @return {@code true} if given {@link Player} has more than the maximally allowed hand cards as specified by {@link Game}
     * @see Player#getHandCards()
     * @see Game#getMaxHandCards()
     */
    private boolean doesPlayerRequireDiscardingOfHandCards(final Player player, final Game game) {
        return player.getHandCards().size() > game.getMaxHandCards();
    }

    /**
     * Determines what step of the following four should follow next:
     * 1. Discard a {@link PlayerCard}
     * 2. Draw a {@link PlayerCard}
     * 3. Draw an {@link InfectionCard}
     * 4. End {@link PlayerTurn}
     *
     * @param game {@link Game} for which to determine the next step
     * @param player current {@link Player}
     * @param origin {@link Message} with the origin of the invocation
     */
    private void determineFollowingStepAndSendUpdate(final Game game, final Player player, final Message origin) {
        final PlayerTurn playerTurn = game.getCurrentTurn();
        playerTurn.setAreInteractionsBlocked(false);

        if (doesPlayerRequireDiscardingOfHandCards(player, game)) {
            playerTurn.setAreInteractionsBlocked(true);
            allowDrawingOrDiscarding(game, origin, ReleaseToDiscardPlayerCardResponse.class);
        } else if (playerTurn.isPlayerCardDrawExecutable()) {
            allowDrawingOrDiscarding(game, origin, ReleaseToDrawPlayerCardResponse.class);
        } else if (playerTurn.isInfectionCardDrawExecutable()) {
            allowDrawingOrDiscarding(game, origin, ReleaseToDrawInfectionCardResponse.class);
        } else if (playerTurn.isOver()) {
            endPlayerTurn(game, origin);
        }

        sendGameUpdateMessage(game);
    }

    /**
     * Ends and starts a new {@link PlayerTurn} in the given {@link Game}
     * by sending an {@link EndPlayerTurnRequest} with context of given origin {@link Message}.
     *
     * @param game {@link Game} to start a new {@link PlayerTurn} in
     * @param origin origin {@link Message} from which the ending of the {@link PlayerTurn} is requested
     */
    private void endPlayerTurn(final Game game, final Message origin) {
        final EndPlayerTurnRequest endPlayerTurnRequest = new EndPlayerTurnRequest(game);
        endPlayerTurnRequest.initWithMessage(origin);
        post(endPlayerTurnRequest);
    }

    /**
     * Handles the epidemic event in the game by performing three steps:
     * 1. Increases the infection rate
     * 2. Infects the city from the bottom card with three plague cubes
     * 3. Reshuffles the infection discard pile and places it on top of the draw pile
     *
     * @param game the current game state
     */
    private void triggerEpidemic(Game game, EpidemicCard epidemicCard) {
        game.increaseInfectionLevel();
        processBottomInfectionCardEpidemicBehavior(game);
        reshuffleInfectionDiscardPileOntoDrawPile(game);
        post(new EpidemicCardDrawnServerMessage(game, epidemicCard));
    }

    /**
     * Draws the bottom card from the infection deck and triggers its processing
     * through the standard infection card drawing mechanism.
     *
     * @param game the current game state
     */
    private void processBottomInfectionCardEpidemicBehavior(Game game) {
        InfectionCard bottomCard = cardManagement.drawInfectionCardFromTheBottom(game);
        Field field = bottomCard.getAssociatedField();

        if (!game.hasAntidoteMarkerForPlague(field.getPlague())) {
            List<Field> infectedFields = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                handleInfectionProcess(game, bottomCard, infectedFields);
            }
        }

        cardManagement.discardInfectionCard(game, bottomCard);
    }

    /**
     * Takes all cards from the infection discard pile, shuffles them,
     * and places them on top of the infection draw pile during an epidemic.
     *
     * @param game the current game state
     */
    private void reshuffleInfectionDiscardPileOntoDrawPile(Game game) {
        CardStack<InfectionCard> discardStack = game.getInfectionDiscardStack();
        discardStack.shuffle();

        for (InfectionCard card : discardStack) {
            game.getInfectionDrawStack().push(card);
        }
        discardStack.clear();
    }

    /**
     * Sends a message informing players about an outbreak in the given city.
     *
     * @param field Field where the outbreak occurred
     * @param game Game in which the outbreak occurred
     */
    private void sendOutbreakMessage(final Field field, final Game game) {
        String message = String.format("Ausbruch in %s!!!", field.getCity().getName());
        SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage = new SystemLobbyMessageServerInternalMessage(message, game.getLobby());
        post(systemLobbyMessageServerInternalMessage);
    }
}
