package com.javaapp3; // تأكد من أن اسم الحزمة صحيح

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.regex.Pattern;

public class TechniciansController {
    @FXML
    private TableView<Technician> techniciansTable;
    @FXML
    private TableColumn<Technician, String> idColumn;
    @FXML
    private TableColumn<Technician, String> firstNameColumn;
    @FXML
    private TableColumn<Technician, String> lastNameColumn;
    @FXML
    private TableColumn<Technician, String> phoneNumberColumn;
    @FXML
    private TableColumn<Technician, String> emailColumn;
    @FXML
    private TableColumn<Technician, String> specialtyColumn;
    @FXML
    private TableColumn<Technician, String> availabilityColumn;
    @FXML
    private TableColumn<Technician, Void> actionsColumn;
    @FXML
    private Button toggleFormButton;
    @FXML
    private VBox technicianForm;
    @FXML
    private TextField technicianIdField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField specialtyField;
    @FXML
    private TextField availabilityField;
    @FXML
    private TextField searchField;
    @FXML
    private Button addTechnicianButton;
    @FXML
    private Button cancelButton;

    private ObservableList<Technician> techniciansList = FXCollections.observableArrayList();
    private FilteredList<Technician> filteredTechnicians;
    private Technician technicianToEdit = null;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,15}$");

    @FXML
    private void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        phoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        specialtyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialty()));
        availabilityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailability()));

        // Initialize actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<Technician, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setStyle("-fx-background-color: #5b2c87; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12;");

                editButton.setOnAction(event -> {
                    Technician technician = getTableView().getItems().get(getIndex());
                    handleEditTechnician(technician);
                });

                deleteButton.setOnAction(event -> {
                    Technician technician = getTableView().getItems().get(getIndex());
                    handleDeleteTechnician(technician);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        // جلب بيانات الفنيين من قاعدة البيانات
        techniciansList.clear();
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT id, first_name, last_name, phone, email, specialty, availability FROM technicians";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    techniciansList.add(new Technician(
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("specialty"),
                        rs.getString("availability")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading technicians from DB: " + e.getMessage());
        }

        // Initialize filtered list
        filteredTechnicians = new FilteredList<>(techniciansList, p -> true);
        techniciansTable.setItems(filteredTechnicians);

        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTechnicians.setPredicate(technician -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.trim().toLowerCase();
                return technician.getAvailability().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    @FXML
    private void handleToggleForm() {
        boolean isVisible = technicianForm.isVisible();
        technicianForm.setVisible(!isVisible);
        technicianForm.setManaged(!isVisible);
        toggleFormButton.setText(isVisible ? "Show Add Form" : "Hide Add Form");
        if (!isVisible) {
            clearForm();
            addTechnicianButton.setText("Add Technician");
            technicianToEdit = null;
        }
    }

    @FXML
    private void handleAddTechnician() {
        String id = technicianIdField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();
        String specialty = specialtyField.getText().trim();
        String availability = availabilityField.getText().trim();

        // Validate required fields
        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || specialty.isEmpty() || availability.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in Technician ID, First Name, Last Name, Specialty, and Availability fields.");
            alert.showAndWait();
            return;
        }

        // Validate email if provided
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Email");
            alert.setContentText("Please enter a valid email address.");
            alert.showAndWait();
            return;
        }

        // Validate phone number if provided
        if (!phoneNumber.isEmpty() && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Phone Number");
            alert.setContentText("Please enter a valid phone number (10-15 digits, optionally starting with +).");
            alert.showAndWait();
            return;
        }

        // Validate availability
        if (!availability.equalsIgnoreCase("Available") && !availability.equalsIgnoreCase("Unavailable")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Availability");
            alert.setContentText("Availability must be 'Available' or 'Unavailable'.");
            alert.showAndWait();
            return;
        }

        // Check for duplicate ID
        if (technicianToEdit == null && techniciansList.stream().anyMatch(t -> t.getId().equals(id))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Duplicate Technician ID");
            alert.setContentText("A technician with this ID already exists.");
            alert.showAndWait();
            return;
        }

        if (technicianToEdit != null) {
            // Update existing technician in database
            try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
                String sql = "UPDATE technicians SET first_name = ?, last_name = ?, phone = ?, email = ?, specialty = ?, availability = ? WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, phoneNumber);
                    stmt.setString(4, email);
                    stmt.setString(5, specialty);
                    stmt.setString(6, availability);
                    stmt.setString(7, id);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Technician '" + id + "' updated in database.");
                    } else {
                        System.err.println("No technician updated in DB. Check ID value.");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error updating technician in DB: " + ex.getMessage());
            }
            technicianToEdit = null;
        } else {
            // Add new technician to database
            try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
                String sql = "INSERT INTO technicians (id, first_name, last_name, phone, email, specialty, availability) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setString(4, phoneNumber);
                    stmt.setString(5, email);
                    stmt.setString(6, specialty);
                    stmt.setString(7, availability);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Technician '" + id + "' added to database.");
                    } else {
                        System.err.println("No technician added to DB. Check values.");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error adding technician to DB: " + ex.getMessage());
            }
        }

        // إعادة تحميل قائمة الفنيين من قاعدة البيانات
        techniciansList.clear();
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT id, first_name, last_name, phone, email, specialty, availability FROM technicians";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    techniciansList.add(new Technician(
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("specialty"),
                        rs.getString("availability")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading technicians from DB: " + e.getMessage());
        }

        // Clear and hide form
        clearForm();
        technicianForm.setVisible(false);
        technicianForm.setManaged(false);
        toggleFormButton.setText("Show Add Form");
        techniciansTable.refresh();
    }

    @FXML
    private void handleCancel() {
        clearForm();
        technicianForm.setVisible(false);
        technicianForm.setManaged(false);
        toggleFormButton.setText("Show Add Form");
        technicianToEdit = null;
    }

    private void handleEditTechnician(Technician technician) {
        technicianToEdit = technician;
        technicianIdField.setText(technician.getId());
        firstNameField.setText(technician.getFirstName());
        lastNameField.setText(technician.getLastName());
        phoneNumberField.setText(technician.getPhoneNumber());
        emailField.setText(technician.getEmail());
        specialtyField.setText(technician.getSpecialty());
        availabilityField.setText(technician.getAvailability());
        addTechnicianButton.setText("Update Technician");
        technicianForm.setVisible(true);
        technicianForm.setManaged(true);
        toggleFormButton.setText("Hide Add Form");
    }

    private void handleDeleteTechnician(Technician technician) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Technician");
        alert.setContentText("Are you sure you want to delete technician " + technician.getFirstName() + " " + technician.getLastName() + "?");
        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                // حذف من قاعدة البيانات
                try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
                    String sql = "DELETE FROM technicians WHERE id = ?";
                    try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, technician.getId());
                        int affectedRows = stmt.executeUpdate();
                        if (affectedRows > 0) {
                            System.out.println("Technician '" + technician.getId() + "' deleted from database.");
                        } else {
                            System.err.println("No technician deleted from DB. Check ID value.");
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error deleting technician from DB: " + ex.getMessage());
                }
                // إعادة تحميل قائمة الفنيين من قاعدة البيانات
                techniciansList.clear();
                try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
                    String sql = "SELECT id, first_name, last_name, phone, email, specialty, availability FROM technicians";
                    try (java.sql.Statement stmt = conn.createStatement();
                         java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            techniciansList.add(new Technician(
                                rs.getString("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("phone"),
                                rs.getString("email"),
                                rs.getString("specialty"),
                                rs.getString("availability")
                            ));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading technicians from DB: " + e.getMessage());
                }
                techniciansTable.refresh();
            }
        });
    }

    private void clearForm() {
        technicianIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        phoneNumberField.clear();
        emailField.clear();
        specialtyField.clear();
        availabilityField.clear();
        addTechnicianButton.setText("Add Technician");
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
    private void handleTechniciansClick() {}
    @FXML
    private void handleLogoutClick() {}

    // Technician class
    public static class Technician {
        private final SimpleStringProperty id;
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty phoneNumber;
        private final SimpleStringProperty email;
        private final SimpleStringProperty specialty;
        private final SimpleStringProperty availability;

        public Technician(String id, String firstName, String lastName, String phoneNumber, String email, String specialty, String availability) {
            this.id = new SimpleStringProperty(id);
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.phoneNumber = new SimpleStringProperty(phoneNumber);
            this.email = new SimpleStringProperty(email);
            this.specialty = new SimpleStringProperty(specialty);
            this.availability = new SimpleStringProperty(availability);
        }

        public String getId() { return id.get(); }
        public void setId(String id) { this.id.set(id); }
        public String getFirstName() { return firstName.get(); }
        public void setFirstName(String firstName) { this.firstName.set(firstName); }
        public String getLastName() { return lastName.get(); }
        public void setLastName(String lastName) { this.lastName.set(lastName); }
        public String getPhoneNumber() { return phoneNumber.get(); }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
        public String getEmail() { return email.get(); }
        public void setEmail(String email) { this.email.set(email); }
        public String getSpecialty() { return specialty.get(); }
        public void setSpecialty(String specialty) { this.specialty.set(specialty); }
        public String getAvailability() { return availability.get(); }
        public void setAvailability(String availability) { this.availability.set(availability); }
    }
}
