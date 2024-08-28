package de.uol.swp.client.gameinstructions;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * The GameInstructionsPresenter class manages the display and hiding of a popup
 * containing game instructions in the user interface.
 *
 * @since 2024-08-27
 */
public class GameInstructionsPresenter {

    @FXML
    private AnchorPane popupAnchorPane;

    /**
     * Closes the popup by setting its visibility to false.
     *
     * @since 2024-08-27
     */
    public void closePopup() {
        popupAnchorPane.setVisible(false);
    }

    /**
     * Opens the popup by setting its visibility to true.
     *
     * @since 2024-08-27
     */
    public void openPopup() {
        popupAnchorPane.setVisible(true);
    }

    /**
     * Handles the action event triggered when the close button is pressed.
     *
     * @param event the ActionEvent triggered by pressing the close button
     * @since 2024-08-27
     */
    @FXML
    void onCloseButtonPressed(ActionEvent event) {
        closePopup();
    }
}
