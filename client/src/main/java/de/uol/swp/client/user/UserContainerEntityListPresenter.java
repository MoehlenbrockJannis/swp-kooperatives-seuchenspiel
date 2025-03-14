package de.uol.swp.client.user;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserContainerEntity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Presenter for managing a list view of UserContainerEntities with highlighting and custom interactions.
 */
public class UserContainerEntityListPresenter extends AbstractPresenter {

    @FXML
    private Label title;
    @FXML
    private ListView<UserContainerEntity> listView;
    private HighlightUserCellFactory highlightUserCellFactory;

    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    /**
     * Initializes the presenter and sets up the list view and highlighting configurations.
     */
    @FXML
    private void initialize() {
        highlightUserCellFactory = new HighlightUserCellFactory();
        resetHighlighting();
        listView.setCellFactory(highlightUserCellFactory);
    }

    /**
     * Resets the highlighting configurations to default.
     */
    private void resetHighlighting() {
        highlightUserCellFactory.reset();
        highlightUserCellFactory.addHighlightingConfiguration(loggedInUserProvider, listCell -> listCell.getStyleClass().add("logged-in-user-highlighting"));
    }

    /**
     * Sets the title above the list to the given string.
     *
     * @param title The title to be set.
     */
    public void setTitle(final String title) {
        this.title.setText(title);
    }

    /**
     * Updates the list view with the given list of UserContainerEntities.
     *
     * @param list The list of UserContainerEntities to display.
     */
    public void setList(final Collection<UserContainerEntity> list) {
        Platform.runLater(() -> {
            final ObservableList<UserContainerEntity> observableList = FXCollections.observableArrayList(list);
            this.listView.setItems(observableList);
        });
    }

    /**
     * Highlights the cell containing the specified user.
     *
     * @param user The user to highlight.
     */
    public void highlightUser(final User user) {
        resetHighlighting();
        highlightUserCellFactory.addHighlightingConfiguration(() -> user, listCell -> listCell.getStyleClass().add("lobby-owner-highlighting"));
    }

    /**
     * Sets the right-click function on the list cells.
     *
     * @param rightClickFunction The function to be executed on right-click.
     */
    public void setRightClickFunctionToListCells(Consumer<ListCell<UserContainerEntity>> rightClickFunction) {
        this.highlightUserCellFactory.setRightClickFunction(rightClickFunction);
    }
}