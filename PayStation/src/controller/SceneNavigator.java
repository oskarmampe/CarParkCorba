package controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigator {

    public static final String PATH = "../view/";
    public static final String MAIN = PATH+"main.fxml";
    public static final String REGISTRATION = PATH+"registration.fxml";
    public static final String BASIC_POPUP = PATH+"popup.fxml";

    private static MainController mainController;

    public static Stage mPrimaryStage;
    private static Stage mPopupStage;

    public static void setMainController(MainController mainController) {
        SceneNavigator.mainController = mainController;
    }


    public static void loadScene(String fxml) {
        try {
            mainController.setScene(FXMLLoader.load(SceneNavigator.class.getResource(fxml)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showBasicPopupWindow(String labelText) {
        try {
            FXMLLoader loader = new FXMLLoader();

            mPopupStage = new Stage();
            mPopupStage.setResizable(false);
            mPopupStage.initModality(Modality.APPLICATION_MODAL);

            Scene popupScene = new Scene(loader.load(SceneNavigator.class.getResource(BASIC_POPUP).openStream()));
            ((PopupController)loader.getController()).setLabelText(labelText);
            ((PopupController)loader.getController()).okButton.setDisable(true);
            mPopupStage.setScene(popupScene);
            mPopupStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void closePopupWindow() {
        mPopupStage.close();
    }
}