package controller;

import Server.Device;
import Server.DeviceType;
import application.App;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class ListDevicesController {

    @FXML
    TreeView devicesTree;

    private TreeItem<String> root;

    @FXML
    public void initialize() {
        root = new TreeItem<>("Devices");
        root.setExpanded(true);

        devicesTree.setRoot(root);
        devicesTree.setShowRoot(false);

        for (Device device: App.companyHQ.all_devices()) {
            if(device.type == DeviceType.local_server) {
                makeBranch(device.device_name, root);
            }
        }

        devicesTree.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            TreeItem<String> treeItem = (TreeItem) newValue;
            System.out.println(treeItem.getValue().toString());
            if (newValue == root) {

            }
            Device device = getDevice(((TreeItem) newValue).getValue().toString());
        });
    }

    @FXML
    void onClickButton() {
        App.companyHQ.get_local_server_devices(App.companyHQ.all_devices()[0]);
    }

    @FXML
    void onRefreshLocalServer() {
        for (Device device: App.companyHQ.all_devices()) {
            if(device.type == DeviceType.local_server) {
                makeBranch(device.device_name, root);
            }
        }
    }

    public TreeItem<String> makeBranch(String device, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<String>(device);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }

    public Device getDevice(String deviceName) {
        for (Device device: App.companyHQ.all_devices()) {
            if(device.device_name.equals(deviceName)) {
                return device;
            }
        }
        return null;
    }

}



