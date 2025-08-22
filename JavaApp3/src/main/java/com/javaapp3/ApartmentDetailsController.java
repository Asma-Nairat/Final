package com.javaapp3;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ApartmentDetailsController implements Initializable {

    // --- الربط مع عناصر الواجهة ---
    @FXML private Button deleteButton;
    @FXML private Button editDetailsButton;
    @FXML private Label apartmentNameHeaderLabel;
    @FXML private Label apartmentNameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label ownerContactLabel;
    @FXML private Label ownerNameLabel;
    @FXML private Label statusLabel;
    
    // --- متغيرات لتتبع حالة النافذة ---
    private boolean editRequested = false;      // تصبح true إذا تم تعديل الشقة بنجاح
    private boolean apartmentDeleted = false;   // تصبح true إذا تم حذف الشقة بنجاح
    private int currentApartmentId = -1;      // لتخزين ID الشقة المعروضة حالياً
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // لا نحتاج لشيء هنا حالياً، لأن تحميل البيانات يتم عبر دالة loadApartmentData
    }
    
    // --- دوال Getters لإعلام النافذة الرئيسية بالتغييرات ---
    public boolean isEditRequested() {
        return editRequested;
    }
    
    public boolean isApartmentDeleted() {
        return apartmentDeleted;
    }
    
    /**
     * دالة أساسية لتحميل بيانات الشقة من قاعدة البيانات وعرضها في الواجهة.
     * يتم استدعاؤها من ApartmentsViewController عند الضغط على زر "View".
     * @param apartmentId الـ ID الخاص بالشقة المطلوب عرضها
     */
    public void loadApartmentData(int apartmentId) {
        this.currentApartmentId = apartmentId;
        
        // التحقق من أن الـ ID صالح
        if (apartmentId <= 0) {
            System.err.println("Invalid Apartment ID received: " + apartmentId);
            showAlert("Error", "Invalid Data", "Could not load apartment details due to an invalid ID.");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            // استعلام SQL لجلب كل تفاصيل الشقة مع اسم وبيانات التواصل الخاصة بالمالك
            String sql = """
                SELECT a.id, a.name, a.type, a.town, a.location, a.description, a.status,
                       o.name as owner_name, o.phone as owner_phone, o.email as owner_email
                FROM apartments a
                LEFT JOIN owners o ON a.owner_id = o.id
                WHERE a.id = ?
            """;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    // إذا وجدنا الشقة، نقوم بتعبئة الحقول في الواجهة
                    String apartmentName = rs.getString("name");
                    apartmentNameHeaderLabel.setText(apartmentName);
                    apartmentNameLabel.setText(apartmentName);
                    
                    String description = rs.getString("description");
                    descriptionLabel.setText(description != null && !description.isEmpty() ? description : "No description provided.");
                    
                    ownerNameLabel.setText(rs.getString("owner_name") != null ? rs.getString("owner_name") : "N/A");
                    
                    String ownerPhone = rs.getString("owner_phone");
                    String ownerEmail = rs.getString("owner_email");
                    String contactInfo = ownerPhone != null ? ownerPhone : "";
                    if (ownerEmail != null && !ownerEmail.isEmpty()) {
                        contactInfo += (contactInfo.isEmpty() ? "" : " | ") + ownerEmail;
                    }
                    ownerContactLabel.setText(contactInfo.isEmpty() ? "N/A" : contactInfo);
                    
                    String status = rs.getString("status");
                    statusLabel.setText("● " + (status != null ? status : "N/A"));
                    if ("Available".equals(status)) {
                        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                } else {
                    System.err.println("Apartment not found with ID: " + apartmentId);
                    showAlert("Not Found", "Apartment Not Found", "The selected apartment could not be found in the database.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading apartment data: " + e.getMessage());
            e.printStackTrace();
            showAlert("Database Error", "Loading Failed", "An error occurred while fetching apartment details.");
        }
    }

    /**
     * دالة تعمل عند الضغط على زر "Edit".
     * تفتح نافذة التعديل (AddApartmentView.fxml) وتمرر لها ID الشقة الحالية.
     */
    @FXML
    private void handleEditDetailsButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddApartmentView.fxml"));
            Parent root = loader.load();
            AddApartmentController editController = loader.getController();
            
            // نمرر ID الشقة الحالية إلى كنترولر التعديل
            editController.loadApartmentForEditing(this.currentApartmentId);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Apartment Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // الانتظار حتى يغلق المستخدم نافذة التعديل
            
            // بعد إغلاق نافذة التعديل، نتحقق إذا تم الحفظ
            if (editController.isSaved()) {
                this.editRequested = true; // نعلم النافذة الرئيسية بوجود تعديل
                loadApartmentData(this.currentApartmentId); // نعيد تحميل البيانات المحدثة في هذه النافذة
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot Open Window", "Failed to load the edit apartment form.");
        }
    }

    /**
     * دالة تعمل عند الضغط على زر "Delete".
     * تعرض رسالة تأكيد، وإذا وافق المستخدم، تقوم بحذف الشقة من قاعدة البيانات.
     */
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this apartment?");
        confirmationAlert.setContentText("This action cannot be undone. All related data will also be removed.");

        // عرض نافذة التأكيد وانتظار رد المستخدم
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // إذا ضغط المستخدم "OK"، ننفذ الحذف
                if (deleteApartmentFromDatabase()) {
                    this.apartmentDeleted = true; // نعلم النافذة الرئيسية بوجود حذف
                    
                    // إغلاق نافذة التفاصيل بعد الحذف الناجح
                    Stage stage = (Stage) deleteButton.getScene().getWindow();
                    stage.close();
                } else {
                    showAlert("Error", "Deletion Failed", "The apartment could not be deleted due to a database error.");
                }
            }
        });
    }
    
    /**
     * دالة مساعدة لتنفيذ عملية الحذف في قاعدة البيانات.
     * @return true إذا نجح الحذف, false إذا فشل.
     */
    private boolean deleteApartmentFromDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // بداية المعاملة لضمان تنفيذ كل شيء أو لا شيء
            try {
                // حذف البيانات المرتبطة أولاً لمنع أخطاء Foreign Key
                String[] deleteSqls = {
                    "DELETE FROM maintenance_requests WHERE apartment_id = ?",
                    "DELETE FROM payments WHERE apartment_id = ?",
                    "DELETE FROM apartments WHERE id = ?"
                };
                
                for (String sql : deleteSqls) {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, this.currentApartmentId);
                        stmt.executeUpdate();
                    }
                }
                
                conn.commit(); // تأكيد تنفيذ كل عمليات الحذف
                System.out.println("Apartment " + currentApartmentId + " and related data deleted successfully.");
                return true;
                
            } catch (Exception e) {
                conn.rollback(); // إذا حدث خطأ، تراجع عن كل التغييرات
                throw e; // إلقاء الاستثناء لمعالجته في الأعلى
            } finally {
                conn.setAutoCommit(true); // إعادة الوضع الافتراضي
            }
        } catch (Exception e) {
            System.err.println("Error during apartment deletion transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * دالة مساعدة لعرض رسائل التنبيه.
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}