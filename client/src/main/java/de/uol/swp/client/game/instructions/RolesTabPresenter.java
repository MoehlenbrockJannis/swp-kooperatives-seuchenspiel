package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.role.RoleColors;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Presents the roles tab within the {@link GameInstructionsPresenter}.
 *
 */
public class RolesTabPresenter extends AbstractPresenter {

    @FXML
    private StackPane arztPlayerMarkerPane;
    @FXML
    private StackPane betriebsexpertePlayerMarkerPane;
    @FXML
    private StackPane forscherinPlayerMarkerPane;
    @FXML
    private StackPane logistikerPlayerMarkerPane;
    @FXML
    private StackPane wissenschaftlerPlayerMarkerPane;

    private static final Color ARZT_COLOR = ColorService.convertColorToJavaFXColor(RoleColors.ARZT_COLOR_ORANGE);
    private static final Color BETRIEBSEXPERTE_COLOR = ColorService.convertColorToJavaFXColor(RoleColors.BETRIEBSEXPERTE_COLOR_GREEN);
    private static final Color FORSCHERIN_COLOR = ColorService.convertColorToJavaFXColor(RoleColors.FORSCHERIN_COLOR_BROWN);
    private static final Color LOGISTIKER_COLOR = ColorService.convertColorToJavaFXColor(RoleColors.LOGISTIKER_COLOR_PURPLE);
    private static final Color WISSENSCHAFTLER_COLOR = ColorService.convertColorToJavaFXColor(RoleColors.WISSENSCHAFTLER_COLOR_GRAY);


    /**
     * Initializes all {@link de.uol.swp.client.player.PlayerMarker} {@link StackPane}s ofF the {@link RolesTabPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.initializePlayerMarkerStackPane(arztPlayerMarkerPane, ARZT_COLOR);
        GameInstructionIconUtil.initializePlayerMarkerStackPane(betriebsexpertePlayerMarkerPane, BETRIEBSEXPERTE_COLOR);
        GameInstructionIconUtil.initializePlayerMarkerStackPane(forscherinPlayerMarkerPane, FORSCHERIN_COLOR);
        GameInstructionIconUtil.initializePlayerMarkerStackPane(logistikerPlayerMarkerPane, LOGISTIKER_COLOR);
        GameInstructionIconUtil.initializePlayerMarkerStackPane(wissenschaftlerPlayerMarkerPane, WISSENSCHAFTLER_COLOR);
    }
}
