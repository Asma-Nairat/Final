package com.javaapp3;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import java.io.IOException;

public class ApartmentDetailsController implements Initializable {

    @FXML private Button deleteButton;
    @FXML private Button editDetailsButton;
    @FXML private Label apartmentNameHeaderLabel;
    @FXML private Label apartmentNameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label ownerContactLabel;
    @FXML private Label ownerNameLabel;
    @FXML private Label statusLabel;
    
    private boolean editRequested = false;
    private boolean apartmentDeleted = false;
    private int currentApartmentId = -1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // يمكن ترك هذا فارغاً حالياً
    }
    
    public boolean isEditRequested() {
        return editRequested;
    }
    
    public boolean isApartmentDeleted() {
        return apartmentDeleted;
    }
    
    /**
     * دالة لتحميل بيانات الشقة من قاعدة البيانات وعرضها
     */
    public void loadApartmentData(int apartmentId) {
        this.currentApartmentId = apartmentId;
        
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            // جلب بيانات الشقة مع بيانات المالك
            String sql = """
                SELECT a.id, a.name, a.type, a.town, a.location, a.description, a.status,
                       o.name as owner_name, o.phone as owner_phone, o.email as owner_email
                FROM apartments a
                JOIN owners o ON a.owner_id = o.id
                WHERE a.id = ?
            """;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                java.sql.ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    // تحديث عناصر الواجهة
                    String apartmentName = rs.getString("name");
                    apartmentNameHeaderLabel.setText(apartmentName);
                    apartmentNameLabel.setText(apartmentName);
                    
                    String description = rs.getString("description");
                    descriptionLabel.setText(description != null && !description.isEmpty() ? description : "لا يوجد وصف");
                    
                    ownerNameLabel.setText(rs.getString("owner_name"));
                    
                    String ownerPhone = rs.getString("owner_phone");
                    String ownerEmail = rs.getString("owner_email");
                    String contactInfo = ownerPhone;
                    if (ownerEmail != null && !ownerEmail.isEmpty()) {
                        contactInfo += " | " + ownerEmail;
                    }
                    ownerContactLabel.setText(contactInfo);
                    
                    String status = rs.getString("status");
                    statusLabel.setText("● " + (status != null ? status : "Available"));
                    if ("Available".equals(status)) {
                        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                } else {
                    System.err.println("Apartment not found with ID: " + apartmentId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading apartment data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditDetailsButton(ActionEvent event) {
        System.out.println("Edit button clicked for apartment ID: " + currentApartmentId);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddApartmentView.fxml"));
            Parent root = loader.load();
            AddApartmentController editController = loader.getController();
            
            // تحميل البيانات الحالية للتعديل
            loadApartmentForEditing(editController);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Apartment Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // التحقق من حفظ التعديلات
            if (editController.isSaved()) {
                this.editRequested = true;
                // إعادة تحميل البيانات المحدثة
                loadApartmentData(currentApartmentId);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Apartment details updated successfully!");
                alert.showAndWait();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open edit window");
            alert.setContentText("Failed to load the edit apartment form.");
            alert.showAndWait();
        }
    }
    
    private void loadApartmentForEditing(AddApartmentController editController) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT a.name, a.type, a.town, a.location, a.description,
                       o.name as owner_name
                FROM apartments a
                JOIN owners o ON a.owner_id = o.id
                WHERE a.id = ?
            """;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentApartmentId);
                java.sql.ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    editController.loadApartmentForEditing(
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("owner_name"),
                        rs.getString("town"),
                        rs.getString("location"),
                        rs.getString("description")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading apartment for editing: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        System.out.println("Delete button clicked for apartment ID: " + currentApartmentId);

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this apartment?");
        confirmationAlert.setContentText("This action cannot be undone. All related data will be removed.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (deleteApartmentFromDatabase()) {
                    this.apartmentDeleted = true;
                    
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Apartment deleted successfully!");
                    successAlert.showAndWait();
                    
                    // إغلاق نافذة التفاصيل
                    Stage stage = (Stage) deleteButton.getScene().getWindow();
                    stage.close();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to delete apartment");
                    errorAlert.setContentText("The apartment may be in use or there was a database error.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    private boolean deleteApartmentFromDatabase() {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            // بداية المعاملة
            conn.setAutoCommit(false);
            
            try {
                // حذف الطلبات المرتبطة أولاً (إذا وجدت)
                String deleteMaintenanceSql = "DELETE FROM maintenance_requests WHERE apartment_id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(deleteMaintenanceSql)) {
                    stmt.setInt(1, currentApartmentId);
                    stmt.executeUpdate();
                }
                
                // حذف المدفوعات المرتبطة
                String deletePaymentsSql = "DELETE FROM payments WHERE apartment_id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(deletePaymentsSql)) {
                    stmt.setInt(1, currentApartmentId);
                    stmt.executeUpdate();
                }
                
                // حذف الشقة
                String deleteApartmentSql = "DELETE FROM apartments WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(deleteApartmentSql)) {
                    stmt.setInt(1, currentApartmentId);
                    int affectedRows = stmt.executeUpdate();
                    
                    if (affectedRows > 0) {
                        conn.commit();
                        System.out.println("Apartment " + currentApartmentId + " deleted successfully.");
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
                
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting apartment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}