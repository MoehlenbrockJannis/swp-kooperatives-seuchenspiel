package de.uol.swp.client.gameinstructions;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class GameInstructionsPresenter {

    @FXML
    private AnchorPane popupAnchorPane;

    public void closePopup() {
        popupAnchorPane.setVisible(false);
    }

    public void openPopup() {
        popupAnchorPane.setVisible(true);
    }

    @FXML
    void onCloseButtonPressed(ActionEvent event) {
        closePopup();
    }
}
