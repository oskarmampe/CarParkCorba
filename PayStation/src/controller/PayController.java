package controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class PayController {

    @FXML
    ComboBox<String> timeCmb;

    @FXML
    TextField amountTxt;

    @FXML
    public void initialize() {
        String[] items = {
                "1 Hour",
                "2 Hours",
                "4 Hours",
                "8 Hours",
                "All Day"
        };
        Double[] prices = {
                1.6,
                2.8,
                4.4,
                6.0,
                6.5
        };
        timeCmb.getItems().setAll(items);
        timeCmb.getSelectionModel().select(0);
        timeCmb.valueProperty().addListener((composant, oldString, newString) -> {
            switch (newString){
                case "1 Hour":
                    amountTxt.setText(prices[0].toString());
                    break;
                case "2 Hours":
                    amountTxt.setText(prices[1].toString());
                    break;
                case "4 Hours":
                    amountTxt.setText(prices[2].toString());
                    break;
                case "8 Hours":
                    amountTxt.setText(prices[3].toString());
                    break;
                case "All Day":
                    amountTxt.setText(prices[4].toString());
                    break;
            }
        });
        timeCmb.getSelectionModel().select(1);
    }

    public void onPayClickButton() {
        SceneNavigator.showBasicPopupWindow("Please Pay...");
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished( event -> {
            SceneNavigator.closePopupWindow();
            createPaymentEvent();
        });
        delay.play();
    }

    private void createPaymentEvent() {

    }
}
