// هذا هو كود ApartmentDetailsController.java
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


public class ApartmentDetailsController implements Initializable {

    @FXML
    
private Button deleteButton;
    @FXML
private Button editDetailsButton;

    @FXML
    private Label apartmentNameHeaderLabel;

    @FXML
    private Label apartmentNameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label ownerContactLabel;

    @FXML
    private Label ownerNameLabel;

    @FXML
    private Label statusLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // يمكن ترك هذا فارغاً حالياً
    }    
    // هذا الكود يضاف إلى ApartmentDetailsController.java
private boolean editRequested = false;

public boolean isEditRequested() {
    return editRequested;
}
    
    /**
     * هذه هي الدالة الأهم، يتم استدعاؤها من الكنترولر السابق.
     * وظيفتها هي تعبئة الواجهة بالبيانات الصحيحة.
     * @param name اسم الشقة التي تم الضغط عليها
     * @param location موقعها
     */
    public void loadApartmentData(String name, String location) {
        // حالياً سنعرض البيانات البسيطة التي تم تمريرها
        apartmentNameHeaderLabel.setText(name);
        apartmentNameLabel.setText(name);
        
        // يمكنك هنا جلب بقية البيانات من قاعدة البيانات بناءً على اسم الشقة
        // بيانات وهمية للتوضيح
        descriptionLabel.setText("Some description for " + name + " located in " + location);
        ownerNameLabel.setText("Mr. Fares"); // بيانات من قاعدة البيانات
        ownerContactLabel.setText("079-1234567"); // بيانات من قاعدة البيانات
        statusLabel.setText("• Available"); // بيانات من قاعدة البيانات
    }
    


@FXML
private void handleEditDetailsButton(ActionEvent event) {
    System.out.println("--- 'Edit Details' button clicked. Closing details and requesting edit. ---");
    this.editRequested = true; // نؤكد أن المستخدم طلب التعديل
    
    // إغلاق نافذة التفاصيل
    Stage stage = (Stage) editDetailsButton.getScene().getWindow();
    stage.close();
}




private boolean apartmentDeleted = false;

/**
 * دالة للتحقق مما إذا كان يجب تحديث القائمة الرئيسية
 * @return true إذا تم الحذف، وإلا false
 */
public boolean isApartmentDeleted() {
    return apartmentDeleted;
}

@FXML
private void handleDeleteButton(ActionEvent event) {
    System.out.println("--- 'DELETE' button clicked from the details view! ---");

    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmationAlert.setTitle("Confirm Deletion");
    confirmationAlert.setHeaderText("Are you sure you want to delete this apartment permanently?");
    confirmationAlert.setContentText("This action cannot be undone.");

    confirmationAlert.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            System.out.println("User confirmed deletion.");
            // لاحقاً، هنا سيتم الحذف من قاعدة البيانات

            // الخطوة الأهم: نغير قيمة المتغير إلى true
            this.apartmentDeleted = true;
            
            // إغلاق نافذة التفاصيل
            Stage stage = (Stage) deleteButton.getScene().getWindow();
            stage.close();
        }
    });
}
}
