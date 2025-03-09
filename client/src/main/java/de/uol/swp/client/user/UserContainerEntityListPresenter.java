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

public class UserContainerEntityListPresenter extends AbstractPresenter {
    @FXML
    private Label title;
    @FXML
    private ListView<UserContainerEntity> listView;
    private HighlightUserCellFactory highlightUserCellFactory;

    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    @FXML
    private void initialize() {
        highlightUserCellFactory = new HighlightUserCellFactory();
        resetHighlighting();
        listView.setCellFactory(highlightUserCellFactory);

    }

    private void resetHighlighting() {
        highlightUserCellFactory.reset();
        highlightUserCellFactory.addHighlightingConfiguration(loggedInUserProvider, listCell -> listCell.getStyleClass().add("logged-in-user-highlighting"));
    }

    /**
     * Sets the title above the list to the given String
     *
     * @param title Title to be set
     * @since 2024-09-11
     */
    public void setTitle(final String title) {
        this.title.setText(title);
    }

    /**
     * Updates the {@link #listView} according to the list given
     *
     * @param list A list of {@link UserContainerEntity} to put into {@link #listView}
     * @since 2024-09-11
     */
    public void setList(final Collection<UserContainerEntity> list) {
        Platform.runLater(() -> {
            final ObservableList<UserContainerEntity> observableList = FXCollections.observableArrayList(list);
            this.listView.setItems(observableList);
        });
    }

    /**
     * Highlights the cell the given user is in
     *
     * @param user The user to highlight
     */
    public void highlightUser(final User user) {
        resetHighlighting();
        highlightUserCellFactory.addHighlightingConfiguration(() -> user, listCell -> listCell.getStyleClass().add("lobby-owner-highlighting"));
    }

    public void setRightClickFunctionToListCells(Consumer<ListCell<UserContainerEntity>> rightClickFunction) {
        this.highlightUserCellFactory.setRightClickFunction(rightClickFunction);
    }
}
