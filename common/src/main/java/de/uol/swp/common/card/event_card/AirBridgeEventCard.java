package de.uol.swp.common.card.event_card;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an Air Bridge event card in the game.
 * This card allows a player to move any game piece to any city.
 * To move another player's piece, permission from that player is required.
 */
@Getter
@Setter
public class AirBridgeEventCard extends EventCard implements Approvable {
    private static final String DESCRIPTION_STRING = "Ziehen Sie eine beliebige Spielfigur in eine beliebige Stadt. Um eine fremde Spielfigur zu bewegen, benötigen Sie die Erlaubnis des Spielers.";
    private static final String TITLE_STRING = "Luftbrücke";

    @Setter(AccessLevel.NONE)
    protected boolean isApproved;
    private Field targetField;
    private Player targetPlayer;

    /**
     * Constructs an AirBridgeEventCard with a predefined description and title.
     */
    public AirBridgeEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }

    @Override
    public void trigger() {
        if (isApproved) {
            targetPlayer.setCurrentField(targetField);
        }
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
        return "Möchtest du zulassen, dass " + player + " dich auf Feld " + targetField + " verschiebt?";
    }

    @Override
    public String getApprovedMessage() {
        return targetPlayer + " hat die Verschiebung auf Feld " + targetField + " angenommen.";
    }

    @Override
    public String getRejectedMessage() {
        return targetPlayer + " hat die Verschiebung auf Feld " + targetField + " abgelehnt.";
    }

    @Override
    public String getEffectMessage() {
        return targetPlayer + " wurde auf Feld " + targetField + " verschoben.";
    }

    @Override
    public void initWithGame(final Game game) {
        super.initWithGame(game);
        this.targetField = this.game.findField(this.targetField).orElseThrow();
        this.targetPlayer = this.game.findPlayer(this.targetPlayer).orElseThrow();
    }
}
