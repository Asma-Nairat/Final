package javaapp3;

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
import javaapp3.TenantsViewController.Tenant; // استيراد كلاس Tenant

public class AddTenantController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField occupationField;
    @FXML private TextArea addressArea;
    @FXML private Button cancelButton;
    @FXML private Button addButton;
    
    // متغير لتخزين المستأجر المعدل وإرجاعه للواجهة السابقة
    private Tenant editedTenant = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) { }    

    /**
     * دالة جديدة لتعبئة الفورم ببيانات المستأجر الحالي عند التعديل
     */
    public void loadTenantForEditing(Tenant tenant) {
        fullNameField.setText(tenant.getName());
        emailField.setText(tenant.getEmail());
        phoneField.setText(tenant.getPhone());
        occupationField.setText(tenant.getOccupation());
        addressArea.setText(tenant.getAddress());

        // تغيير نص الزر ليدل على التعديل
        addButton.setText("Save Changes");
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        String fullName = fullNameField.getText();
        String phone = phoneField.getText();

        if (fullName.isEmpty() || phone.isEmpty()) {
            // ... (كود التحقق من الحقول يبقى كما هو)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Input Validation Failed");
            alert.setContentText("Please fill in Full Name and Phone fields (*)");
            alert.showAndWait();
            return;
        }

        // إنشاء كائن Tenant جديد بالبيانات المحدثة
        this.editedTenant = new Tenant(
                fullName,
                emailField.getText(),
                phone,
                occupationField.getText(),
                addressArea.getText()
        );
        
        // إغلاق النافذة
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * دالة للسماح للواجهة السابقة بالحصول على البيانات المعدلة
     * @return المستأجر بالبيانات الجديدة، أو null إذا تم الإلغاء
     */
    public Tenant getEditedTenant() {
        return editedTenant;
    }
}