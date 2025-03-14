package de.uol.swp.common.card.event_card;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GovernmentSubsidiesEventCardTest {

    private GovernmentSubsidiesEventCard governmentSubsidiesEventCard;
    private Game game;

    @BeforeEach
    void setUp() {
        game = createGame();
        governmentSubsidiesEventCard = createGovernmentSubsidiesEventCard(game);
    }

    @Test
    @DisplayName("Should build a research laboratory with government subsidies without moving another research laboratory")
    void trigger_withoutResearchLaboratoryToBeMoved() {
        governmentSubsidiesEventCard.trigger();

        assertThat(game.getResearchLaboratories())
                .contains(governmentSubsidiesEventCard.getResearchLaboratory())
                .hasSize(2);

        assertThat(game.getFields().get(0).hasResearchLaboratory()).isTrue();
        assertThat(game.getFields().get(10).hasResearchLaboratory()).isTrue();
    }

    @Test
    @DisplayName("Should build a research laboratory with government subsidies with moving another research laboratory")
    void trigger_withResearchLaboratoryToBeMoved() {
        buildAllResearchLaboratories();

        governmentSubsidiesEventCard.setResearchLaboratoryOriginField(game.getFields().get(0));

        governmentSubsidiesEventCard.trigger();

        assertThat(game.getResearchLaboratories())
                .contains(governmentSubsidiesEventCard.getResearchLaboratory())
                .hasSize(Game.DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES);

        assertThat(game.getFields().get(0).hasResearchLaboratory()).isFalse();
        assertThat(game.getFields().get(10).hasResearchLaboratory()).isTrue();
    }

    @Test
    @DisplayName("Should return the effect message, that a research laboratory was built with government subsidies")
    void getEffectMessage() {
        String cityName = game.getFields().get(10).getCity().getName();
        String effectMessage = governmentSubsidiesEventCard.getEffectMessage();

        assertThat(effectMessage)
                .isEqualTo("Es wurde ein neues Forschungslabor in " + cityName + " mit Hilfe von Regierungssubventionen errichtet.");
    }

    private Game createGame() {
        Lobby lobby = new LobbyDTO("name", new UserDTO("user", "", ""));
        lobby.addPlayer(new AIPlayer("ai"));

        GameDifficulty difficulty = GameDifficulty.getDefault();

        return new Game(lobby, TestUtils.createMapType(), new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);
    }

    private GovernmentSubsidiesEventCard createGovernmentSubsidiesEventCard(Game game) {
        GovernmentSubsidiesEventCard card = new GovernmentSubsidiesEventCard();

        card.setGame(game);
        card.setField(game.getFields().get(10));

        return card;
    }

    private void buildAllResearchLaboratories() {
        for (int labIndex = 1; labIndex < Game.DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES; labIndex++) {
            ResearchLaboratory researchLaboratory = new ResearchLaboratory();
            game.getResearchLaboratories().add(researchLaboratory);
            game.getFields().get(labIndex).buildResearchLaboratory(researchLaboratory);
        }
    }
}