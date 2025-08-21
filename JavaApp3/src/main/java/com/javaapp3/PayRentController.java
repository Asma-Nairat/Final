package com.javaapp3;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.time.LocalDate;

public class PayRentController implements Initializable {

    // --- عناصر البحث ---
    @FXML private TextField searchApartmentIdField;
    @FXML private Button searchButton;

    // --- عناصر حساب الدفعة ---
    @FXML private ComboBox<String> rentTypeComboBox;
    @FXML private Label rentPerMonthLabel;
    @FXML private Label totalMonthsLabel;
    @FXML private Label totalRentLabel;

    // --- عناصر تفاصيل المستأجر ---
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label occupationLabel;
    @FXML private Label addressLabel;
    
    // --- أزرار التحكم ---
    @FXML private Button confirmPaymentButton;
    @FXML private Button cancelButton;

    // متغيرات للبيانات
    private int currentApartmentId = -1;
    private int currentTenantId = -1;
    private double baseRentAmount = 300.0; // افتراضي - يمكن جلبه من قاعدة البيانات

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Pay Rent View and Controller loaded.");
        
        // تعبئة قائمة أنواع الإيجار
        rentTypeComboBox.getItems().addAll(
                "1 Month Rent",
                "2 Months Rent", 
                "3 Months Rent",
                "6 Months Rent",
                "12 Months Rent"
        );
        
        // إضافة مستمع لتحديث الحسابات عند تغيير نوع الإيجار
        rentTypeComboBox.setOnAction(e -> updateRentCalculation());
        
        // ربط زر البحث
        searchButton.setOnAction(this::handleSearchApartment);
        confirmPaymentButton.setOnAction(this::handleConfirmPayment);
        cancelButton.setOnAction(this::handleCancel);
        
