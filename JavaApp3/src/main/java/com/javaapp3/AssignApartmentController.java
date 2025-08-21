package com.javaapp3; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class for AssignApartmentView.fxml
 */
public class AssignApartmentController implements Initializable {

    // --- عناصر الاختيار ---
    @FXML
    private ComboBox<String> selectApartmentComboBox;
    @FXML
    private ComboBox<String> selectTenantComboBox;

    // --- عناصر عرض تفاصيل الشقة ---
    @FXML
    private Label apartmentAddressLabel;
    @FXML
    private Label apartmentStatusLabel;
    @FXML
    private Label apartmentRentLabel;

    // --- عناصر عرض تفاصيل المستأجر ---
    @FXML
    private Label tenantNameLabel;
    @FXML
    private Label tenantEmailLabel;
    @FXML
    private Label tenantPhoneLabel;

    // --- عناصر إدخال المبالغ ---
    @FXML
    private TextField depositAmountField;
    @FXML
    private TextField placementFeeField;

    /**
     * Initializes the controller class.
     */
 // استبدل دالة initialize في AssignApartmentController.java بهذا الكود:

@Override
public void initialize(URL url, ResourceBundle rb) {
    System.out.println("Assign Apartment View and Controller loaded.");
    
    // تعبئة قائمة الشقق المتاحة من قاعدة البيانات
    // أعمدة جدول apartments: id, owner_id, name, type, town, location, description, image_path, status
    try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
        String sqlApt = "SELECT id, name, town FROM apartments WHERE status = 'Available'";
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sqlApt)) {
            javafx.collections.ObservableList<String> apartments = javafx.collections.FXCollections.observableArrayList();
            while (rs.next()) {
                // نضع معرف الشقة مع اسمها لسهولة التعرف عليها
                apartments.add("ID:" + rs.getInt("id") + " - " + rs.getString("name") + " (" + rs.getString("town") + ")");
            }
            selectApartmentComboBox.setItems(apartments);
        }
        
        // تعبئة قائمة المستأجرين النشطين بدون شقة مخصصة
        // أعمدة جدول tenants: id, name, email, phone, occupation, address, status  
        String sqlTenant = "SELECT id, name FROM tenants WHERE status = 'Active'";
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sqlTenant)) {
            javafx.collections.ObservableList<String> tenants = javafx.collections.FXCollections.observableArrayList();
            while (rs.next()) {
                // نضع معرف المستأجر مع اسمه
                tenants.add("ID:" + rs.getInt("id") + " - " + rs.getString("name"));
            }
            selectTenantComboBox.setItems(tenants);
        }
        
    } catch (Exception e) {
        System.err.println("Error loading apartments/tenants for assignment: " + e.getMessage());
        e.printStackTrace();
    }
    
    // إضافة مستمعين للتحديث التلقائي للتفاصيل
    selectApartmentComboBox.setOnAction(e -> updateApartmentDetails());
    selectTenantComboBox.setOnAction(e -> updateTenantDetails());
}

private void updateApartmentDetails() {
    String selectedApartment = selectApartmentComboBox.getValue();
    if (selectedApartment == null || selectedApartment.isEmpty()) {
        apartmentAddressLabel.setText("-");
        apartmentStatusLabel.setText("-");
        apartmentRentLabel.setText("-");
        return;
    }
    
    // استخراج ID الشقة من النص المحدد
    try {
        int apartmentId = Integer.parseInt(selectedApartment.split(" - ")[0].replace("ID:", ""));
        
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT name, town, location, status FROM apartments WHERE id = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                java.sql.ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    apartmentAddressLabel.setText(rs.getString("name") + ", " + rs.getString("town"));
                    apartmentStatusLabel.setText(rs.getString("status"));
                    apartmentRentLabel.setText("تحديد لاحقاً"); // يمكن إضافة حقل للإيجار لاحقاً
                }
            }
        }
    } catch (Exception e) {
        System.err.println("Error loading apartment details: " + e.getMessage());
    }
}

private void updateTenantDetails() {
    String selectedTenant = selectTenantComboBox.getValue();
    if (selectedTenant == null || selectedTenant.isEmpty()) {
        tenantNameLabel.setText("-");
        tenantEmailLabel.setText("-");
        tenantPhoneLabel.setText("-");
        return;
    }
    
    // استخراج ID المستأجر من النص المحدد
    try {
        int tenantId = Integer.parseInt(selectedTenant.split(" - ")[0].replace("ID:", ""));
        
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT name, email, phone FROM tenants WHERE id = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, tenantId);
                java.sql.ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tenantNameLabel.setText(rs.getString("name"));
                    tenantEmailLabel.setText(rs.getString("email") != null ? rs.getString("email") : "لا يوجد");
                    tenantPhoneLabel.setText(rs.getString("phone"));
                }
            }
        }
    } catch (Exception e) {
        System.err.println("Error loading tenant details: " + e.getMessage());
    }
}  
// أضف هذه الدوال إلى AssignApartmentController.java:

