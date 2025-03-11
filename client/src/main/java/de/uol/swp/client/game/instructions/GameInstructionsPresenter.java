package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * The GameInstructionsPresenter class manages the display and hiding of a popup
 * containing game instructions in the user interface.
 *
 */
public class GameInstructionsPresenter extends AbstractPresenter {

    @FXML
    private GridPane popupGridPane;
    @FXML
    private TabPane tabPane;

    /**
     * Initializes the {@link TabPane} selection listener of the {@link GameInstructionsPresenter}.
     * <p>
     * This method adds a listener to the {@link TabPane}'s selection model, which is triggered
     * whenever the selected tab changes. If the new tab contains a Parent as its content,
     * it requests a layout update to ensure the content is properly arranged and displayed.
     */
    @FXML
    private void initialize() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) -> {
            if (newTab != null && newTab.getContent() instanceof Parent parentContent) {
                parentContent.requestLayout();
            }
        });
    }

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
