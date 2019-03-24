package controller;

import Server.PayStation;
import Server.PayStationHelper;
import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.omg.CosNaming.NameComponent;


public class RegistrationController {

    @FXML
    TextField localServerTxt;

    @FXML
    TextField machineNameTxt;

    public void onRegisterDeviceClickButton() {
        // Get the local server
        NameComponent comp = new NameComponent(localServerTxt.getText(),  "");
        NameComponent path[] = {comp};
        try {
            App.localServer = App.nameContext.resolve(path);
        } catch (Exception e) {
            System.out.println("Error resolving name against Naming Service");
            e.printStackTrace();
        }

        try {
            App.payStation.turn_on(machineNameTxt.getText(), App.rootpoa.servant_to_reference(App.payStation).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SceneNavigator.loadScene(SceneNavigator.PAY);
    }

}
