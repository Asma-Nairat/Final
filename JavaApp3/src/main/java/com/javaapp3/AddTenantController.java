package com.javaapp3;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
// استيراد كلاس Tenant من مكانه الجديد (إذا نقلته إلى حزمة models)
// أو من com.javaapp3.TenantsViewController.Tenant إذا بقي ككلاس داخلي
import com.javaapp3.TenantsViewController.Tenant; 

public class AddTenantController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField occupationField;
    @FXML private TextArea addressArea;
    @FXML private Button cancelButton;
    @FXML private Button addButton;

    // === تم توحيد اسم المتغير هنا ===
    private Tenant resultTenant = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) { }    

    @FXML
    private void handleCancelButton(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        String fullName = fullNameField.getText();
        String phone = phoneField.getText();

        if (fullName.trim().isEmpty() || phone.trim().isEmpty()) {
            showAlert("Input Error", "Full Name and Phone are required fields.", Alert.AlertType.ERROR);
            return;
        }

        Tenant newTenant = new Tenant(
                0, 
                fullName,
                emailField.getText(),
                phone,
                occupationField.getText(),
                addressArea.getText(),
                "Active"
        );
        
        boolean success = DatabaseManager.addTenant(newTenant);

        if (success) {
            showAlert("Success", "Tenant has been added successfully!", Alert.AlertType.INFORMATION);
            
            // نخزن المستأجر الجديد في المتغير
            this.resultTenant = newTenant;
            
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Database Error", "Failed to add the tenant to the database.", Alert.AlertType.ERROR);
        }
    }
    
    public void loadTenantForEditing(Tenant tenant) {
        // ... (الكود يبقى كما هو)
    }

    /**
     * دالة للسماح للواجهة السابقة بالحصول على المستأجر الذي تم إنشاؤه.
     * @return كائن المستأجر، أو null إذا تم الإلغاء.
     */
    public Tenant getEditedTenant() {
        // === تم تصحيح اسم المتغير هنا ===
        return resultTenant;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        // ... (الكود يبقى كما هو)
    }
}