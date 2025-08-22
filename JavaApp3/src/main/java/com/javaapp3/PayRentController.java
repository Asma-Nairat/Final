package com.javaapp3;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PayRentController implements Initializable {

    @FXML private ComboBox<String> apartmentComboBox;
    @FXML private ComboBox<String> rentTypeComboBox;
    @FXML private Label rentPerMonthLabel;
    @FXML private Label totalMonthsLabel;
    @FXML private Label totalRentLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label occupationLabel;
    @FXML private Label addressLabel;
    @FXML private Button confirmPaymentButton;
    @FXML private Button cancelButton;

    private Map<String, Integer> apartmentNameToIdMap = new HashMap<>();
    
    private int currentApartmentId = -1;
    private int currentTenantId = -1;
    private double baseRentAmount = 300.0; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Pay Rent View and Controller loaded.");
        loadRentedApartments();
        rentTypeComboBox.getItems().addAll("1 Month Rent", "2 Months Rent", "3 Months Rent", "6 Months Rent", "12 Months Rent");
        apartmentComboBox.setOnAction(e -> handleApartmentSelection());
        rentTypeComboBox.setOnAction(e -> updateRentCalculation());
        confirmPaymentButton.setOnAction(this::handleConfirmPayment);
        cancelButton.setOnAction(this::handleCancel);
        resetForm();
    }

    private void loadRentedApartments() {
        apartmentComboBox.getItems().clear();
        apartmentNameToIdMap.clear();
        String sql = "SELECT id, name FROM apartments WHERE status = 'Rented' ORDER BY name";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String apartmentName = rs.getString("name");
                int apartmentId = rs.getInt("id");
                apartmentComboBox.getItems().add(apartmentName);
                apartmentNameToIdMap.put(apartmentName, apartmentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void handleApartmentSelection() {
        String selectedApartmentName = apartmentComboBox.getValue();
        if (selectedApartmentName == null) {
            resetForm();
            return;
        }
        this.currentApartmentId = apartmentNameToIdMap.get(selectedApartmentName);
        loadTenantDataForApartment(this.currentApartmentId);
    }
    
    private void loadTenantDataForApartment(int apartmentId) {
        // *** التصحيح الرئيسي هنا: نستخدم tenant_id من جدول apartments ***
        String sql = """
            SELECT t.* 
            FROM tenants t 
            JOIN apartments a ON t.id = a.tenant_id 
            WHERE a.id = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, apartmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                this.currentTenantId = rs.getInt("id");
                fullNameLabel.setText(rs.getString("name"));
                emailLabel.setText(rs.getString("email"));
                phoneLabel.setText(rs.getString("phone"));
                occupationLabel.setText(rs.getString("occupation"));
                addressLabel.setText(rs.getString("address"));
                
                rentTypeComboBox.setDisable(false);
                confirmPaymentButton.setDisable(false);
                
                rentPerMonthLabel.setText(baseRentAmount + " JD");
                updateRentCalculation();
            } else {
                resetForm();
                showAlert("Database Error", "Could not fetch tenant data for the selected apartment.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching tenant data.", Alert.AlertType.ERROR);
        }
    }
    
    private void resetForm() {
        fullNameLabel.setText("-");
        emailLabel.setText("-");
        phoneLabel.setText("-");
        occupationLabel.setText("-");
        addressLabel.setText("-");
        rentPerMonthLabel.setText("-");
        totalMonthsLabel.setText("-");
        totalRentLabel.setText("-");
        rentTypeComboBox.setDisable(true);
        confirmPaymentButton.setDisable(true);
        rentTypeComboBox.getSelectionModel().clearSelection();
    }
    
    private void updateRentCalculation() {
        String selectedRentType = rentTypeComboBox.getValue();
        if (selectedRentType == null) {
            totalMonthsLabel.setText("-");
            totalRentLabel.setText("-");
            return;
        }
        
        int months = Integer.parseInt(selectedRentType.split(" ")[0]);
        totalMonthsLabel.setText(String.valueOf(months));
        
        double totalAmount = baseRentAmount * months;
        totalRentLabel.setText(String.format("%.2f JD", totalAmount));
    }
    
    @FXML
    private void handleConfirmPayment(ActionEvent event) {
        if (currentApartmentId == -1 || currentTenantId == -1) {
            showAlert("Error", "Please select a valid apartment first.", Alert.AlertType.ERROR);
            return;
        }
        
        String selectedRentType = rentTypeComboBox.getValue();
        if (selectedRentType == null) {
            showAlert("Error", "Please select the rent type.", Alert.AlertType.ERROR);
            return;
        }
        
        int months = Integer.parseInt(selectedRentType.split(" ")[0]);
        double totalAmount = baseRentAmount * months;
        
        if (recordPayment(totalAmount, months)) {
            showAlert("Success", String.format("Payment of %.2f JD for %d month(s) has been recorded successfully!", totalAmount, months), Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showAlert("Failed", "An error occurred while recording the payment.", Alert.AlertType.ERROR);
        }
    }
    
    private boolean recordPayment(double amount, int months) {
        String sql = """
            INSERT INTO payments (tenant_id, apartment_id, payment_date, amount, month, 
                                payment_method, payment_status, description) 
            VALUES (?, ?, ?, ?, ?, 'Cash', 'Completed', ?)
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, currentTenantId);
            stmt.setInt(2, currentApartmentId);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setDouble(4, amount);
            
            LocalDate currentDate = LocalDate.now();
            String monthDescription = currentDate.getMonth().toString() + " " + currentDate.getYear();
            if (months > 1) {
                LocalDate endDate = currentDate.plusMonths(months - 1);
                monthDescription += " - " + endDate.getMonth().toString() + " " + endDate.getYear();
            }
            stmt.setString(5, monthDescription);
            stmt.setString(6, String.format("Rent payment for %d month(s)", months));
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (Exception e) {
            System.err.println("Error recording payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
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