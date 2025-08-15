package javaapp3;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
// استيراد كلاس Payment من الكنترولر الآخر
import javaapp3.TenantDetailsController.Payment;

public class RecordPaymentController implements Initializable {

    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea notesArea;
    
    private Payment newPayment = null; // متغير لتخزين الدفعة الجديدة

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datePicker.setValue(LocalDate.now());
    }    

    @FXML
    private void handleCancelButton(ActionEvent event) {
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSavePaymentButton(ActionEvent event) {
        String amountStr = amountField.getText();
        LocalDate date = datePicker.getValue();
        double amount;

        if (amountStr.isEmpty() || date == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setContentText("Please enter an amount and select a date.");
            alert.showAndWait();
            return;
        }
        
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setContentText("Please enter a valid number for the amount.");
            alert.showAndWait();
            return;
        }

        // إنشاء كائن دفعة جديد بالبيانات المدخلة
        // ملاحظة: الشهر حاليًا وهمي، لاحقًا يمكننا استنتاجه أو إضافة حقل له
        this.newPayment = new Payment(date.toString(), String.format("%.2f JD", amount), date.getMonth().toString() + " " + date.getYear());
        
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    /**
     * دالة للسماح للواجهة السابقة بالحصول على الدفعة الجديدة
     * @return كائن الدفعة، أو null إذا تم الإلغاء
     */
    public Payment getNewPayment() {
        return newPayment;
    }
}