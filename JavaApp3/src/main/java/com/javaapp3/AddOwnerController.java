package com.javaapp3; // تأكد من أن اسم الحزمة صحيح
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddOwnerController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea addressField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addLandlordButton;

    private ObservableList<OwnersController.Owner> ownersList;
    private OwnersController.Owner ownerToEdit;

    public void setOwnersList(ObservableList<OwnersController.Owner> ownersList) {
        this.ownersList = ownersList;
    }

    public void setOwnerToEdit(OwnersController.Owner owner) {
        this.ownerToEdit = owner;
        nameField.setText(owner.getName());
        emailField.setText(owner.getEmail());
        phoneField.setText(owner.getPhone());
        addressField.setText(owner.getAddress());
        addLandlordButton.setText("Update Owner");
    }

    @FXML
    private void handleAddLandlord() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // Validate required fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in Name, Email, and Phone fields.");
            alert.showAndWait();
            return;
        }

        if (ownerToEdit != null) {
            // Update existing owner in database
            try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
                String sql = "UPDATE owners SET email = ?, phone = ?, address = ? WHERE name = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, email);
                    stmt.setString(2, phone);
                    stmt.setString(3, address);
                    stmt.setString(4, name);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Owner '" + name + "' updated in database.");
                    } else {
                        System.err.println("No owner updated in DB. Check name value.");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error updating owner in DB: " + ex.getMessage());
            }
        } else {
            // Add new owner to database
            try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
                String sql = "INSERT INTO owners (name, email, phone, address) VALUES (?, ?, ?, ?)";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, phone);
                    stmt.setString(4, address);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Owner '" + name + "' added to database.");
                    } else {
                        System.err.println("No owner added to DB. Check values.");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error adding owner to DB: " + ex.getMessage());
            }
        }

        // Close the window
        Stage stage = (Stage) addLandlordButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
