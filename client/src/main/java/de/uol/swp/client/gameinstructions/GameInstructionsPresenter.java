package de.uol.swp.client.gameinstructions;

import de.uol.swp.client.AbstractPresenter;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * The GameInstructionsPresenter class manages the display and hiding of a popup
 * containing game instructions in the user interface.
 *
 * @since 2024-08-27
 */
public class GameInstructionsPresenter extends AbstractPresenter {

    @FXML
    private GridPane popupGridPane;

    /**
     * Closes the popup by setting its visibility to false.
     *
     * @since 2024-08-27
     */
    public void closePopup() {
        popupGridPane.setVisible(false);
    }

    /**
     * Opens the popup by setting its visibility to true.
     *
     * @since 2024-08-27
     */
    public void openPopup() {
        popupGridPane.setVisible(true);
    }

    /**
     * Handles the key press event to close the popup when the Escape key is pressed.
     *
     * @param event the KeyEvent triggered by pressing a key
     * @since 2024-08-28
     */
    @FXML
    void handleEscKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closePopup();
        }
    }
}
