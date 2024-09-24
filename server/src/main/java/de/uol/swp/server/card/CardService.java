package de.uol.swp.server.card;

import com.google.inject.Inject;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.card.response.DrawPlayerCardResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;

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
        final Optional<Game> gameOptional = gameManagement.getGame(drawPlayerCardRequest.getGame());
        if(gameOptional.isEmpty()) {
            return;
        }
        final Game game = gameOptional.get();
        if(game.getPlayerDrawStack().isEmpty()) {
            //TODO: Send response that the game has been lost
            return;
        }
        final Player player = game.getPlayersInTurnOrder().get(game.getIndexOfCurrentPlayer());
        final PlayerCard playerCard = gameManagement.drawPlayerCard(game);
        player.addHandCard(playerCard);
        if (player.getHandCards().size() > game.getMaxHandCards()) {
            //TODO: Send response that the player has to discard a card
        }
        gameManagement.updateGame(game);

        DrawPlayerCardResponse response = new DrawPlayerCardResponse(playerCard);
        response.initWithMessage(drawPlayerCardRequest);
        post(response);

        RetrieveUpdatedGameMessage message = new RetrieveUpdatedGameMessage(game);
        lobbyService.sendToAllInLobby(game.getLobby(), message);
    }
}
