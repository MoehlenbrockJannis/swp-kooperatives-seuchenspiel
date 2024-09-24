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

public class HighlightUserCellFactory implements Callback<ListView<UserContainerEntity>, ListCell<UserContainerEntity>> {
    public interface HighlightingFunction extends Consumer<ListCell<UserContainerEntity>> {}

    private Map<Provider<User>, HighlightingFunction> highlightingTable = new HashMap<>();

    @Setter
    private Consumer<ListCell<UserContainerEntity>> rightClickFunction;

    public void reset() {
        highlightingTable = new HashMap<>();
    }

    public void addHighlightingConfiguration(final Provider<User> userProvider,
                                             final HighlightingFunction highlightingFunction) {
        highlightingTable.put(userProvider, highlightingFunction);
    }

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

            private void checkHighlightingTable(final UserContainerEntity entity) {
                for (final Map.Entry<Provider<User>, HighlightingFunction> highlightingConfiguration : highlightingTable.entrySet()) {
                    checkHighlightingConfiguration(
                            entity,
                            highlightingConfiguration.getKey(),
                            highlightingConfiguration.getValue()
                    );
                }
            }

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
