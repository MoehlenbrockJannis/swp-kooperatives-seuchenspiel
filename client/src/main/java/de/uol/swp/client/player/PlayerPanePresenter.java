package de.uol.swp.client.player;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.DiscardCardDialog;
import de.uol.swp.client.card.PlayerCardHBox;
import de.uol.swp.client.card.SortInfectionCardsDialogPresenter;
import de.uol.swp.client.map.GameMapPresenter;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.client.util.NodeBindingUtils;
import de.uol.swp.client.util.TooltipsUtil;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.event_card.*;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.request.TriggerableRequest;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Presenter class for managing the player pane in the game UI.
 * <p>
 * This class is responsible for updating the player information, handling the player's hand cards,
 * and managing the visual representation of the player marker.
 * </p>
 *
 * @author Marvin Tischer
 * @since 2025-01-16
 */
public class PlayerPanePresenter extends AbstractPresenter {
    private static final int PLAYER_NUMBER_OF_ACTIONS_CIRCLE_RADIUS = 1;
    private static final double PLAYER_NUMBER_OF_ACTIONS_CIRCLE_RADIUS_SCALE_FACTOR = 0.3;
    private static final double PLAYER_NUMBER_OF_ACTIONS_TEXT_SCALE_FACTOR = 0.4;
    public static final int TOOLTIP_WIDTH = 400;

    @Getter
    @FXML
    private GridPane playerGridPane;
    @FXML
    private HBox playerInfoBox;
    @FXML
    private Pane playerNameStackPane;
    @FXML
    private Text playerNameText;
    @FXML
    private Pane playerRoleStackPane;
    @FXML
    private Text playerRoleText;
    @FXML
    private Pane symbolPane;
    @FXML
    private GridPane handCardGridPane;
    @FXML
    private StackPane playerNumberOfActionsPane;

    private Timeline borderTimeline;

    private int maxHandCards;

    private Player player;
    @Setter
    private Game game;
    private boolean isPresenterOfLobbyPlayer;

    private List<PlayerCard> currentlyDisplayedHandCards;

    @Setter
    private Supplier<Game> gameSupplier;
    @Setter
    private GameMapPresenter gameMapPresenter;

    private Map<PlayerCard, Runnable> handCardToClickListenerAssociation;

    private static final double DIALOG_WIDTH = 400;
    private static final double DIALOG_HEIGHT = 300;

    private static final int NUMBER_OF_TOP_CARDS = 6;

    private static final Color PLAYER_GRID_PANE_BACKGROUND_COLOR = Color.GRAY;

    /**
     * Creates the {@link #handCardGridPane} for the {@link PlayerPanePresenter} using {@link #createHandCardGridPane(int)}.
     * <p>
     * If this presenter represents the lobby player, two columns will be created for the {@link #handCardGridPane}.
     * Otherwise, only one column will be created.
     *
     * @param isLobbyPlayer Whether the player of this presenter is the lobby player.
     * @param maxHandCards  The maximum amount of hand cards allowed.
     * @see #createHandCardGridPane(int)
     */
    public void createHandCardStackPane(boolean isLobbyPlayer, int maxHandCards) {
        initializePlayerGridPaneBorderTimeline();
        this.maxHandCards = maxHandCards;
        this.isPresenterOfLobbyPlayer = isLobbyPlayer;

        if (isLobbyPlayer) {
            adjustPlayerGridPaneRowConstraintsForLobbyPlayer();
            createHandCardGridPane(2);
        } else {
            createHandCardGridPane(1);
        }
        updateHandCardGridPane(new AIPlayer("temporary player"));
    }

    /**
     * Adjusts the {@link RowConstraints} of the {@link #handCardGridPane}.
     * <p>
     * Increases the height of the playerInfoRow of the {@link #handCardGridPane},
     * if {@link #player} is the lobby player.
     */
    private void adjustPlayerGridPaneRowConstraintsForLobbyPlayer() {
        RowConstraints playerInfoRow = playerGridPane.getRowConstraints().get(0);
        RowConstraints handCardRow = playerGridPane.getRowConstraints().get(1);

        playerInfoRow.setPercentHeight(30);
        handCardRow.setPercentHeight(70);
    }

