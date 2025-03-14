package de.uol.swp.client.user;

import com.google.inject.Provider;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserContainerEntity;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A factory class to create ListCells with user highlighting and custom right-click handling.
 */
public class HighlightUserCellFactory implements Callback<ListView<UserContainerEntity>, ListCell<UserContainerEntity>> {

    /**
     * Functional interface for highlighting a ListCell based on custom conditions.
     */
    public interface HighlightingFunction extends Consumer<ListCell<UserContainerEntity>> {}

    private Map<Provider<User>, HighlightingFunction> highlightingTable = new HashMap<>();

    @Setter
    private Consumer<ListCell<UserContainerEntity>> rightClickFunction;

    /**
     * Resets the highlighting configurations.
     */
    public void reset() {
        highlightingTable = new HashMap<>();
    }

    /**
     * Adds highlighting configuration for a specific user.
     *
     * @param userProvider The provider for the user to be highlighted.
     * @param highlightingFunction The function defining the highlighting behavior.
     */
    public void addHighlightingConfiguration(final Provider<User> userProvider,
                                             final HighlightingFunction highlightingFunction) {
        highlightingTable.put(userProvider, highlightingFunction);
    }

    /**
     * Creates a ListCell for user entities with custom display and highlighting configurations.
     *
     * @param listView The ListView for which the cell is created.
     * @return A ListCell with user highlighting and custom right-click handling.
     */
    @Override
    public ListCell<UserContainerEntity> call(final ListView<UserContainerEntity> listView) {
        final ListCell<UserContainerEntity> listCell = new ListCell<>() {
            @Override
            protected void updateItem(final UserContainerEntity item, final boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().clear();
                getStyleClass().add("list-item-default");

                if (item != null) {
                    setText(item.display());
                    checkHighlightingTable(item);
                } else {
                    setText(null);
                    setStyle(null);
                }
            }

            /**
             * Checks the highlighting configurations and applies them if conditions are met.
             *
             * @param entity The user container entity whose cells may be highlighted.
             */
            private void checkHighlightingTable(final UserContainerEntity entity) {
                for (final Map.Entry<Provider<User>, HighlightingFunction> highlightingConfiguration : highlightingTable.entrySet()) {
                    checkHighlightingConfiguration(
                            entity,
                            highlightingConfiguration.getKey(),
                            highlightingConfiguration.getValue()
                    );
                }
            }

            /**
             * Applies the highlighting function if the entity contains the specified user.
             *
             * @param entity The user container entity.
             * @param userProvider The provider for the user.
             * @param highlightingFunction The function defining the highlighting behavior.
             */
            private void checkHighlightingConfiguration(final UserContainerEntity entity,
                                                        final Provider<User> userProvider,
                                                        final HighlightingFunction highlightingFunction) {
                if (entity.containsUser(userProvider.get())) {
                    highlightingFunction.accept(this);
                }
            }
        };

        listCell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY && !listCell.isEmpty() && rightClickFunction != null) {
                rightClickFunction.accept(listCell);
            }
        });

        return listCell;
    }
}