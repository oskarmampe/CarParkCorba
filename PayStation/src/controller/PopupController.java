package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PopupController {

    @FXML
    Label textLabel;

    @FXML
    Button okButton;

    public void onOkButton() {
        SceneNavigator.closePopupWindow();
    }

    public void setLabelText(String text) {
        textLabel.setText(text);
    }
}