package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * Popup Controller, responsible for showing a temporary popup that can be closed.
 *
 * @author Oskar Mampe U1564420
 *
 */
public class PopupController {

    @FXML
    Label textLabel;

    @FXML
    Button okButton;

    @FXML
    public void onOkButton() {
        SceneNavigator.closePopupWindow();
    }

    /**
     *
     * Allows an external controller to set the text of the label.
     *
     */
    public void setLabelText(String text) {
        textLabel.setText(text);
    }
}