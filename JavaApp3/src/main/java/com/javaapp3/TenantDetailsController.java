package com.javaapp3; // تأكد من أن اسم الحزمة صحيح
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.javaapp3.TenantsViewController.Tenant;
import com.javaapp3.Payment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;

public class TenantDetailsController implements Initializable {

    // --- FXML Variables ---
    @FXML private Label tenantNameHeaderLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label occupationLabel;
    @FXML private Label statusLabel;
    @FXML private Button editDetailsButton;
    @FXML private Button recordPaymentButton;
    @FXML private Button terminateContractButton;
    
    // === تعريف الجدول وأعمدته ===
    @FXML private TableView<Payment> paymentsTable;
    @FXML private TableColumn<Payment, String> dateColumn;
    @FXML private TableColumn<Payment, String> amountColumn;
    @FXML private TableColumn<Payment, String> monthColumn;
     @FXML private Button deleteButton; 
     
    // --- Class Variables ---
    private Tenant currentTenant;
    private boolean tenantUpdated = false;
    private final ObservableList<Payment> paymentData = FXCollections.observableArrayList();
    private boolean apartmentDeleted = false;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ربط أعمدة جدول الدفعات ببيانات كلاس Payment
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        monthColumn.setCellValueFactory(cellData -> cellData.getValue().monthProperty());
        
        // تعيين القائمة للجدول
        paymentsTable.setItems(paymentData);
    }    

public void loadTenantData(Tenant tenant) {
    this.currentTenant = tenant;
    if (currentTenant == null) { return; }
    refreshView();
    // لاحقاً، ستقوم هنا بتحميل سجل الدفعات الخاص بهذا المستأجر من قاعدة البيانات
    // paymentData.addAll(database.getPaymentsForTenant(tenant.getId()));
}


    private void refreshView() {
        // ... (دالة تحديث الليبلات تبقى كما هي)
        tenantNameHeaderLabel.setText(currentTenant.getName());
        phoneLabel.setText(currentTenant.getPhone());
        emailLabel.setText(currentTenant.getEmail());
        occupationLabel.setText(currentTenant.getOccupation());
        statusLabel.setText("● Active");
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }


   @FXML
private void handleRecordPaymentButton(ActionEvent event) {
    try {
        // تحميل واجهة تسجيل الدفعة
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecordPaymentView.fxml"));
        Parent root = loader.load();
        RecordPaymentController paymentController = loader.getController();

        // إنشاء وعرض النافذة
        Stage stage = new Stage();
        stage.setTitle("Record Payment for " + currentTenant.getName());
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        // الحصول على الدفعة الجديدة بعد إغلاق النافذة
        Payment newPayment = paymentController.getNewPayment();
        if (newPayment != null) {
            // إضافة الدفعة إلى الجدول
            paymentData.add(newPayment);
            
            // إظهار رسالة نجاح
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Payment Recorded");
            alert.setHeaderText("Payment Added Successfully");
            alert.setContentText("Payment has been recorded for " + currentTenant.getName());
            alert.showAndWait();
        }

    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot open payment form");
        alert.setContentText("Failed to load the payment recording form.");
        alert.showAndWait();
    }
}
    
    
    
  
    
    public boolean isTenantUpdated() { return tenantUpdated; }
    // أضف هذه الدالة إلى نهاية كلاس TenantDetailsController.java

/**
 * دالة للسماح للواجهة الرئيسية بمعرفة ما إذا كان يجب حذف المستأجر.
 * @return true إذا ضغط المستخدم "OK" في حوار الحذف.
 */
public boolean isApartmentDeleted() {
    return apartmentDeleted;
}

// تأكد من أن دالة handleDeleteButton موجودة وتبدو هكذا

// هذا الكود يستبدل الدالة القديمة في TenantDetailsController.java

