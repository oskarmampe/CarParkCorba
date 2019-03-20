package controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.Duration;

public class PayController {

    @FXML
    ComboBox<String> timeCmb;

    @FXML
    public void initialize() {
        String[] items = {
                "1 Hour",
                "2 Hours",
                "4 Hours",
                "8 Hours",
                "All Day"
        };
        timeCmb.getItems().setAll(items);
        timeCmb.getSelectionModel().select(0);
    }

    public void onPayClickButton() {
        SceneNavigator.showBasicPopupWindow("Please Pay...");
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished( event -> SceneNavigator.closePopupWindow() );
        delay.play();
    }
}
