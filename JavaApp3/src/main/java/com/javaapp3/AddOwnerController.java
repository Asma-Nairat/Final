package com.javaapp3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddOwnerController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private Button addLandlordButton;

    private OwnersController.Owner ownerToEdit;

    public void setOwnerToEdit(OwnersController.Owner owner) {
        this.ownerToEdit = owner;
        
        // تعبئة الحقول ببيانات المالك الحالي للتعديل
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

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert("Missing Fields", "Please fill in all required fields.");
            return;
        }

        if (ownerToEdit != null) {
            // منطق التحديث: نستخدم id لتحديد السجل
            updateOwner(name, email, phone, address);
        } else {
            // منطق الإضافة: نضيف سجلاً جديداً
            addNewOwner(name, email, phone, address);
        }

        // إغلاق النافذة بعد الإضافة أو التحديث
        closeWindow();
    }

    private void updateOwner(String name, String email, String phone, String address) {
        // *** تعديل: التحديث أصبح الآن باستخدام الـ id الآمن ***
        String sql = "UPDATE owners SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            // *** تعديل: نستخدم id المالك الذي نقوم بتعديله ***
            stmt.setInt(5, ownerToEdit.getId()); 
            
            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Database Error", "Failed to update owner.");
        }
    }

    private void addNewOwner(String name, String email, String phone, String address) {
        // ملاحظة: هذا الاستعلام يفترض عدم إدخال كلمة المرور من هذه الشاشة
        String sql = "INSERT INTO owners (name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);

            stmt.executeUpdate();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Database Error", "Failed to add new owner.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) addLandlordButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}