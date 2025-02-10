package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Presenter for the remaining components component
 *
 * @author Silas van Thiel
 * @since 2025-02-07
 */
public class RemainingComponentsPresenter extends AbstractPresenter {

    @FXML
    private StackPane componentSymbolPane;
    @FXML
    private Text numberOfComponentsText;

    private final double REMAINING_SYMBOL_SCALE_FACTOR = 0.027;

    /**
     * Sets the number of remaining components
     * @param numberOfRemainingComponents The number of components remaining
     */
    public void setNumberOfRemainingComponents(int numberOfRemainingComponents) {
        numberOfComponentsText.setText(String.valueOf(numberOfRemainingComponents));
    }

    /**
     * Sets the component symbol
     * @param componentSymbol The component symbol
     */
    public void setComponentSymbol(Group componentSymbol) {
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
     * Returns the FXML file path
     * @return The FXML file path
     */
    @Override
    public String getFXMLFilePath() {
        return "/fxml/game/RemainingComponentsComponent.fxml";
    }
}