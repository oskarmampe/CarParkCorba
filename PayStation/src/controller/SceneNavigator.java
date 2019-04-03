package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * SceneNavigator main.controller. This class is used to switch scenes. Holds all possible views to navigate to.
 * @author Oskar Mampe: U1564420
 *
 */
public class SceneNavigator {

    private static final String PATH = "../view/";
    public static final String MAIN = PATH+"main.fxml";
    public static final String REGISTRATION = PATH+"registration.fxml";
    public static final String PAY = PATH+"pay.fxml";
    public static final String BASIC_POPUP = PATH+"popup.fxml";
    public static final String RESET_POPUP = PATH+"popup_reset.fxml";

    private static MainController mainController;

    public static Stage mPrimaryStage;
    private static Stage mPopupStage;

    /**
     *
     * Sets the main controller.
     *
     * @param mainController the {@link MainController} injected into main.view/main.fxml
     */
    public static void setMainController(MainController mainController) {
        SceneNavigator.mainController = mainController;
    }

    /**
     *
     * Loads an fxml view.
     *
     * @param fxml {@link String} resource path to the fxml file
     */
    public static void loadScene(String fxml) {
        try {
            mainController.setScene(FXMLLoader.load(SceneNavigator.class.getResource(fxml)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Shows a basic popup window with a label.
     *
     * @param labelText {@link String} text of a label
     */
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

    /**
     *
     * Close the popup window.
     *
     */
    public static void closePopupWindow() {
        mPopupStage.close();
    }

    /**
     *
     * Show a popup window.
     *
     * @param fxml {@link String} resource path to the fxml file
     */
    public static void showPopupWindow(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader();

            mPopupStage = new Stage();
            mPopupStage.setResizable(false);
            mPopupStage.initModality(Modality.APPLICATION_MODAL);

            Scene popupScene = new Scene(loader.load(SceneNavigator.class.getResource(fxml).openStream()));
            mPopupStage.setScene(popupScene);
            mPopupStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}