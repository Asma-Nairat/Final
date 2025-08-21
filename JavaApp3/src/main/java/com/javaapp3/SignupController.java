package com.javaapp3;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    // الربط مع الحقول في ملف FXML
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    /**
     * تعمل عند الضغط على زر "Sign Up".
     */
    @FXML
    private void handleSignUpButton(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // التحقق من أن الحقول ليست فارغة
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Sign-Up Failed", "Email and password cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        // جملة SQL الصحيحة للتعامل مع جدول admins
        String sql = "INSERT INTO admins (email, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password); // هام: يجب تشفيره لاحقاً

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                showAlert("Success", "Admin account created successfully! You can now log in.", Alert.AlertType.INFORMATION);
                // بعد النجاح، نعود إلى شاشة تسجيل الدخول
                handleLoginLink(event);
            } else {
                showAlert("Sign-Up Failed", "An unexpected error occurred. Account was not created.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            // خطأ "23505" هو رمز تكرار القيد (unique constraint) في PostgreSQL
            if (e.getSQLState().equals("23505")) { 
                showAlert("Sign-Up Failed", "This email is already registered.", Alert.AlertType.ERROR);
            } else {
                showAlert("Database Error", "An error occurred while creating the account.", Alert.AlertType.ERROR);
            }
            e.printStackTrace();
        }
    }
    
    /**
     * تعمل عند الضغط على رابط "Already have an account? Log in".
     * تعود إلى شاشة تسجيل الدخول.
     */
    @FXML
    private void handleLoginLink(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml")); // تأكد من المسار
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}