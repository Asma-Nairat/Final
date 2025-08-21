package com.javaapp3;

import javafx.collections.ObservableList;
import com.javaapp3.MaintenanceRequestController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddMaintenanceRequestController {
    @FXML
    private TextField apartmentIdField;
    @FXML
    private TextField tenantIdField;
    @FXML
    private TextField dateField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField statusField;
    @FXML
    private TextField costField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addRequestButton;

private ObservableList<MaintenanceRequestController.MaintenanceRequest> requestsList;
private MaintenanceRequestController.MaintenanceRequest requestToEdit;

public void setRequestsList(ObservableList<MaintenanceRequestController.MaintenanceRequest> requestsList) {
    this.requestsList = requestsList;
}

public void setRequestToEdit(MaintenanceRequestController.MaintenanceRequest request) {
    this.requestToEdit = request;
    apartmentIdField.setText(request.getApartmentId());
    tenantIdField.setText(request.getTenantId());
    dateField.setText(request.getRequestDate());
    descriptionField.setText(request.getDescription());
    statusField.setText(request.getStatus());
    costField.setText(String.valueOf(request.getCost()));
    addRequestButton.setText("Update Request");
}

    @FXML
    private void handleAddRequest() {
        String apartmentId = apartmentIdField.getText().trim();
        String tenantId = tenantIdField.getText().trim();
        String date = dateField.getText().trim();
        String description = descriptionField.getText().trim();
        String status = statusField.getText().trim();
        String costText = costField.getText().trim();
        double cost = costText.isEmpty() ? 0.0 : Double.parseDouble(costText);

        // Validate required fields
        if (apartmentId.isEmpty() || tenantId.isEmpty() || date.isEmpty() || description.isEmpty() || status.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in Apartment ID, Tenant ID, Date, Description, and Status fields.");
            alert.showAndWait();
            return;
        }

        if (requestToEdit != null) {
            // Update existing request
            requestToEdit.setApartmentId(apartmentId);
            requestToEdit.setTenantId(tenantId);
            requestToEdit.setRequestDate(date);
            requestToEdit.setDescription(description);
            requestToEdit.setStatus(status);
            requestToEdit.setCost(cost);
        } else {
            // Add new request
            String newId = "REQ" + (requestsList.size() + 1);
MaintenanceRequestController.MaintenanceRequest newRequest = new MaintenanceRequestController.MaintenanceRequest(
    newId, apartmentId, tenantId, date, description, status, cost
);
requestsList.add(newRequest);
        }

        // Close the window
        Stage stage = (Stage) addRequestButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
