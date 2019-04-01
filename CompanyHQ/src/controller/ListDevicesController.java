package controller;

import Server.AlarmEvent;
import Server.Device;
import Server.DeviceType;
import Server.PayTicket;
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

import static application.App.companyHQ;

public class ListDevicesController {

    @FXML
    TreeView devicesTree;

    @FXML
    Button turnOffButton;

    @FXML
    ListView eventLogList;

    @FXML
    Label selectedItemLabel;

    private TreeItem<String> root;

    @FXML
    public void initialize() {
        root = new TreeItem<>("Devices");
        root.setExpanded(true);

        devicesTree.setRoot(root);
        devicesTree.setShowRoot(false);

        onRefreshLocalServer();


        devicesTree.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            TreeItem<String> treeItem = (TreeItem<String>) newValue;
            Device device = getDevice(treeItem.getValue());
            System.out.println(treeItem.getValue());
            if(device == null || device.type == DeviceType.local_server) {
                turnOffButton.setVisible(false);
            } else {
                turnOffButton.setVisible(true);
            }

            if (treeItem.getChildren().size() == 0 && device.type == DeviceType.local_server)
                getLocalServerDevices(treeItem);
        });
    }

    @FXML
    void onRefreshDevice() {
        TreeItem<String> item = (TreeItem<String>) devicesTree.getSelectionModel().getSelectedItem();
        TreeItem<String> parent = item.getParent();
        Device device = getDevice(item.getValue());
        if (device != null) {
            getLocalServerDevices(device.type == DeviceType.local_server ? item : item.getParent());
        }
    }

    @FXML
    void onRefreshLocalServer() {
        for (Device device: companyHQ.all_devices()) {
            if(device.type == DeviceType.local_server) {
                makeBranch(device.device_name, root);
            }
        }
    }

    @FXML
    public void onDeviceTurnOff() {
        TreeItem<String> item = (TreeItem<String>) devicesTree.getSelectionModel().getSelectedItem();
        Device device = getDevice(item.getValue());
        if (device != null && device.type != DeviceType.local_server) {
            companyHQ.turn_off_device(device.device_ior);
            item.getParent().getChildren().remove(item);
        }
    }

    @FXML
    public void onSeeFullEventLog() {
        AlarmEvent event = companyHQ.events()[eventLogList.getSelectionModel().getSelectedIndex()];
        String popupText = "Alarm Raised by reigstration number" + event.in_event.registration_number + ", Arrival Time: ";
        popupText += event.in_event.timestamp + ", Departure Time: " + event.out_event.timestamp;
        if (event.pay_event == null) {
            popupText += ", Pay Event: None";
        } else {
            PayTicket ticket = event.pay_event;
            popupText += ", Pay Time: " + ticket.timestamp + ", Parked For: " + ticket.parked_for_timestamp
                    + ", Amount Paid: " + ticket.amount;
        }

        SceneNavigator.showBasicPopupWindow(popupText);
    }

    @FXML
    public void onRefreshEventLogList() {
        eventLogList.getItems().clear();
        for (AlarmEvent alarm: companyHQ.events()) {
            eventLogList.getItems().add("Plate Number: "+alarm.in_event.registration_number);
        }
    }

    public TreeItem<String> makeBranch(String device, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<String>(device);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }

    public void getLocalServerDevices(TreeItem<String> root) {
        Device device = getDevice(root.getValue());
        System.out.println("Getting devices of: "+device.device_name);
        if (device != null && device.type == DeviceType.local_server) {
            devicesTree.getSelectionModel().clearSelection();
            root.getChildren().clear();
            devicesTree.setDisable(true);
            Device[] devices = companyHQ.get_local_server_devices(device);
            for (Device dev: devices) {
                makeBranch(dev.device_name, root);
            }
        }
        devicesTree.setDisable(false);
    }

    public Device getDevice(String deviceName) {
        for (Device device: companyHQ.all_devices()) {
            if(device.device_name.equals(deviceName)) {
                return device;
            }

            if(device.type == DeviceType.local_server) {
                Device[] devices = companyHQ.getLocalServerDevices(device);
                for (Device dev: devices) {
                    if(dev.device_name.equals(deviceName)) {
                        return dev;
                    }
                }
            }
        }
        return null;
    }

}



