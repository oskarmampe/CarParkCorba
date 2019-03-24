package controller;

import application.App;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class PayController {

    @FXML
    ComboBox<String> timeCmb;

    @FXML
    TextField amountTxt;

    @FXML
    TextField plateNumberTxt;

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

        String time = timeCmb.getItems().get(timeCmb.getSelectionModel().getSelectedIndex());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        switch (time){
            case "1 Hour":
                c.add(Calendar.HOUR, 1);
                break;
            case "2 Hours":
                c.add(Calendar.HOUR, 2);
                break;
            case "4 Hours":
                c.add(Calendar.HOUR, 4);
                break;
            case "8 Hours":
                c.add(Calendar.HOUR, 8);
                break;
            case "All Day":
                c.add(Calendar.HOUR, 24);
                break;
        }
        System.out.println("Current Time: " + sdf.format(System.currentTimeMillis()));
        System.out.println("Parked for: " + sdf.format(c.getTime()));
        App.payStation.pay(plateNumberTxt.getText(), Double.parseDouble(amountTxt.getText()), (int)(c.getTimeInMillis() / 1000L), (int) (System.currentTimeMillis() / 1000L));
    }

    private void createPaymentEvent() {

    }
}
