package de.uol.swp.server.card;

import com.google.inject.Inject;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.request.DiscardInfectionCardRequest;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.card.request.DrawInfectionCardRequest;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.card.response.DrawPlayerCardResponse;
import de.uol.swp.common.card.server_message.DrawInfectionCardServerMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Service class responsible for handling card-related operations on the server side.
 * <p>
 * This class extends the {@link AbstractService} and provides functionality to handle
 * requests related to drawing player cards, updating game states, and communicating
 * with the lobby service.
 * </p>
 *
 */
public class CardService extends AbstractService {

    private final GameManagement gameManagement;
    private final LobbyService lobbyService;
    /**
     * Constructor
     *
     * @param eventBus the EvenBus used throughout the server
     * @param gameManagement the GameManagement used to manage games
     * @param lobbyService the LobbyService used to communicate with the lobby
     * @since 2019-10-08
     */
    @Inject
    public CardService(EventBus eventBus, GameManagement gameManagement, LobbyService lobbyService) {
        super(eventBus);
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
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

            if (player.getHandCards().size()  >= game.getMaxHandCards()) {
                //TODO: Send response that the player has to discard a card
            }

            final PlayerCard playerCard = gameManagement.drawPlayerCard(game);
            player.addHandCard(playerCard);

            DrawPlayerCardResponse response = new DrawPlayerCardResponse(playerCard, game);
            response.initWithMessage(drawPlayerCardRequest);
            post(response);

            sendGameUpdateMessage(game);
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
            final PlayerCard playerCard = discardPlayerCardRequest.getCard();
            if(!player.getHandCards().contains(playerCard)) {
                return;
            }
            player.removeHandCard(playerCard);
            gameManagement.discardPlayerCard(game, playerCard);

            sendGameUpdateMessage(game);
        });
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
            InfectionCard infectionCard = gameManagement.drawInfectionCardFromTheTop(game);

            DrawInfectionCardServerMessage message = new DrawInfectionCardServerMessage(infectionCard, game);
            lobbyService.sendToAllInLobby(game.getLobby(), message);

            sendGameUpdateMessage(game);

        });
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
            gameManagement.discardInfectionCard(game, infectionCard);

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
        final Player player = game.getPlayersInTurnOrder().get(game.getIndexOfCurrentPlayer());
        callback.accept(game,player);
    }
}