@FXML
private void handleDeleteButton(ActionEvent event) {
    System.out.println("--- 'DELETE' button clicked from the details view! ---");

    // إنشاء رسالة التأكيد
    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmationAlert.setTitle("Confirm Deletion");
    confirmationAlert.setHeaderText("Are you sure you want to delete this apartment permanently?");
    confirmationAlert.setContentText("This action cannot be undone.");

    // === التغيير هنا: نحصل على النتيجة أولاً ===
    // .showAndWait() ستنتظر حتى يضغط المستخدم على زر
    Optional<ButtonType> result = confirmationAlert.showAndWait();

    // نتحقق من أن المستخدم ضغط على "OK"
    if (result.isPresent() && result.get() == ButtonType.OK) {
        System.out.println("User confirmed deletion.");
        
        // نغير قيمة المتغير
        this.apartmentDeleted = true;
        
        // نغلق النافذة الحالية
        Stage stage = (Stage) deleteButton.getScene().getWindow();
        stage.close();
    } else {
        // إذا ضغط المستخدم "Cancel" أو أغلق الحوار، لا نفعل شيئًا
        System.out.println("Deletion cancelled by user.");
    }
}

// أضف هذه الدالة إلى TenantDetailsController.java

@FXML
private void handleTerminateContractButton(ActionEvent event) {
    if (currentTenant == null) return;

    // إنشاء رسالة تأكيد
    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmationAlert.setTitle("Confirm Contract Termination");
    confirmationAlert.setHeaderText("Are you sure you want to terminate the contract for " + currentTenant.getName() + "?");
    confirmationAlert.setContentText("This will mark the tenant as 'Inactive' and free up their assigned apartment.");

    Optional<ButtonType> result = confirmationAlert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
        System.out.println("Contract terminated for: " + currentTenant.getName());
        
        // لاحقاً، سنقوم بتحديث قاعدة البيانات هنا
        // UPDATE tenants SET status = 'Inactive' WHERE id = ...
        // UPDATE apartments SET status = 'Available' WHERE tenant_id = ...

        // تحديث الواجهة الحالية (واجهة التفاصيل)
        currentTenant.setStatus("Inactive"); // سنضيف هذه الدالة لكلاس Tenant
        statusLabel.setText("● Inactive");
        statusLabel.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
        
        // نجعل الأزرار غير فعالة لأن العقد انتهى
        terminateContractButton.setDisable(true);
        recordPaymentButton.setDisable(true);
        
        // نؤكد أن تحديثًا قد حصل ليتم تحديث الجدول الرئيسي
        this.tenantUpdated = true;
    }
}

@FXML
private void handleEditTenantButton(ActionEvent event) {
    try {
        // تحميل واجهة تعديل المستأجر
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddTenantView.fxml"));
        Parent root = loader.load();
        AddTenantController editController = loader.getController();
        
        // تحميل البيانات الحالية للتعديل
        editController.loadTenantForEditing(this.currentTenant);

        // إنشاء وعرض النافذة
        Stage stage = new Stage();
        stage.setTitle("Edit Tenant: " + currentTenant.getName());
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        // الحصول على البيانات المحدثة
        Tenant updatedTenant = editController.getEditedTenant();
        if (updatedTenant != null) {
            // تحديث البيانات الحالية
            this.currentTenant.setName(updatedTenant.getName());
            this.currentTenant.setEmail(updatedTenant.getEmail());
            this.currentTenant.setPhone(updatedTenant.getPhone());
            this.currentTenant.setOccupation(updatedTenant.getOccupation());
            this.currentTenant.setAddress(updatedTenant.getAddress());
            
            // إعادة رسم الواجهة
            refreshView();
            this.tenantUpdated = true;
            
            // إظهار رسالة نجاح
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tenant Updated");
            alert.setHeaderText("Information Updated Successfully");
            alert.setContentText("Tenant information has been updated.");
            alert.showAndWait();
        }

    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot open edit form");
        alert.setContentText("Failed to load the tenant editing form.");
        alert.showAndWait();
    }
}
}



    // === كلاس داخلي لتمثيل بيانات الدفعة ===