@FXML
private void handleAssignApartment(ActionEvent event) {
    String selectedApartment = selectApartmentComboBox.getValue();
    String selectedTenant = selectTenantComboBox.getValue();
    String depositAmount = depositAmountField.getText().trim();
    String placementFee = placementFeeField.getText().trim();
    
    // التحقق من صحة المدخلات
    if (selectedApartment == null || selectedTenant == null) {
        showAlert("خطأ في الإدخال", "يرجى اختيار شقة ومستأجر", Alert.AlertType.ERROR);
        return;
    }
    
    if (depositAmount.isEmpty()) {
        showAlert("خطأ في الإدخال", "يرجى إدخال مبلغ التأمين", Alert.AlertType.ERROR);
        return;
    }
    
    try {
        // استخراج المعرفات
        int apartmentId = Integer.parseInt(selectedApartment.split(" - ")[0].replace("ID:", ""));
        int tenantId = Integer.parseInt(selectedTenant.split(" - ")[0].replace("ID:", ""));
        double deposit = Double.parseDouble(depositAmount);
        double placement = placementFee.isEmpty() ? 0.0 : Double.parseDouble(placementFee);
        
        // تنفيذ عملية التخصيص في قاعدة البيانات
        if (assignApartmentToTenant(apartmentId, tenantId, deposit, placement)) {
            showAlert("نجح العملية", "تم تخصيص الشقة للمستأجر بنجاح!", Alert.AlertType.INFORMATION);
            
            // إغلاق النافذة
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            showAlert("خطأ", "فشل في تخصيص الشقة. يرجى المحاولة مرة أخرى.", Alert.AlertType.ERROR);
        }
        
    } catch (NumberFormatException e) {
        showAlert("خطأ في الأرقام", "يرجى إدخال أرقام صحيحة للمبالغ", Alert.AlertType.ERROR);
    }
}

private boolean assignApartmentToTenant(int apartmentId, int tenantId, double deposit, double placement) {
    try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
        // بداية المعاملة (Transaction)
        conn.setAutoCommit(false);
        
        // 1. تحديث حالة الشقة إلى "مؤجرة"
        String updateApartmentSql = "UPDATE apartments SET status = 'Occupied' WHERE id = ?";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(updateApartmentSql)) {
            stmt.setInt(1, apartmentId);
            stmt.executeUpdate();
        }
        
        // 2. يمكن إضافة جدول منفصل لتسجيل التخصيصات أو تحديث جدول المستأجرين
        // لكن حسب الجداول المتاحة، سنكتفي بتغيير حالة الشقة
        
        // 3. تسجيل دفعة التأمين (إذا كانت أكبر من صفر)
        if (deposit > 0) {
            String insertPaymentSql = "INSERT INTO payments (tenant_id, apartment_id, payment_date, amount, month, payment_method, payment_status, description) VALUES (?, ?, CURRENT_DATE, ?, ?, 'Cash', 'Completed', ?)";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(insertPaymentSql)) {
                stmt.setInt(1, tenantId);
                stmt.setInt(2, apartmentId);
                stmt.setString(3, String.valueOf(deposit));
                stmt.setString(4, java.time.LocalDate.now().getMonth().toString() + " " + java.time.LocalDate.now().getYear());
                stmt.setString(5, "Security Deposit");
                stmt.executeUpdate();
            }
        }
        
        // 4. تسجيل رسوم التخصيص (إذا كانت أكبر من صفر)
        if (placement > 0) {
            String insertPlacementSql = "INSERT INTO payments (tenant_id, apartment_id, payment_date, amount, month, payment_method, payment_status, description) VALUES (?, ?, CURRENT_DATE, ?, ?, 'Cash', 'Completed', ?)";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(insertPlacementSql)) {
                stmt.setInt(1, tenantId);
                stmt.setInt(2, apartmentId);
                stmt.setString(3, String.valueOf(placement));
                stmt.setString(4, java.time.LocalDate.now().getMonth().toString() + " " + java.time.LocalDate.now().getYear());
                stmt.setString(5, "Placement Fee");
                stmt.executeUpdate();
            }
        }
        
        // إتمام المعاملة
        conn.commit();
        System.out.println("Apartment " + apartmentId + " assigned to tenant " + tenantId + " successfully.");
        return true;
        
    } catch (Exception e) {
        System.err.println("Error assigning apartment: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

@FXML
private void handleCancelAssignment(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
