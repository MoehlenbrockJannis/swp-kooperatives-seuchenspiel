package de.uol.swp.client.laboratory;

import de.uol.swp.client.game.GameMapPresenter;
import de.uol.swp.client.research_laboratory.ResearchLaboratoryMarker;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.turn.PlayerTurn;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ResearchLaboratoryTest {

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

    @Test
    @DisplayName("Check if the laboratory was added to the pane")
    void addLaboratoryToPane() {
        ObservableList<Node> children = FXCollections.observableArrayList();
        when(researchLaboratoryPane.getChildren()).thenReturn(children);

        gameMapPresenter.buildResearchLaboratoryMarker(researchLaboratoryMarker, field);

        assertThat(children).contains(researchLaboratoryMarker);
        assertThat(researchLaboratoryMarker.layoutXProperty().isBound()).isTrue();
        assertThat(researchLaboratoryMarker.layoutYProperty().isBound()).isTrue();
        assertThat(researchLaboratoryMarker.scaleXProperty().isBound()).isTrue();
        assertThat(researchLaboratoryMarker.scaleYProperty().isBound()).isTrue();
    }
}