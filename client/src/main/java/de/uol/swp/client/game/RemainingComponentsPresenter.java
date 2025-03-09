package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.Game;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Presenter for the remaining components component
 *
 * @author Silas van Thiel
 * @since 2025-02-07
 */
public class RemainingComponentsPresenter extends AbstractPresenter {
    private static final double REMAINING_SYMBOL_SCALE_FACTOR = 0.027;

    @FXML
    private StackPane componentSymbolPane;
    @FXML
    private Text numberOfComponentsText;
    private Supplier<Game> gameSupplier;
    private Function<Game, Integer> getNumberOfRemainingComponents;


    /**
     * Initializes the presenter with the given parameters
     *
     * @param gameSupplier                The supplier for the current game
     * @param getNumberOfRemainingComponents The function to get the number of remaining components
     * @param componentSymbol             The component symbol
     */
    public void initialize(Supplier<Game> gameSupplier, Function<Game, Integer> getNumberOfRemainingComponents, Group componentSymbol) {
        this.gameSupplier = gameSupplier;
        this.getNumberOfRemainingComponents = getNumberOfRemainingComponents;
        setComponentSymbol(componentSymbol);
        setNumberOfRemainingComponents();
    }

    /**
     * Sets the number of remaining components
     *
     */
    private void setNumberOfRemainingComponents() {
        Game game = gameSupplier.get();
        numberOfComponentsText.setText(String.valueOf(getNumberOfRemainingComponents.apply(game)));
    }

    /**
     * Sets the component symbol
     * @param componentSymbol The component symbol
     */
    private void setComponentSymbol(Group componentSymbol) {
        addComponentSymbolToPane(componentSymbol);
        scaleComponentSymbol(componentSymbol);
    }

    /**
     * Adds the component symbol to the pane
     * @param componentSymbol The component symbol
     */
    private void addComponentSymbolToPane(Group componentSymbol) {
        componentSymbolPane.getChildren().clear();
        componentSymbolPane.getChildren().add(componentSymbol);
    }

    /**
     * Scales the component symbol
     * @param componentSymbol The component symbol
     */
    private void scaleComponentSymbol(Group componentSymbol) {
        scaleComponentSymbolXAxis(componentSymbol);
        scaleComponentSymbolYAxis(componentSymbol);
    }

    /**
     * Scales the component symbol on the x-axis
     * @param componentSymbol The component symbol
     */
    private void scaleComponentSymbolXAxis(Group componentSymbol) {
        componentSymbol.scaleXProperty().bind(
                Bindings.min(
                        componentSymbolPane.widthProperty().multiply(REMAINING_SYMBOL_SCALE_FACTOR),
                        componentSymbolPane.heightProperty().multiply(REMAINING_SYMBOL_SCALE_FACTOR)
                )
        );
    }

    /**
     * Scales the component symbol on the y-axis
     * @param componentSymbol The component symbol
     */
    private void scaleComponentSymbolYAxis(Group componentSymbol) {
        componentSymbol.scaleYProperty().bind(
                Bindings.min(
                        componentSymbolPane.widthProperty().multiply(REMAINING_SYMBOL_SCALE_FACTOR),
                        componentSymbolPane.heightProperty().multiply(REMAINING_SYMBOL_SCALE_FACTOR)
                )
        );
    }

    /**
     * Updates the number of remaining components
     */
    public void updateLabel() {
        setNumberOfRemainingComponents();
    }
}