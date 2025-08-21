package com.javaapp3; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class for PaymentsView.fxml
 * هذا هو الهيكل الأولي للمتحكم، وهو غير مبرمج بعد.
 */
public class PaymentsViewController implements Initializable {

    @FXML
    private void handleAddPaymentButton() {
        // TODO: Implement payment addition logic
        System.out.println("Add Payment button clicked.");
    }

    // الربط مع زر "Pay Rent"
    @FXML
    private Button payRentButton;

    // الربط مع الجدول الرئيسي
    @FXML
    private TableView<PaymentDetails> paymentsTable;

    // الربط مع جميع أعمدة الجدول
    @FXML
    private TableColumn<PaymentDetails, String> idColumn;

    @FXML
    private TableColumn<PaymentDetails, String> tenantIdColumn;

    @FXML
    private TableColumn<PaymentDetails, String> apartmentIdColumn;

    @FXML
    private TableColumn<PaymentDetails, String> paymentDateColumn;

    @FXML
    private TableColumn<PaymentDetails, String> amountColumn;

    @FXML
    private TableColumn<PaymentDetails, String> paymentMethodColumn;

    @FXML
    private TableColumn<PaymentDetails, String> paymentStatusColumn;

    @FXML
    private TableColumn<PaymentDetails, String> descriptionColumn;

    private javafx.collections.ObservableList<PaymentDetails> paymentData = javafx.collections.FXCollections.observableArrayList();

    public static class PaymentDetails {
        private final javafx.beans.property.SimpleStringProperty id, tenantId, apartmentId, paymentDate, amount, paymentMethod, paymentStatus, description;

        public PaymentDetails(String id, String tenantId, String apartmentId, String paymentDate, String amount, String paymentMethod, String paymentStatus, String description) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.tenantId = new javafx.beans.property.SimpleStringProperty(tenantId);
            this.apartmentId = new javafx.beans.property.SimpleStringProperty(apartmentId);
            this.paymentDate = new javafx.beans.property.SimpleStringProperty(paymentDate);
            this.amount = new javafx.beans.property.SimpleStringProperty(amount);
            this.paymentMethod = new javafx.beans.property.SimpleStringProperty(paymentMethod);
            this.paymentStatus = new javafx.beans.property.SimpleStringProperty(paymentStatus);
            this.description = new javafx.beans.property.SimpleStringProperty(description);
        }

        public String getId() { return id.get(); }
        public String getTenantId() { return tenantId.get(); }
        public String getApartmentId() { return apartmentId.get(); }
        public String getPaymentDate() { return paymentDate.get(); }
        public String getAmount() { return amount.get(); }
        public String getPaymentMethod() { return paymentMethod.get(); }
        public String getPaymentStatus() { return paymentStatus.get(); }
        public String getDescription() { return description.get(); }

        public javafx.beans.property.SimpleStringProperty idProperty() { return id; }
        public javafx.beans.property.SimpleStringProperty tenantIdProperty() { return tenantId; }
        public javafx.beans.property.SimpleStringProperty apartmentIdProperty() { return apartmentId; }
        public javafx.beans.property.SimpleStringProperty paymentDateProperty() { return paymentDate; }
        public javafx.beans.property.SimpleStringProperty amountProperty() { return amount; }
        public javafx.beans.property.SimpleStringProperty paymentMethodProperty() { return paymentMethod; }
        public javafx.beans.property.SimpleStringProperty paymentStatusProperty() { return paymentStatus; }
        public javafx.beans.property.SimpleStringProperty descriptionProperty() { return description; }
    }

    /**
     * Initializes the controller class.
     * هذه الدالة تعمل تلقائياً عند تحميل الواجهة.
     * سنقوم ببرمجتها لاحقاً لجلب البيانات من قاعدة البيانات وتعبئة الجدول.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // جلب بيانات الدفعات من قاعدة البيانات وتعبئة الجدول
        paymentData.clear();
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
String sql = "SELECT id, tenant_id, apartment_id, payment_date, amount, payment_method, payment_status, description FROM payments";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    paymentData.add(new PaymentDetails(
                        rs.getString("id"),
                        rs.getString("tenant_id"),
                        rs.getString("apartment_id"),
                        rs.getString("payment_date"),
                        rs.getString("amount"),
                        rs.getString("payment_method"),
                        rs.getString("payment_status"),
                        rs.getString("description")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading payments from DB: " + e.getMessage());
        }

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        tenantIdColumn.setCellValueFactory(cellData -> cellData.getValue().tenantIdProperty());
        apartmentIdColumn.setCellValueFactory(cellData -> cellData.getValue().apartmentIdProperty());
        paymentDateColumn.setCellValueFactory(cellData -> cellData.getValue().paymentDateProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        paymentMethodColumn.setCellValueFactory(cellData -> cellData.getValue().paymentMethodProperty());
        paymentStatusColumn.setCellValueFactory(cellData -> cellData.getValue().paymentStatusProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        paymentsTable.setItems(paymentData);
        System.out.println("Payments View and Controller have been loaded successfully.");
    }    
    
}
