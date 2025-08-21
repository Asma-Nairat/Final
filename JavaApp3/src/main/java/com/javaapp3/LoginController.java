package com.javaapp3; // تأكد من أن هذه هي الحزمة الصحيحة

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;

public class LoginController {

    @FXML
    private TextField emailField; // في FXML، هذا الحقل يجب أن يكون fx:id="emailField"

    @FXML
    private PasswordField passwordField; // في FXML، هذا الحقل يجب أن يكون fx:id="passwordField"

    // داخل دالة handleLogin في LoginController.java

@FXML
private void handleLogin() {
    String email = emailField.getText(); // سنفترض أن الأدمن يسجل الدخول باسم مستخدم
    String password = passwordField.getText();

    if (email.isEmpty() || password.isEmpty()) {
        showAlert("Login Failed", "Please enter email and password.", Alert.AlertType.WARNING);
        return;
    }

    // === الكود الصحيح: يبحث في جدول admins عن username ===
    String sql = "SELECT * FROM admins WHERE email = ? AND password = ?";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // تسجيل دخول الأدمن ناجح
            System.out.println("Admin login successful for: " + email);
            
            // افتح واجهة الداشبورد
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml")); 
            Scene dashboardScene = new Scene(dashboardRoot);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard");
            stage.show();
            
        } else {
            showAlert("Login Failed", "The username or password you entered is incorrect.", Alert.AlertType.ERROR);
        }
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Database Error", "An error occurred while connecting to the database.", Alert.AlertType.ERROR);
    }
}

 // هذه الدالة يجب أن تكون موجودة داخل كلاس LoginController.java

/**
 * تعمل عند الضغط على رابط "Sign up".
 * تقوم بإغلاق نافذة تسجيل الدخول وفتح نافذة إنشاء حساب جديد.
 */
@FXML

// هذا الكود يوضع داخل كلاس LoginController.java


private void handleSignupLink(ActionEvent event) {
    try {
        System.out.println("Signup link clicked. Attempting to load signup view using an alternative method...");

        // الحصول على النافذة الحالية
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // === الطريقة البديلة لتحميل ملف FXML ===
        // نستخدم الكلاس الرئيسي للتطبيق (JavaApp3.class) للبحث عن المورد
        // هذا يضمن أننا نستخدم الـ ClassLoader الصحيح.
        URL fxmlLocation = JavaApp3.class.getResource("/fxml/signup.fxml");
        
        // طباعة المسار الذي تم العثور عليه للتأكد
        System.out.println("Found FXML Location: " + fxmlLocation);

        // التحقق من أن الملف تم العثور عليه قبل محاولة تحميله
        if (fxmlLocation == null) {
            System.err.println("CRITICAL: FXML file '/fxml/signup.fxml' could not be found. Please check the path and pom.xml configuration.");
            showAlert("Error", "Could not find the sign-up page resource file.", Alert.AlertType.ERROR);
            return; // إيقاف التنفيذ
        }
        
        // تحميل الواجهة من المسار الذي تم العثور عليه
        Parent signUpRoot = FXMLLoader.load(fxmlLocation);
        // ==========================================
        
        Scene signUpScene = new Scene(signUpRoot);
        currentStage.setScene(signUpScene);
        currentStage.setTitle("Create New Account");

    } catch (IOException e) {
        System.err.println("Failed to load signup.fxml even with the alternative method.");
        e.printStackTrace();
        showAlert("Error", "An I/O error occurred while trying to load the sign-up page.", Alert.AlertType.ERROR);
    }
}
/*
private void handleSignupLink(ActionEvent event) {
    try {
        // طباعة رسالة للتأكد من أن الدالة تعمل
        System.out.println("Signup link clicked. Loading signup view...");

        // الحصول على النافذة الحالية من أي عنصر (مثل الرابط نفسه)
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // تحميل واجهة إنشاء الحساب الجديدة
        // تأكد من أن المسار "/fxml/signup.fxml" صحيح بالنسبة لمشروعك
        Parent signUpRoot = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
        
        // إنشاء مشهد جديد بالواجهة الجديدة
        Scene signUpScene = new Scene(signUpRoot);
        
        // استبدال المشهد الحالي في نفس النافذة
        currentStage.setScene(signUpScene);
        currentStage.setTitle("Create New Account");
        // لا حاجة لـ stage.show() لأن النافذة موجودة بالفعل

    } catch (IOException e) {
        System.err.println("Failed to load signup.fxml");
        e.printStackTrace();
        // يمكنك إظهار رسالة خطأ للمستخدم هنا
        showAlert("Error", "Could not load the sign-up page.", Alert.AlertType.ERROR);
    }
}*/

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}