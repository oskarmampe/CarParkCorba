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

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    @FXML
    Button getTotalButton;

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
                getTotalButton.setVisible(true);
            } else {
                turnOffButton.setVisible(true);
                getTotalButton.setVisible(false);
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
            devicesTree.getSelectionModel().clearSelection();
            item.getParent().getChildren().remove(item);
        }
    }

    @FXML
    public void onSeeFullEventLog() {
        AlarmEvent event = companyHQ.events()[eventLogList.getSelectionModel().getSelectedIndex()];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        Calendar in_event_time = null;
        Calendar parked = null;
        Calendar payTime = null;
        Calendar out_event_time = null;

        if (event.in_event.timestamp != -1) {
            in_event_time = Calendar.getInstance();
            in_event_time.setTimeInMillis((long) event.in_event.timestamp * 1000L);
        }

        if (event.pay_event.parked_for_timestamp != -1) {
            parked = Calendar.getInstance();
            parked.setTimeInMillis((long) event.pay_event.parked_for_timestamp * 1000L);
        }

        if (event.pay_event.timestamp != -1) {
            payTime = Calendar.getInstance();
            payTime.setTimeInMillis((long) event.pay_event.timestamp * 1000L);
        }

        if (event.out_event.timestamp != -1) {
            out_event_time = Calendar.getInstance();
            out_event_time.setTimeInMillis((long) event.out_event.timestamp * 1000L);
        }


        String popupText = "Alarm Raised by registration number: " + event.out_event.registration_number + ", Arrival Time: ";
        popupText += (in_event_time != null ? sdf.format(in_event_time.getTime()) : "None") + ", Departure Time: " +
                (out_event_time != null ? sdf.format(out_event_time.getTime()) : "None");
        if (event.pay_event == null) {
            popupText += ", Pay Event: None";
        } else {
            PayTicket ticket = event.pay_event;
            popupText += ", Pay Time: " + (payTime != null ? sdf.format(payTime.getTime()) : "None") + ", Parked For: "
                    + (parked != null ? sdf.format(parked.getTime()) : "None")
                    + ", Amount Paid: " + ticket.amount;
        }

        SceneNavigator.showBasicPopupWindow(popupText);
    }

    @FXML
    public void onRefreshEventLogList() {
        eventLogList.getItems().clear();
        for (AlarmEvent alarm: companyHQ.events()) {
            eventLogList.getItems().add(alarm.out_event.registration_number);
        }
    }

    public TreeItem<String> makeBranch(String device, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<String>(device);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }

    public void getLocalServerDevices(TreeItem<String> root) {
        try {
            Device device = getDevice(root.getValue());
            System.out.println("Getting devices of: " + device.device_name);
            if (device != null && device.type == DeviceType.local_server) {
                devicesTree.getSelectionModel().clearSelection();
                root.getChildren().clear();
                devicesTree.setDisable(true);
                Device[] devices = companyHQ.get_local_server_devices(device);
                for (Device dev : devices) {
                    makeBranch(dev.device_name, root);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            devicesTree.setDisable(false);
        }
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

    @FXML
    public void onShowLocalTotal() {
        TreeItem<String> item = (TreeItem<String>) devicesTree.getSelectionModel().getSelectedItem();
        Device device = getDevice(item.getValue());

        String popupText = "The total is: ";
        popupText += Double.toString(companyHQ.get_local_server_total(device.device_ior));
        SceneNavigator.showBasicPopupWindow(popupText);
    }

}



