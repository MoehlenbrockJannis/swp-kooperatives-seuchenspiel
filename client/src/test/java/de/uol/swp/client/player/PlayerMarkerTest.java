package de.uol.swp.client.player;

import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.map.CityMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerMarkerTest {

    @Mock
    private LoggedInUserProvider loggedInUserProvider;
    @Mock
    private Game game;
    @Mock
    private ActionService actionService;
    @Mock
    private Player player;
    @Mock
    private Field field;
    @Mock
    private EventBus eventBus;

    private PlayerMarker playerMarker;
    private List<CityMarker> cityMarkers;
    private static final double PLAYER_SIZE = 1.0;
    private static final Color PLAYER_COLOR = Color.RED;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cityMarkers = new ArrayList<>();
        actionService = new ActionService(eventBus);
        playerMarker = new PlayerMarker(
                PLAYER_COLOR,
                PLAYER_SIZE,
                loggedInUserProvider,
                player,
                game
        );
    }

    @Test
    @DisplayName("Default player marker dimensions should be 40x20")
    void shouldHaveCorrectDimensions() {
        assertThat(playerMarker.getHeight())
                .as("Player marker height should be 40.0 with default size")
                .isEqualTo(40.0);

        assertThat(playerMarker.getWidth())
                .as("Player marker width should be 20.0 with default size")
                .isEqualTo(20.0);
    }

    @Test
    @DisplayName("Shapes should have correct fill and stroke colors")
    void shouldHaveCorrectColor() {
        List<Node> children = playerMarker.getChildren();
        assertThat(children)
                .as("All nodes should be of type Shape")
                .allMatch(node -> node instanceof Shape);

        List<Shape> shapes = playerMarker.getChildren()
                .stream()
                .map(node -> (Shape) node)
                .collect(Collectors.toList());

        assertThat(shapes)
                .as("All shapes should have the specified player color")
                .allMatch(shape -> shape.getFill().equals(PLAYER_COLOR));

        assertThat(shapes)
                .as("All shapes should have black stroke")
                .allMatch(shape -> shape.getStroke().equals(Color.BLACK));
    }
}