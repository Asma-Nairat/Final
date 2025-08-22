package com.javaapp3;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AssignApartmentController implements Initializable {

    @FXML private ComboBox<String> selectApartmentComboBox;
    @FXML private ComboBox<String> selectTenantComboBox;
    @FXML private Label apartmentAddressLabel;
    @FXML private Label apartmentStatusLabel;
    @FXML private Label apartmentRentLabel;
    @FXML private Label tenantNameLabel;
    @FXML private Label tenantEmailLabel;
    @FXML private Label tenantPhoneLabel;
    @FXML private TextField depositAmountField;
    @FXML private TextField placementFeeField;
    @FXML private Button assignButton; 
    @FXML private Button cancelButton;

    private Map<String, Integer> apartmentMap = new HashMap<>();
    private Map<String, Integer> tenantMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Assign Apartment View and Controller loaded.");
        
        loadAvailableApartments();
        loadAvailableTenants();
        
        selectApartmentComboBox.setOnAction(e -> updateApartmentDetails());
        selectTenantComboBox.setOnAction(e -> updateTenantDetails());
    }

    private void loadAvailableApartments() {
        String sql = "SELECT id, name, town FROM apartments WHERE status = 'Available'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            selectApartmentComboBox.getItems().clear();
            apartmentMap.clear();
            
            while (rs.next()) {
                String displayText = rs.getString("name") + " (" + rs.getString("town") + ")";
                selectApartmentComboBox.getItems().add(displayText);
                apartmentMap.put(displayText, rs.getInt("id"));
            }
        } catch (Exception e) {
            System.err.println("Error loading available apartments: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadAvailableTenants() {
        // *** تعديل منطقي: جلب كل المستأجرين النشطين ***
        // بما أن المستأجر يمكنه استئجار أكثر من شقة، لا نحتاج لفلترة معقدة
        String sql = "SELECT id, name FROM tenants WHERE status = 'Active' ORDER BY name";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            selectTenantComboBox.getItems().clear();
            tenantMap.clear();

            while (rs.next()) {
                String displayText = rs.getString("name");
                selectTenantComboBox.getItems().add(displayText);
                tenantMap.put(displayText, rs.getInt("id"));
            }
        } catch (Exception e) {
            System.err.println("Error loading available tenants: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateApartmentDetails() {
        String selected = selectApartmentComboBox.getValue();
        if (selected == null) return;
        apartmentAddressLabel.setText(selected);
        apartmentStatusLabel.setText("Available");
    }

    private void updateTenantDetails() {
        String selected = selectTenantComboBox.getValue();
        if (selected == null) return;
        
        int tenantId = tenantMap.get(selected);
        String sql = "SELECT name, email, phone FROM tenants WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tenantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tenantNameLabel.setText(rs.getString("name"));
                tenantEmailLabel.setText(rs.getString("email"));
                tenantPhoneLabel.setText(rs.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  

    @FXML
    private void handleAssignApartment(ActionEvent event) {
        String selectedApartment = selectApartmentComboBox.getValue();
        String selectedTenant = selectTenantComboBox.getValue();
        String depositText = depositAmountField.getText().trim();
        String placementFeeText = placementFeeField.getText().trim();

        if (selectedApartment == null || selectedTenant == null || depositText.isEmpty()) {
            showAlert("Input Error", "Please select an apartment, a tenant, and enter the deposit amount.", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            int apartmentId = apartmentMap.get(selectedApartment);
            int tenantId = tenantMap.get(selectedTenant);
            double deposit = Double.parseDouble(depositText);
            double placementFee = placementFeeText.isEmpty() ? 0.0 : Double.parseDouble(placementFeeText);

            if (assignInDatabase(apartmentId, tenantId, deposit, placementFee)) {
                showAlert("Success", "Apartment assigned successfully!", Alert.AlertType.INFORMATION);
                closeWindow();
            } else {
                showAlert("Database Error", "Failed to assign apartment.", Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid number for amounts.", Alert.AlertType.ERROR);
        }
    }

    private boolean assignInDatabase(int apartmentId, int tenantId, double deposit, double placementFee) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // بداية المعاملة

            // *** تعديل: تحديث جدول الشقق مباشرةً ***
            // 1. تحديث حالة الشقة إلى "Rented" وإضافة tenant_id
            String updateAptSql = "UPDATE apartments SET status = 'Rented', tenant_id = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateAptSql)) {
                stmt.setInt(1, tenantId);
                stmt.setInt(2, apartmentId);
                stmt.executeUpdate();
            }
            
            // 2. تسجيل دفعة التأمين (تبقى كما هي)
            if (deposit > 0) {
                String paymentSql = "INSERT INTO payments (tenant_id, apartment_id, payment_date, amount, month, payment_method, payment_status, description) VALUES (?, ?, CURRENT_DATE, ?, 'N/A', 'Cash', 'Completed', 'Security Deposit')";
                try (PreparedStatement stmt = conn.prepareStatement(paymentSql)) {
                    stmt.setInt(1, tenantId);
                    stmt.setInt(2, apartmentId);
                    stmt.setDouble(3, deposit); 
                    stmt.executeUpdate();
                }
            }
            
            // 3. تسجيل رسوم التسكين (Placement Fee) (تبقى كما هي)
            if (placementFee > 0) {
                String paymentSql = "INSERT INTO payments (tenant_id, apartment_id, payment_date, amount, month, payment_method, payment_status, description) VALUES (?, ?, CURRENT_DATE, ?, 'N/A', 'Cash', 'Completed', 'Placement Fee')";
                try (PreparedStatement stmt = conn.prepareStatement(paymentSql)) {
                    stmt.setInt(1, tenantId);
                    stmt.setInt(2, apartmentId);
                    stmt.setDouble(3, placementFee); 
                    stmt.executeUpdate();
                }
            }
            
            conn.commit(); // إتمام المعاملة بنجاح
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            System.err.println("Error in assignment transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    @FXML
    private void handleCancelAssignment(ActionEvent event) {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}