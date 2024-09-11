package de.uol.swp.client.main;

import com.google.inject.Inject;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.user.User;
import javafx.util.Callback;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import lombok.AllArgsConstructor;
import lombok.Setter;


@Setter
public class HighlightLoggedInUserCellFactory implements Callback<ListView<String>, ListCell<String>> {
    private User loggedInUserProvider;

    @Override
    public ListCell<String> call(ListView<String> listView) {
        return new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    User user = loggedInUserProvider;
                    if (user != null && user.getUsername().equals(item)) {
                        setStyle("-fx-background-color: lightgray;");
                    } else {
                        setStyle("-fx-background-color: white;");
                    }
                } else {
                    setText(null);
                    setStyle(null);
                }
            }
        };
    }
}
