package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * Main controller. This is injected using JavaFX from view/main.fxml
 * @author Oskar Mampe: U1564420
 *
 */
public class MainController {

    @FXML
    private StackPane paneHolder;

    /**
     * Replaces the pane displayed with a new pane
     *
     * @param node the pane node to be swapped.
     */
    public void setScene(Pane node) {
        paneHolder.getChildren().setAll(node);
        //scene.getStylesheets().setAll(getClass().getResource("style.css").toExternalForm());
    }
}