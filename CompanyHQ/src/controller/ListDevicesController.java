package controller;

import Server.AlarmEvent;
import Server.Device;
import Server.DeviceType;
import Server.PayTicket;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static application.App.companyHQ;

/**
 *
 * List Devices controller responsible for taking any inputs from the 'devices_list' view.
 *
 * @author Oskar Mampe
 *
 */
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

        onRefreshLocalServer(); // Show any local servers already connected

        // Add a listener when the user presses on a list item, load all the devices connected to the server
        // Otherwise, show appropriate buttons, depending on what the user selected
        devicesTree.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            TreeItem<String> treeItem = (TreeItem<String>) newValue;
            if (treeItem != null && treeItem.getValue() != null) {
                Device device = getDevice(treeItem.getValue());
                if (device == null || device.type == DeviceType.local_server) {
                    // If device is a local server, allow to get total earned today
                    turnOffButton.setVisible(false);
                    getTotalButton.setVisible(true);
                } else {
                    // If device is not a local server, allow to turn it off
                    turnOffButton.setVisible(true);
                    getTotalButton.setVisible(false);
                }


                if (device != null) {
                    String deviceType = "";
                    if (device.type == DeviceType.local_server) {
                        deviceType = "Local Server";
                    } else if (device.type == DeviceType.paystation){
                        deviceType = "Pay Station";
                    } else if (device.type == DeviceType.entry_gate){
                        deviceType = "Entry Gate";
                    } else if (device.type == DeviceType.exit_gate) {
                        deviceType = "Exit Gate";
                    } else {
                        deviceType = "N/A";
                    }

                    selectedItemLabel.setText("Selected: " + device.device_name + " Type: " + deviceType);
                } else {
                    selectedItemLabel.setText("Selected: N/A Type: N/A");
                }

                // If a local server doesn't have children, allow to retrieve them
                if (treeItem.getChildren().size() == 0 && device.type == DeviceType.local_server)
                    getLocalServerDevices(treeItem);
            }
        });
    }

    @FXML
    void onRefreshDevice() {
        TreeItem<String> item = (TreeItem<String>) devicesTree.getSelectionModel().getSelectedItem();
        Device device = getDevice(item.getValue());
        if (device != null) {
            getLocalServerDevices(device.type == DeviceType.local_server ? item : item.getParent());
        }
    }

    @FXML
    void onRefreshLocalServer() {
        root.getChildren().clear();
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
            devicesTree.getSelectionModel().clearSelection(); // Clear the selection first to avoid any weird visuals
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

        // If a time doesn't exist, the server sends -1 instead of null, as sending null from CORBA caused some issues.
        // Therefore, if a user has not parked, then both parked_timestamp and timestamp will be -1
        // This will cause errors when parsing it as a timestamp date.
        // Therefore check for these errors to avoid any crashes.
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

        // popup text is text parsed to the popup to be displayed as a label.
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
        for (AlarmEvent alarm: companyHQ.events())
            eventLogList.getItems().add(alarm.out_event.registration_number);
    }

    @FXML
    public void onShowLocalTotal() {
        TreeItem<String> item = (TreeItem<String>) devicesTree.getSelectionModel().getSelectedItem();
        Device device = getDevice(item.getValue());
        String popupText;

        if (device != null) {
            popupText = "The total is: ";
            popupText += Double.toString(companyHQ.get_local_server_total(device.device_ior));
        } else {
            popupText = "Error.";
        }

        SceneNavigator.showBasicPopupWindow(popupText);
    }

    /**
     *
     * JavaFX method to create a branch on the root.
     *
     * @param device Device to get added to the root
     * @param parent TreeItem root to have its children appended
     */
    private void makeBranch(String device, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<String>(device);
        item.setExpanded(true);
        parent.getChildren().add(item);
    }

    /**
     *
     *  Method that clears any children of a local server root, and then gets all devices connected.
     *
     * @param root TreeItem the element that holds the local server
     */
    private void getLocalServerDevices(TreeItem<String> root) {
        try {
            Device device = getDevice(root.getValue());
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

    /**
     *
     * Takes in a device name and retrieves the appropriate Device object.
     *
     * @param deviceName the device name of Device
     * @return Device connected to the local server or company hq
     */
    private Device getDevice(String deviceName) {
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



