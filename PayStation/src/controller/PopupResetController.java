package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 *
 * PopupResetController main.controller. This is injected using JavaFX from main.view/popup_reset.fxml
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public class PopupResetController {

    @FXML
    TextField machineNameTxt;

    @FXML
    public void onResetButton() {
        App.payStation.reset(machineNameTxt.getText(), App.payStation.ior);

        SceneNavigator.closePopupWindow();
    }

}