        // إعداد القيم الافتراضية
        resetLabels();
    }
    
    private void resetLabels() {
        fullNameLabel.setText("-");
        emailLabel.setText("-");
        phoneLabel.setText("-");
        occupationLabel.setText("-");
        addressLabel.setText("-");
        rentPerMonthLabel.setText("-");
        totalMonthsLabel.setText("-");
        totalRentLabel.setText("-");
    }

    @FXML
    private void handleSearchApartment(ActionEvent event) {
        String apartmentIdText = searchApartmentIdField.getText().trim();
        
        if (apartmentIdText.isEmpty()) {
            showAlert("خطأ في الإدخال", "يرجى إدخال رقم الشقة", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            int apartmentId = Integer.parseInt(apartmentIdText);
            searchForApartmentAndTenant(apartmentId);
        } catch (NumberFormatException e) {
            showAlert("خطأ في الإدخال", "يرجى إدخال رقم صحيح للشقة", Alert.AlertType.ERROR);
        }
    }
    
    private void searchForApartmentAndTenant(int apartmentId) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            // البحث عن الشقة والمستأجر المخصص لها
            String sql = """
                SELECT a.id as apartment_id, a.name as apartment_name, a.status,
                       t.id as tenant_id, t.name as tenant_name, t.email, t.phone, 
                       t.occupation, t.address
                FROM apartments a
                LEFT JOIN tenants t ON a.status = 'Occupied' 
                    AND EXISTS (
                        SELECT 1 FROM payments p 
                        WHERE p.apartment_id = a.id AND p.tenant_id = t.id 
                        LIMIT 1
                    )
                WHERE a.id = ?
            """;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                java.sql.ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    this.currentApartmentId = apartmentId;
                    
                    if (rs.getInt("tenant_id") > 0) {
                        // الشقة مؤجرة - إظهار بيانات المستأجر
                        this.currentTenantId = rs.getInt("tenant_id");
                        loadTenantData(rs);
                        
                        // تفعيل عناصر الدفع
                        rentTypeComboBox.setDisable(false);
                        confirmPaymentButton.setDisable(false);
                        
                        showAlert("تم العثور على الشقة", 
                                "الشقة: " + rs.getString("apartment_name") + 
                                "\nالمستأجر: " + rs.getString("tenant_name"), 
                                Alert.AlertType.INFORMATION);
                    } else {
                        // الشقة غير مؤجرة
                        resetLabels();
                        rentTypeComboBox.setDisable(true);
                        confirmPaymentButton.setDisable(true);
                        showAlert("الشقة غير مؤجرة", 
                                "الشقة موجودة لكنها غير مؤجرة حالياً", 
                                Alert.AlertType.WARNING);
                    }
                } else {
                    showAlert("الشقة غير موجودة", 
                            "لم يتم العثور على شقة برقم " + apartmentId, 
                            Alert.AlertType.ERROR);
                    resetLabels();
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching for apartment: " + e.getMessage());
            e.printStackTrace();
            showAlert("خطأ في قاعدة البيانات", 
                    "حدث خطأ أثناء البحث عن الشقة", 
                    Alert.AlertType.ERROR);
        }
    }
    
    private void loadTenantData(java.sql.ResultSet rs) throws Exception {
        fullNameLabel.setText(rs.getString("tenant_name"));
        emailLabel.setText(rs.getString("email") != null ? rs.getString("email") : "لا يوجد");
        phoneLabel.setText(rs.getString("phone"));
        occupationLabel.setText(rs.getString("occupation") != null ? rs.getString("occupation") : "غير محدد");
        addressLabel.setText(rs.getString("address") != null ? rs.getString("address") : "غير محدد");
        
        // تحديث حساب الإيجار
        rentPerMonthLabel.setText(baseRentAmount + " JD");
        updateRentCalculation();
    }
    
    private void updateRentCalculation() {
        String selectedRentType = rentTypeComboBox.getValue();
        if (selectedRentType == null) {
            totalMonthsLabel.setText("-");
            totalRentLabel.setText("-");
            return;
        }
        
        // استخراج عدد الأشهر من النص المحدد
        int months = 1;
        if (selectedRentType.contains("2")) months = 2;
        else if (selectedRentType.contains("3")) months = 3;
        else if (selectedRentType.contains("6")) months = 6;
        else if (selectedRentType.contains("12")) months = 12;
        
        totalMonthsLabel.setText(String.valueOf(months));
        
        double totalAmount = baseRentAmount * months;
        totalRentLabel.setText(String.format("%.2f JD", totalAmount));
    }
    
    @FXML
    private void handleConfirmPayment(ActionEvent event) {
        if (currentApartmentId == -1 || currentTenantId == -1) {
            showAlert("خطأ", "يرجى البحث عن شقة صالحة أولاً", Alert.AlertType.ERROR);
            return;
        }
        
        String selectedRentType = rentTypeComboBox.getValue();
        if (selectedRentType == null) {
            showAlert("خطأ", "يرجى اختيار نوع الدفعة", Alert.AlertType.ERROR);
            return;
        }
        
        // حساب المبلغ والأشهر
        int months = 1;
        if (selectedRentType.contains("2")) months = 2;
        else if (selectedRentType.contains("3")) months = 3;
        else if (selectedRentType.contains("6")) months = 6;
        else if (selectedRentType.contains("12")) months = 12;
        
        double totalAmount = baseRentAmount * months;
        
        // تسجيل الدفعة في قاعدة البيانات
        if (recordPayment(totalAmount, months)) {
            showAlert("نجح الدفع", 
                    String.format("تم تسجيل دفعة %.2f JD لمدة %d شهر/أشهر بنجاح!", totalAmount, months),
                    Alert.AlertType.INFORMATION);
            
            // إغلاق النافذة
            Stage stage = (Stage) confirmPaymentButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert("فشل الدفع", "حدث خطأ أثناء تسجيل الدفعة", Alert.AlertType.ERROR);
        }
    }
    
    private boolean recordPayment(double amount, int months) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                INSERT INTO payments (tenant_id, apartment_id, payment_date, amount, month, 
                                    payment_method, payment_status, description) 
                VALUES (?, ?, ?, ?, ?, 'Cash', 'Completed', ?)
            """;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentTenantId);
                stmt.setInt(2, currentApartmentId);
                stmt.setString(3, LocalDate.now().toString());
                stmt.setString(4, String.format("%.2f", amount));
                
                // تحديد الشهر/الأشهر
                LocalDate currentDate = LocalDate.now();
                String monthDescription;
                if (months == 1) {
                    monthDescription = currentDate.getMonth().toString() + " " + currentDate.getYear();
                } else {
                    LocalDate endDate = currentDate.plusMonths(months - 1);
                    monthDescription = currentDate.getMonth().toString() + " " + currentDate.getYear() + 
                                     " - " + endDate.getMonth().toString() + " " + endDate.getYear();
                }
                stmt.setString(5, monthDescription);
                
                String description = String.format("Rent payment for %d month(s)", months);
                stmt.setString(6, description);
                
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
            
        } catch (Exception e) {
            System.err.println("Error recording payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
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