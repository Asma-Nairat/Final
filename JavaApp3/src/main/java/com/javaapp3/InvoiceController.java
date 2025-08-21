package com.javaapp3; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class for InvoiceView.fxml
 */
public class InvoiceController implements Initializable {

    // --- عناصر رأس الفاتورة ---
    @FXML
    private Label invoiceIdLabel;
    @FXML
    private Label invoiceDateLabel;

    // --- عناصر معلومات المستأجر ---
    @FXML
    private Label tenantNameLabel;
    @FXML
    private Label tenantPhoneLabel;
    @FXML
    private Label tenantEmailLabel;
    @FXML
    private Label tenantAddressLabel;

    // --- عناصر معلومات المالك ---
    @FXML
    private Label landlordNameLabel;
    @FXML
    private Label landlordPhoneLabel;
    @FXML
    private Label landlordEmailLabel;
    @FXML
    private Label landlordAddressLabel;

    // --- عناصر تفاصيل الفاتورة ---
    @FXML
    private Label apartmentAddressLabel;
    @FXML
    private Label rentMonthLabel;
    @FXML
    private Label paymentMethodLabel;
    @FXML
    private Label paymentStatusLabel;
    @FXML
    private Label totalAmountLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO: سنكتب الكود هنا لاحقاً
        System.out.println("Invoice View and Controller loaded.");
    }    
    
    /**
     * دالة (سننشئها لاحقاً) لاستقبال كل بيانات الفاتورة وتعبئة الحقول
     * ستحتاج هذه الدالة لاستقبال كائن يحتوي على كل المعلومات
     * (معلومات الدفعة، المستأجر، الشقة، المالك)
     */
    public void loadInvoiceData(/*... parameters ...*/) {
        // مثال:
        // invoiceIdLabel.setText("INVOICE #" + payment.getId());
        // tenantNameLabel.setText(tenant.getName());
        // ... etc.
    }
}