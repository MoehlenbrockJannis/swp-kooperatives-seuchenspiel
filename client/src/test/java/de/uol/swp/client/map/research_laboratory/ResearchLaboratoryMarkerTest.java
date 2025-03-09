package de.uol.swp.client.map.research_laboratory;

import de.uol.swp.client.map.GameMapPresenter;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.map.Field;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

class ResearchLaboratoryMarkerTest {

    @Mock
    private Game game;
    @Mock
    private PlayerTurn turn;
    @Mock
    private Pane researchLaboratoryPane;
    @Mock
    private WebView webView;
    @Mock
    private Field field;
    private List<Action> possibleActions;
    @Mock
    private BuildResearchLaboratoryAction buildResearchLaboratoryAction;

    @InjectMocks
    private GameMapPresenter gameMapPresenter;

    private ResearchLaboratoryMarker researchLaboratoryMarker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        possibleActions = new ArrayList<>();
        possibleActions.add(buildResearchLaboratoryAction);
        when(game.getCurrentTurn()).thenReturn(turn);
        when(turn.getPossibleActions()).thenReturn(possibleActions);

        ReadOnlyDoubleProperty widthProperty = new SimpleDoubleProperty();
        ReadOnlyDoubleProperty heightProperty = new SimpleDoubleProperty();

        when(webView.widthProperty()).thenReturn(widthProperty);
        when(webView.heightProperty()).thenReturn(heightProperty);

        when(field.getXCoordinate()).thenReturn(100);
        when(field.getYCoordinate()).thenReturn(200);

        widthProperty.addListener((observable, oldValue, newValue) -> ((DoubleProperty) widthProperty).set(newValue.doubleValue()));
        heightProperty.addListener((observable, oldValue, newValue) -> ((DoubleProperty) heightProperty).set(newValue.doubleValue()));

        researchLaboratoryMarker = new ResearchLaboratoryMarker(0.7);
    }

}