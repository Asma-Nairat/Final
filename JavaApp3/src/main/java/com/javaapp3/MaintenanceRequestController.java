package com.javaapp3;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MaintenanceRequestController {
    @FXML
    private TableView<MaintenanceRequest> requestsTable;
    @FXML
    private TableColumn<MaintenanceRequest, String> idColumn;
    @FXML
    private TableColumn<MaintenanceRequest, String> apartmentIdColumn;
    @FXML
    private TableColumn<MaintenanceRequest, String> tenantIdColumn;
    @FXML
    private TableColumn<MaintenanceRequest, String> dateColumn;
    @FXML
    private TableColumn<MaintenanceRequest, String> descriptionColumn;
    @FXML
    private TableColumn<MaintenanceRequest, String> statusColumn;
    @FXML
    private TableColumn<MaintenanceRequest, Double> costColumn;
    @FXML
    private TableColumn<MaintenanceRequest, Void> actionsColumn;
    @FXML
    private Button toggleFormButton;
    @FXML
    private VBox requestForm;
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
    private Button addRequestButton;
    @FXML
    private Button cancelButton;

    private ObservableList<MaintenanceRequest> requestsList = FXCollections.observableArrayList();
    private MaintenanceRequest requestToEdit = null;

    @FXML
    private void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        apartmentIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApartmentId()));
        tenantIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenantId()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequestDate()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        costColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCost()).asObject());

        // Initialize actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<MaintenanceRequest, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button resolveButton = new Button("Resolve");

            {
                editButton.setStyle("-fx-background-color: #5b2c87; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12;");
                resolveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12;");

                editButton.setOnAction(event -> {
                    MaintenanceRequest request = getTableView().getItems().get(getIndex());
                    handleEditRequest(request);
                });

                resolveButton.setOnAction(event -> {
                    MaintenanceRequest request = getTableView().getItems().get(getIndex());
                    handleResolveRequest(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, resolveButton);
                    setGraphic(buttons);
                }
            }
        });

        // Sample data
        requestsList.addAll(
            new MaintenanceRequest("REQ001", "APT001", "TEN001", "2025-08-01", "Leaky faucet", "Pending", 50.0),
            new MaintenanceRequest("REQ002", "APT002", "TEN002", "2025-08-02", "Broken window", "Resolved", 200.0)
        );
        requestsTable.setItems(requestsList);
    }

    @FXML
    private void handleToggleForm() {
        boolean isVisible = requestForm.isVisible();
        requestForm.setVisible(!isVisible);
        requestForm.setManaged(!isVisible);
        toggleFormButton.setText(isVisible ? "Show Add Form" : "Hide Add Form");
        if (!isVisible) {
            clearForm();
            addRequestButton.setText("Add Request");
            requestToEdit = null;
        }
    }

    @FXML
    private void handleAddRequest() {
        String apartmentId = apartmentIdField.getText().trim();
        String tenantId = tenantIdField.getText().trim();
        String date = dateField.getText().trim();
        String description = descriptionField.getText().trim();
        String status = statusField.getText().trim();
        String costText = costField.getText().trim();
        double cost;

        // Validate required fields
        if (apartmentId.isEmpty() || tenantId.isEmpty() || date.isEmpty() || description.isEmpty() || status.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in Apartment ID, Tenant ID, Date, Description, and Status fields.");
            alert.showAndWait();
            return;
        }

        // Validate cost
        try {
            cost = costText.isEmpty() ? 0.0 : Double.parseDouble(costText);
            if (cost < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Cost");
                alert.setContentText("Cost cannot be negative.");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Cost");
            alert.setContentText("Please enter a valid number for Cost.");
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
            requestToEdit = null;
        } else {
            // Add new request
            String newId = "REQ" + (requestsList.size() + 1);
            MaintenanceRequest newRequest = new MaintenanceRequest(newId, apartmentId, tenantId, date, description, status, cost);
            requestsList.add(newRequest);
        }

        // Clear and hide form
        clearForm();
        requestForm.setVisible(false);
        requestForm.setManaged(false);
        toggleFormButton.setText("Show Add Form");
        requestsTable.refresh();
    }

    @FXML
    private void handleCancel() {
        clearForm();
        requestForm.setVisible(false);
        requestForm.setManaged(false);
        toggleFormButton.setText("Show Add Form");
        requestToEdit = null;
    }

    private void handleEditRequest(MaintenanceRequest request) {
        requestToEdit = request;
        apartmentIdField.setText(request.getApartmentId());
        tenantIdField.setText(request.getTenantId());
        dateField.setText(request.getRequestDate());
        descriptionField.setText(request.getDescription());
        statusField.setText(request.getStatus());
        costField.setText(String.valueOf(request.getCost()));
        addRequestButton.setText("Update Request");
        requestForm.setVisible(true);
        requestForm.setManaged(true);
        toggleFormButton.setText("Hide Add Form");
    }

    private void handleResolveRequest(MaintenanceRequest request) {
        request.setStatus("Resolved");
        requestsTable.refresh();
    }

    private void clearForm() {
        apartmentIdField.clear();
        tenantIdField.clear();
        dateField.clear();
        descriptionField.clear();
        statusField.clear();
        costField.clear();
        addRequestButton.setText("Add Request");
    }

    @FXML
    private void handleDashboardClick() {}
    @FXML
    private void handleOwnersClick() {}
    @FXML
    private void handleTenantsClick() {}
    @FXML
    private void handleApartmentsClick() {}
    @FXML
    private void handleHousesClick() {}
    @FXML
    private void handleAssignHouseClick() {}
    @FXML
    private void handlePaymentsClick() {}
    @FXML
    private void handlePayRentClick() {}
    @FXML
    private void handleMaintenanceClick() {}
    @FXML
    private void handleLogoutClick() {}

    // MaintenanceRequest class
    public static class MaintenanceRequest {
        private final SimpleStringProperty id;
        private final SimpleStringProperty apartmentId;
        private final SimpleStringProperty tenantId;
        private final SimpleStringProperty requestDate;
        private final SimpleStringProperty description;
        private final SimpleStringProperty status;
        private final SimpleDoubleProperty cost;

        public MaintenanceRequest(String id, String apartmentId, String tenantId, String requestDate, String description, String status, double cost) {
            this.id = new SimpleStringProperty(id);
            this.apartmentId = new SimpleStringProperty(apartmentId);
            this.tenantId = new SimpleStringProperty(tenantId);
            this.requestDate = new SimpleStringProperty(requestDate);
            this.description = new SimpleStringProperty(description);
            this.status = new SimpleStringProperty(status);
            this.cost = new SimpleDoubleProperty(cost);
        }

        public String getId() { return id.get(); }
        public void setId(String id) { this.id.set(id); }
        public String getApartmentId() { return apartmentId.get(); }
        public void setApartmentId(String apartmentId) { this.apartmentId.set(apartmentId); }
        public String getTenantId() { return tenantId.get(); }
        public void setTenantId(String tenantId) { this.tenantId.set(tenantId); }
        public String getRequestDate() { return requestDate.get(); }
        public void setRequestDate(String requestDate) { this.requestDate.set(requestDate); }
        public String getDescription() { return description.get(); }
        public void setDescription(String description) { this.description.set(description); }
        public String getStatus() { return status.get(); }
        public void setStatus(String status) { this.status.set(status); }
        public double getCost() { return cost.get(); }
        public void setCost(double cost) { this.cost.set(cost); }
    }
}
