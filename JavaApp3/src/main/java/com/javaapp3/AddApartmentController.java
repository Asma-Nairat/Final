package com.javaapp3; 
import java.io.File; // <-- استيراد مكتبة الملفات
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;  
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser; // <-- استيراد مكتبة اختيار الملفات
import javafx.stage.Stage;

public class AddApartmentController implements Initializable {

    // --- الربط مع عناصر الواجهة ---
    @FXML
    private TextField apartmentNameField;
    @FXML
    private ComboBox<String> apartmentTypeCombo;
    
    // === التغيير الأول هنا: تم استبدال ComboBox بـ TextField ===
    @FXML
    private TextField apartmentOwnerField;
    
    @FXML
    private TextField townField;
    @FXML
    private TextField locationField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button chooseImageButton;
    @FXML
    private Label imageNameLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;

    // متغير لتخزين ملف الصورة الذي تم اختياره
    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // تعبئة قائمة أنواع الشقق
        apartmentTypeCombo.getItems().addAll("Flat", "Studio", "Villa", "Bungalow");
        
        // === تم حذف تعبئة قائمة الملاك من هنا ===
    }

private boolean saved = false;

public boolean isSaved() {
    return saved;
}    

    @FXML
    private void handleCancelButton(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // === الدالة الجديدة لبرمجة زر اختيار الصورة ===
    @FXML
    private void handleChooseImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Apartment Image");
        
        // تحديد أنواع الملفات المسموح بها (صور فقط)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // فتح نافذة اختيار الملف
        Stage stage = (Stage) chooseImageButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        // إذا اختار المستخدم ملفًا، قم بتحديث الليبل
        if (selectedImageFile != null) {
            imageNameLabel.setText(selectedImageFile.getName());
        } else {
            imageNameLabel.setText("No image selected");
        }
    }

// استبدل دالة handleAddButton في AddApartmentController.java بهذا الكود:
// استبدل handleAddButton في AddApartmentController.java بهذا:

@FXML
private void handleAddButton(ActionEvent event) {
    // قراءة البيانات من الحقول
    String name = apartmentNameField.getText().trim();
    String type = apartmentTypeCombo.getValue();
    String ownerName = apartmentOwnerField.getText().trim();
    String town = townField.getText().trim();
    String location = locationField.getText().trim();
    String description = descriptionArea.getText().trim();
    
    // التحقق من صحة المدخلات
    if (name.isEmpty() || type == null || ownerName.isEmpty() || town.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Input Validation Failed");
        alert.setContentText("Please fill in all required fields (*)");
        alert.showAndWait();
        return;
    }
}
    
    // هذه الدالة تضاف إلى ملف AddApartmentController.java

/**
 * دالة لتعبئة الفورم ببيانات الشقة التي سيتم تعديلها.
 * يتم استدعاؤها من الكنترولر الرئيسي.
 * @param name اسم الشقة
 * @param type نوع الشقة
 * @param owner مالك الشقة
 * @param town المدينة
 * @param location الموقع
 * @param description الوصف
 */
public void loadApartmentForEditing(String name, String type, String owner, String town, String location, String description) {
    // تعبئة الحقول بالبيانات الحالية
    apartmentNameField.setText(name);
    apartmentTypeCombo.setValue(type); // تعيين القيمة المحددة في القائمة
    apartmentOwnerField.setText(owner);
    townField.setText(town);
    locationField.setText(location);
    descriptionArea.setText(description);

    // (اختياري) تغيير نص الزر والعنوان
    addButton.setText("Save Changes");
   // Stage stage = (Stage) addButton.getScene().getWindow();
   // stage.setTitle("Edit Apartment");
}
    

    
}