    /**
     * Creates the {@link #handCardGridPane} with the given amount of columns and
     * fills it with empty {@link PlayerCardHBox}es.
     *
     * @param numberOfColumns The number of columns the {@link #handCardGridPane} will have.
     */
    private void createHandCardGridPane(int numberOfColumns) {
        handCardGridPane.setVgap(5);
        handCardGridPane.setHgap(5);

        createHandCardGridPaneConstraints(numberOfColumns);

        int numberOfRows = maxHandCards;
        int numberOfRowsPerGridPaneColumn = (int) Math.ceil((double) numberOfRows / numberOfColumns);

        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
            for (int rowIndex = 0; rowIndex < numberOfRowsPerGridPaneColumn; rowIndex++) {
                PlayerCardHBox playerCardHBox = new PlayerCardHBox();
                playerCardHBox.setSpacing(3);
                handCardGridPane.add(playerCardHBox, columnIndex, rowIndex);
                GridPane.setHgrow(playerCardHBox, Priority.ALWAYS);
                GridPane.setVgrow(playerCardHBox, Priority.ALWAYS);
            }
        }
    }

    /**
     * Creates all needed {@link RowConstraints} and {@link ColumnConstraints} of the {@link #handCardGridPane}.
     *
     * @param numberOfColumns The number of columns needed for the {@link #handCardGridPane}.
     */
    private void createHandCardGridPaneConstraints(int numberOfColumns) {
        int numberOfRows = maxHandCards;
        int numberOfRowsPerGridPaneColumn = (int) Math.ceil((double) numberOfRows / numberOfColumns);

        for (int rowIndex = 0; rowIndex < numberOfRowsPerGridPaneColumn; rowIndex++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / numberOfRowsPerGridPaneColumn);
            handCardGridPane.getRowConstraints().add(rowConstraints);
        }

        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / numberOfColumns);
            handCardGridPane.getColumnConstraints().add(columnConstraints);
        }
    }

    /**
     * Updates the {@link #handCardGridPane} by filling its {@link PlayerCardHBox}es
     * with the {@link #player}'s hand cards.
     * <p>
     * Also highlights and adds a click listener to all {@link PlayerCardHBox}es,
     * whose {@link PlayerCard} is found in {@link #handCardToClickListenerAssociation}.
     *
     * @param currentPlayer The current {@link Player} from {@link de.uol.swp.common.game.Game#getPlayersInTurnOrder()}.
     * @see #highlightPlayerCardHBox(PlayerCardHBox)
     * @see #addClickListenerToPlayerCardHBox(PlayerCardHBox)
     */
    public void updateHandCardGridPane(Player currentPlayer) {
        if (!currentlyDisplayedHandCards.equals(player.getHandCards())) {
            Platform.runLater(() -> {
                clearAllHandCardGridPaneRows();

                int maxHandCardGridPaneRowIndex = Math.min(maxHandCards, player.getHandCards().size());

                for (int handCardGridPaneRowIndex = 0; handCardGridPaneRowIndex < maxHandCardGridPaneRowIndex; handCardGridPaneRowIndex++) {
                    PlayerCardHBox playerCardHBox = (PlayerCardHBox) handCardGridPane.getChildren().get(handCardGridPaneRowIndex);
                    PlayerCard playerCard = player.getHandCards().get(handCardGridPaneRowIndex);
                    fillHandCardGridPaneRow(playerCardHBox, playerCard);

                    if (handCardToClickListenerAssociation.containsKey(playerCard)) {
                        highlightPlayerCardHBox(playerCardHBox);
                        addClickListenerToPlayerCardHBox(playerCardHBox);
                    }
                }

                currentlyDisplayedHandCards = player.getHandCards();
            });
        }
        if (this.player.equals(currentPlayer)) {
            borderTimeline.play();

        } else {
            borderTimeline.stop();
            setDefaultPlayerGridPaneBorder();
        }
    }

    /**
     * Clears all {@link PlayerCardHBox}es of the {@link #handCardGridPane}.
     */
    private void clearAllHandCardGridPaneRows() {
        for (int handCardGridPaneRowIndex = 0; handCardGridPaneRowIndex < handCardGridPane.getChildren().size(); handCardGridPaneRowIndex++) {
            PlayerCardHBox playerCardHBox = (PlayerCardHBox) handCardGridPane.getChildren().get(handCardGridPaneRowIndex);
            playerCardHBox.getChildren().clear();
            playerCardHBox.setBackgroundColor(Color.WHITE);
            playerCardHBox.setCursor(Cursor.DEFAULT);
            playerCardHBox.setOnMouseClicked(null);
        }
    }

    /**
     * Fills a given {@link PlayerCardHBox} with a {@link Circle} and a {@link Text}
     * representing the given {@link PlayerCard}.
     *
     * @param playerCardHBox The {@link PlayerCardHBox} that will be filled.
     * @param playerCard     The {@link PlayerCard} that will be represented.
     */
    private void fillHandCardGridPaneRow(PlayerCardHBox playerCardHBox, PlayerCard playerCard) {
        playerCardHBox.getChildren().clear();
        playerCardHBox.setPlayerCard(playerCard);

        Color color = ColorService.convertColorToJavaFXColor(playerCard.getColor());
        Circle cardColorCircle = new Circle(1, color);

        StackPane cardColorStackPane = new StackPane(cardColorCircle);

        Text cardTitleText = new Text(playerCard.getTitle());
        Pane cardTitlePane = new Pane(cardTitleText);

        bindTextFontSizeToPane(cardTitleText, cardTitlePane);
        cardColorStackPane.prefWidthProperty().bind(playerCardHBox.heightProperty());
        cardColorCircle.radiusProperty().bind(cardColorStackPane.heightProperty().divide(3));
        playerCardHBox.getChildren().addAll(cardColorStackPane, cardTitlePane);

        setupTooltipForEventCard(playerCardHBox, playerCard);
    }

    /**
     * Adds a tooltip displaying the description if the player card is an event card.
     *
     * @param playerCardHBox the UI container for the player card
     * @param playerCard the card to check for tooltip installation
     */
    private void setupTooltipForEventCard(PlayerCardHBox playerCardHBox, PlayerCard playerCard) {
        if (playerCard instanceof EventCard) {
            EventCard eventCard = (EventCard) playerCard;

            Tooltip tooltip = TooltipsUtil.createConsistentTooltip(eventCard.getDescription(), TOOLTIP_WIDTH);
            Tooltip.install(playerCardHBox, tooltip);

            playerCardHBox.setOnMouseEntered(event -> {
                Tooltip.uninstall(playerCardHBox, tooltip);
                Tooltip newTooltip = TooltipsUtil.createConsistentTooltip(eventCard.getDescription(), TOOLTIP_WIDTH);
                Tooltip.install(playerCardHBox, newTooltip);
            });
        }
    }

    /**
     * Highlights and adds a click listener to the {@link PlayerCardHBox} that represents the given {@link PlayerCard}.
     *
     * @param playerCard The {@link PlayerCard} whose {@link PlayerCardHBox} will be highlighted.
     * @see #highlightPlayerCardHBox(PlayerCardHBox)
     * @see #addClickListenerToPlayerCardHBox(PlayerCardHBox)
     */
    private void highlightAndAssignClickListenerToPlayerCardHBoxOfPlayerCard(PlayerCard playerCard) {
        for (int handCardListIndex = 0; handCardListIndex < player.getHandCards().size(); handCardListIndex++) {
            PlayerCardHBox playerCardHBox = (PlayerCardHBox) handCardGridPane.getChildren().get(handCardListIndex);
            if (playerCard.equals(playerCardHBox.getPlayerCard())) {
                highlightPlayerCardHBox(playerCardHBox);
                addClickListenerToPlayerCardHBox(playerCardHBox);
            }
        }
    }

    /**
     * Highlights a given {@link PlayerCardHBox} by setting the background color to a
     * bright color based on its {@link PlayerCard} color.
     *
     * @param playerCardHBox The {@link PlayerCardHBox} that will be highlighted.
     * @see ColorService#adjustBrightness(Color, double)
     * @see PlayerCardHBox#setBackgroundColor(Color)
     */
    private void highlightPlayerCardHBox(PlayerCardHBox playerCardHBox) {
        PlayerCard playerCard = playerCardHBox.getPlayerCard();
        Color color = ColorService.convertColorToJavaFXColor(playerCard.getColor());
        color = ColorService.adjustBrightness(color, 1);
        playerCardHBox.setBackgroundColor(color);
    }

    /**
     * Adds the click listener to the given {@link PlayerCardHBox} from the {@link #handCardToClickListenerAssociation}.
     *
     * @param playerCardHBox The {@link PlayerCardHBox} that will get a click listener.
     */
    private void addClickListenerToPlayerCardHBox(PlayerCardHBox playerCardHBox) {
        playerCardHBox.setCursor(Cursor.HAND);
        playerCardHBox.setOnMouseClicked(event -> {
            final PlayerCard playerCard = playerCardHBox.getPlayerCard();
            final Runnable clickListener = this.handCardToClickListenerAssociation.getOrDefault(playerCard, () -> {
            });
            clickListener.run();
            handCardToClickListenerAssociation.remove(playerCard);
        });
    }


    /**
     * Initializes this {@link PlayerPanePresenter} when the corresponding fxml file is loaded.
     * Initializes the {@link #handCardToClickListenerAssociation} and {@link #currentlyDisplayedHandCards}
     * and binds the {@link #playerNameText} and {@link #playerRoleText} size to their {@link StackPane}.
     */
    @FXML
    private void initialize() {
        this.handCardToClickListenerAssociation = new HashMap<>();
        this.currentlyDisplayedHandCards = new ArrayList<>();
        playerGridPane.setBackground(new Background(new BackgroundFill(
                PLAYER_GRID_PANE_BACKGROUND_COLOR, null, null)));
        setDefaultPlayerGridPaneBorder();
        playerNumberOfActionsPane.prefHeightProperty().bind(playerInfoBox.heightProperty());
        playerNumberOfActionsPane.prefWidthProperty().bind(playerInfoBox.heightProperty());
    }

    /**
     * Binds the font size of a given {@link Text} to the given {@link Pane} and sets the bold font weight.
     *
     * @param text The {@link Text} whose font size will be bound to the given {@link Pane}.
     * @param pane The {@link Pane} whose size will be bound to the given {@link Text}.
     * @see NodeBindingUtils#bindRegionSizeToTextFont(Region, Text, double)
     */
    private void bindTextFontSizeToPane(Text text, Pane pane) {
        NodeBindingUtils.bindRegionSizeToTextFont(pane, text, 0.5);
        text.layoutYProperty().bind(pane.heightProperty().divide(1.5));
    }

    /**
     * Sets the default border for the {@link #playerGridPane}.
     * The default border color is set to {@link #PLAYER_GRID_PANE_BACKGROUND_COLOR}.
     */
    private void setDefaultPlayerGridPaneBorder() {
        playerGridPane.setBorder(createPlayerGridPaneBorder(PLAYER_GRID_PANE_BACKGROUND_COLOR));
    }

    /**
     * Creates a border for the {@link #playerGridPane} with the given {@link Color}.
     *
     * @param color The {@link Color} of the border.
     * @return The created border with the given {@link Color}.
     */
    private Border createPlayerGridPaneBorder(Color color) {
        BorderStroke initialBorderStroke = new BorderStroke(
                color,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(7));
        return new Border(initialBorderStroke);
    }

    /**
     * Initializes the {@link Timeline} for animating the border color of the {@link #playerGridPane}.
     * The animation interpolates the border color from the {@link Player}'s role color
     * to {@link #PLAYER_GRID_PANE_BACKGROUND_COLOR}.
     */
    private void initializePlayerGridPaneBorderTimeline() {
        borderTimeline = new Timeline();
        Duration duration = Duration.seconds(1);
        Color startColor = ColorService.convertColorToJavaFXColor(player.getRole().getColor());

        int numberOfFrames = 30;
        double frameDuration = duration.toMillis() / numberOfFrames;

        for (int frame = 0; frame <= numberOfFrames; frame++) {
            final double progress = frame / (double) numberOfFrames;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(frame * frameDuration), e -> {
                Color interpolatedColor = ColorService.interpolateColor(startColor, PLAYER_GRID_PANE_BACKGROUND_COLOR, progress);
                playerGridPane.setBorder(createPlayerGridPaneBorder(interpolatedColor));
            });
            borderTimeline.getKeyFrames().add(keyFrame);
        }

        borderTimeline.setCycleCount(Animation.INDEFINITE);
        borderTimeline.setAutoReverse(true);
    }

    /**
     * Updates the player's information in the UI, including name, role, and hand cards.
     *
     * @param player the player whose information is to be displayed
     * @author Marvin Tischer
     * @since 2025-01-16
     */
    public void setPlayerInfo(Player player) {
        playerNameText.setText(player.getName());
        playerRoleText.setText(player.getRole().toString());
        this.player = player;
        playerNameText.setFont(Font.font(null, FontWeight.BOLD, 10));
        bindTextFontSizeToPane(playerNameText, playerNameStackPane);
        bindTextFontSizeToPane(playerRoleText, playerRoleStackPane);
        refreshNumberOfActionsToDo();
    }

    /**
     * Sets the visual marker for the player.
     *
     * @param playerMarker the marker to be displayed in the symbol pane
     * @author Marvin Tischer
     * @since 2025-01-16
     */
    public void setPlayerMarker(PlayerMarker playerMarker) {
        symbolPane.getChildren().clear();
        symbolPane.minWidthProperty().bind(symbolPane.heightProperty());
        symbolPane.prefWidthProperty().bind(symbolPane.heightProperty());
        symbolPane.maxWidthProperty().bind(symbolPane.heightProperty());
        symbolPane.getChildren().add(playerMarker);
        bindPlayerMarkerToSymbolPane(playerMarker);
    }

    /**
     * Binds the {@link PlayerMarker} size to the {@link #symbolPane}.
     *
     * @param playerMarker The {@link PlayerMarker} whose size will be bound to the {@link #symbolPane}.
     */
    private void bindPlayerMarkerToSymbolPane(PlayerMarker playerMarker) {
        double scaleFactor = 0.027;
        playerMarker.layoutXProperty().bind(symbolPane.widthProperty().divide(2));
        playerMarker.layoutYProperty().bind(symbolPane.heightProperty().divide(2));
        playerMarker.scaleXProperty().bind(symbolPane.widthProperty().multiply(scaleFactor));
        playerMarker.scaleYProperty().bind(symbolPane.heightProperty().multiply(scaleFactor));
    }

    /**
     * Checks whether {@link #player} equals the given {@link Player}.
     *
     * @param player The {@link Player} that will be compared to the presenter's {@link #player}
     * @return {@code true} if given {@link Player} equals the presenter's {@link #player}. Else returns {@code false}.
     */
    public boolean hasPlayer(Player player) {
        return this.player.equals(player);
    }

    /**
     * Highlights the {@link PlayerCardHBox} of given {@link PlayerCard} and assigns given {@link Runnable} as click listener.
     *
     * @param playerCard    {@link PlayerCard} to highlight
     * @param clickListener {@link Runnable} that is executed when given {@link PlayerCard} in {@link PlayerCardHBox} is clicked
     */
    public void highlightHandCardAndAssignClickListener(final PlayerCard playerCard, final Runnable clickListener) {
        if (isPresenterOfLobbyPlayer && !game.isGameLost()) {
            this.handCardToClickListenerAssociation.put(playerCard, createRunnableForEventCard(playerCard, clickListener));
            highlightAndAssignClickListenerToPlayerCardHBoxOfPlayerCard(playerCard);
        }
    }

    /**
     * Creates a new click listener if given {@link PlayerCard} is an {@link EventCard}
     * by invoking methods that determine what to do with it.
     *
     * @param playerCard    {@link PlayerCard} to create {@link Runnable} click listener for
     * @param clickListener original, unmodified {@link Runnable} click listener
     * @return potentially modified {@link Runnable} click listener for given {@link PlayerCard}
     */
    private Runnable createRunnableForEventCard(final PlayerCard playerCard, final Runnable clickListener) {
        if (game.isGameLost()) {
            return null;
        }
        if (playerCard instanceof AirBridgeEventCard airBridgeEventCard) {
            return prepareAirBridgeEventCard(airBridgeEventCard, clickListener);
        } else if (playerCard instanceof ForecastEventCard forecastEventCard) {
            return prepareForecastEventCard(forecastEventCard, clickListener);
        } else if (playerCard instanceof GovernmentSubsidiesEventCard governmentSubsidiesEventCard) {
            return prepareGovernmentSubsidiesEventCard(governmentSubsidiesEventCard, clickListener);
        } else if (playerCard instanceof ToughPopulationEventCard toughPopulationEventCard) {
            return prepareToughPopulationEventCard(toughPopulationEventCard, clickListener);
        }
        return clickListener;
    }

    /**
     * Prepares a given {@link AirBridgeEventCard} for use.
     *
     * @param airBridgeEventCard {@link AirBridgeEventCard} to prepare an action listener for
     * @param approve            {@link Runnable} executing the given {@link AirBridgeEventCard}
     * @return {@link Runnable} as action listener to play given {@link AirBridgeEventCard}
     */
    private Runnable prepareAirBridgeEventCard(final AirBridgeEventCard airBridgeEventCard, final Runnable approve) {
        if (game.isGameLost()) {
            return null;
        }
        return () -> {
            final Game game = airBridgeEventCard.getGame();
            this.gameMapPresenter.setClickListenersForPlayerMarkersAndFields(
                    game.getPlayersInTurnOrder(),
                    game.getFields(),
                    createAirBridgeEventCardPlayerAndFieldClickConsumer(airBridgeEventCard)
            );
        };
    }

    /**
     * Creates a {@link BiConsumer} that is invoked with the target {@link Player} and {@link Field} of an {@link AirBridgeEventCard}.
     *
     * @param airBridgeEventCard {@link AirBridgeEventCard} that is played
     * @return {@link BiConsumer} invoked with the target {@link Player} and {@link Field} of an {@link AirBridgeEventCard}
     */
    private BiConsumer<Field, Player> createAirBridgeEventCardPlayerAndFieldClickConsumer(final AirBridgeEventCard airBridgeEventCard) {
        if (game.isGameLost()) {
            return null;
        }
        return (field, movedPlayer) -> {
            airBridgeEventCard.setTargetPlayer(movedPlayer);
            airBridgeEventCard.setTargetField(field);

            Message messageToSend = createCorrectAirBridgeEventCardMessage(airBridgeEventCard, movedPlayer);
            this.eventBus.post(messageToSend);
        };
    }

    /**
     * Creates the correct {@link Message} for an {@link AirBridgeEventCard}.
     * The returned {@link Message} will be a {@link TriggerableRequest}
     * if the holder of the {@link AirBridgeEventCard} is the moved {@link Player}.
     * It will be an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#OUTBOUND}
     * if the moved {@link Player} is not the holder.
     *
     * @param airBridgeEventCard {@link AirBridgeEventCard} to send via the {@link Message}
     * @return {@link TriggerableRequest} if moved {@link Player} is event card holder or {@link ApprovableRequest} if not
     */
    private Message createCorrectAirBridgeEventCardMessage(final AirBridgeEventCard airBridgeEventCard, final Player movedPlayer) {
        Message messageToSend = new TriggerableRequest(airBridgeEventCard, null, null);

        final Player eventCardHolder = airBridgeEventCard.getPlayer();

        if (!eventCardHolder.equals(movedPlayer)) {
            messageToSend = new ApprovableRequest(
                    ApprovableMessageStatus.OUTBOUND,
                    airBridgeEventCard,
                    messageToSend,
                    eventCardHolder,
                    null,
                    null
            );
        } else {
            airBridgeEventCard.approve();
        }

        return messageToSend;
    }

    /**
     * Prepares a given {@link ToughPopulationEventCard} for use.
     *
     * @param toughPopulationEventCard {@link ToughPopulationEventCard} to prepare an action listener for
     * @param approve                  {@link Runnable} executing the given {@link ToughPopulationEventCard}
     * @return {@link Runnable} as action listener to play given {@link ToughPopulationEventCard}
     */
    private Runnable prepareToughPopulationEventCard(final ToughPopulationEventCard toughPopulationEventCard, final Runnable approve) {
        return () -> {
            if (!game.isGameLost()) {
                CardStack<InfectionCard> infectionDiscardStack = gameSupplier.get().getInfectionDiscardStack();
                if (infectionDiscardStack.isEmpty()) {
                    return;
                }
                String buttonText = "Karte auswählen";
                DiscardCardDialog<InfectionCard> dialog = new DiscardCardDialog<>(infectionDiscardStack, buttonText, toughPopulationEventCard.getDescription());
                dialog.showAndWait().ifPresent(card -> {
                    toughPopulationEventCard.setInfectionCard(card);
                    approve.run();
                });
            }
        };
    }

    /**
     * Prepares a runnable event for handling the reordering of infection cards in a forecast event.
     *
     * @param forecastEventCard The {@link ForecastEventCard} to update with the reordered infection cards.
     * @param approve A {@link Runnable} that will be executed after the forecast event card is updated.
     * @return A {@link Runnable} that performs the above actions when executed.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private Runnable prepareForecastEventCard(final ForecastEventCard forecastEventCard, final Runnable approve) {
        if (game.isGameLost()) {
            return null;
        }
        return () -> {
            List<InfectionCard> reorderedItems = openSortInfectionCardsDialog();
            if (reorderedItems != null) {
                forecastEventCard.setReorderedInfectionCards(reorderedItems);
                approve.run();
            }
        };
    }

    /**
     * Opens a dialog for sorting infection cards and returns the reordered list of cards.
     *
     * @return A reordered list of {@link InfectionCard} if confirmed, or {@code null} if the operation was canceled.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private List<InfectionCard> openSortInfectionCardsDialog() {
        if (game.isGameLost()) {
            return null;
        }

        SortInfectionCardsDialogPresenter listDialogPresenter = createListDialogPresenter();

        Stage sortInfectionCardsDialogStage = createDialogStage(listDialogPresenter);
        sortInfectionCardsDialogStage.showAndWait();

        if (!listDialogPresenter.wasConfirmed()) {
            return null;
        }

        return listDialogPresenter.getUpdatedList();
    }

    /**
     * Creates and initializes a {@link SortInfectionCardsDialogPresenter} for sorting infection cards.
     *
     * @return A fully initialized {@link SortInfectionCardsDialogPresenter}.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private SortInfectionCardsDialogPresenter createListDialogPresenter() {
        SortInfectionCardsDialogPresenter listDialogPresenter =
                AbstractPresenter.loadFXMLPresenter(SortInfectionCardsDialogPresenter.class);

        Game game = this.gameSupplier.get();
        CardStack<InfectionCard> infectionDrawStack = game.getInfectionDrawStack();
        List<InfectionCard> topCards = infectionDrawStack.getTopCards(NUMBER_OF_TOP_CARDS);

        listDialogPresenter.initialize(topCards);
        return listDialogPresenter;
    }

    /**
     * Creates a modal dialog stage for sorting infection cards with the given presenter.
     *
     * @param sortInfectionCardsDialogPresenter The presenter responsible for handling the infection card sorting dialog.
     * @return A {@link Stage} representing the modal dialog for sorting infection cards.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private Stage createDialogStage(SortInfectionCardsDialogPresenter sortInfectionCardsDialogPresenter) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("Reihenfolge ändern");
        dialogStage.setScene(sortInfectionCardsDialogPresenter.getScene());
        dialogStage.setWidth(DIALOG_WIDTH);
        dialogStage.setHeight(DIALOG_HEIGHT);

        enableDragging(dialogStage, sortInfectionCardsDialogPresenter.getScene());

        return dialogStage;
    }

    /**
     * Enables dragging of the given stage using the specified scene.
     * This allows the user to move the stage by clicking and dragging anywhere in the scene.
     *
     * @param stage The stage to be moved.
     * @param scene The scene used for tracking mouse events.
     *
     * @author Marvin Tischer
     * @since 2025-02-25
     */
    private void enableDragging(Stage stage, Scene scene) {
        final DoubleProperty mouseX = new SimpleDoubleProperty();
        final DoubleProperty mouseY = new SimpleDoubleProperty();

        scene.setOnMousePressed(event -> {
            mouseX.set(event.getSceneX());
            mouseY.set(event.getSceneY());
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - mouseX.get());
            stage.setY(event.getScreenY() - mouseY.get());
        });
    }

    /**
     * Prepares a given {@link GovernmentSubsidiesEventCard} for use.
     *
     * @param governmentSubsidiesEventCard {@link GovernmentSubsidiesEventCard} to prepare an action listener for
     * @param approve {@link Runnable} executing the given {@link GovernmentSubsidiesEventCard}
     * @return {@link Runnable} as action listener to play given {@link GovernmentSubsidiesEventCard}
     */
    private Runnable prepareGovernmentSubsidiesEventCard(final GovernmentSubsidiesEventCard governmentSubsidiesEventCard, final Runnable approve) {
        if (game.isGameLost()) {
            return null;
        }
        return () -> {
            gameMapPresenter.setClickListenersForGovernmentSubsidiesFields(governmentSubsidiesEventCard, approve);
        };
    }

    /**
     * Refreshes the {@link #playerNumberOfActionsPane} by clearing it and
     * then setting the number of actions the {@link #player} has to do
     * if it is the current one in {@link #game}.
     *
     * @see #playerNumberOfActionsPane
     * @see #isCurrentPlayerInGame()
     * @see #addNumberOfActionsToDo()
     */
    private void refreshNumberOfActionsToDo() {
        Platform.runLater(() -> {
            this.playerNumberOfActionsPane.getChildren().clear();

            if (isCurrentPlayerInGame()) {
                addNumberOfActionsToDo();
            }
        });
    }

    /**
     * Returns whether {@link #player} is the current one in {@link #game}.
     *
     * @return {@code true} if {@link #player} is equal to {@link Game#getCurrentPlayer()} for {@link #game}, {@code false} otherwise
     * @see Game#getCurrentPlayer()
     */
    private boolean isCurrentPlayerInGame() {
        return this.player.equals(this.game.getCurrentPlayer());
    }

    /**
     * Adds the number of actions to do as {@link Text} in a {@link Circle} in the {@link #playerNumberOfActionsPane}.
     */
    private void addNumberOfActionsToDo() {
        final Circle circle = new Circle(PLAYER_NUMBER_OF_ACTIONS_CIRCLE_RADIUS);
        circle.setFill(Color.DARKGREY);
        circle.radiusProperty().bind(playerNumberOfActionsPane.heightProperty().multiply(PLAYER_NUMBER_OF_ACTIONS_CIRCLE_RADIUS_SCALE_FACTOR));

        final PlayerTurn currentPlayerTurn = this.game.getCurrentTurn();
        final int numberOfActionsToDo = currentPlayerTurn.getNumberOfActionsToDo();
        final Text text = new Text(Integer.toString(numberOfActionsToDo));
        NodeBindingUtils.bindRegionSizeToTextFont(playerNumberOfActionsPane, text, PLAYER_NUMBER_OF_ACTIONS_TEXT_SCALE_FACTOR);

        this.playerNumberOfActionsPane.getChildren().addAll(circle, text);
    }
}
