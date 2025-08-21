package com.javaapp3; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    
    // أضيفي كل هذه الدوال إلى كلاس PaymentsViewController.java

    // --- دوال التنقل من القائمة الجانبية ---
    
    @FXML
    private void handleGoToDashboard(ActionEvent event) throws IOException {
        navigateTo(event, "/fxml/DashboardView.fxml", "Dashboard");
    }
    
    @FXML
    private void handleGoToOwners(ActionEvent event) throws IOException {
        navigateTo(event, "/fxml/OwnersView.fxml", "Owners Management");
    }

    @FXML
    private void handleGoToTenants(ActionEvent event) throws IOException {
        navigateTo(event, "/fxml/TenantsView.fxml", "Tenants Management");
    }

    @FXML
    private void handleGoToApartments(ActionEvent event) throws IOException {
        navigateTo(event, "/fxml/ApartmentsView.fxml", "Apartments Management");
    }
    
    @FXML
    private void handleGoToAssignApartment(ActionEvent event) throws IOException {
        openModalWindow("/fxml/AssignApartmentView.fxml", "Assign Apartment to Tenant");
    }
    
    /**
     * تعمل عند الضغط على زر "Add Payment".
     * تفتح نافذة منبثقة لتسجيل دفعة جديدة.
     */



    // --- دوال مساعدة للتنقل وفتح النوافذ ---

    /**
     * دالة موحدة للانتقال بين الواجهات الرئيسية (استبدال المشهد).
     */
    private void navigateTo(ActionEvent event, String fxmlFile, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
    
    /**
     * دالة موحدة لفتح نافذة منبثقة (Modal Window).
     */
    private void openModalWindow(String fxmlFile, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
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
// استبدل دالة initialize في PaymentsViewController.java بهذا الكود:
// أضف هذه الدوال إلى PaymentsViewController.java:

@Override
public void initialize(URL url, ResourceBundle rb) {
    // الكود الموجود سابقاً...
    loadPaymentsFromDatabase();
    setupTableColumns();
    System.out.println("Payments View loaded with " + paymentData.size() + " payments from database.");
}

private void loadPaymentsFromDatabase() {
    paymentData.clear();
    try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
        String sql = """
            SELECT p.id, p.tenant_id, p.apartment_id, p.payment_date, p.amount, 
                   p.month, p.payment_method, p.payment_status, p.description,
                   t.name as tenant_name, a.name as apartment_name
            FROM payments p
            JOIN tenants t ON p.tenant_id = t.id
            JOIN apartments a ON p.apartment_id = a.id
            ORDER BY p.payment_date DESC, p.id DESC
        """;
        
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                paymentData.add(new PaymentDetails(
                    rs.getString("id"),
                    rs.getString("tenant_name") + " (ID: " + rs.getString("tenant_id") + ")",
                    rs.getString("apartment_name") + " (ID: " + rs.getString("apartment_id") + ")",
                    rs.getString("payment_date"),
                    rs.getString("amount") + " JD",
                    rs.getString("payment_method") != null ? rs.getString("payment_method") : "Cash",
                    rs.getString("payment_status") != null ? rs.getString("payment_status") : "Completed",
                    rs.getString("description") != null ? rs.getString("description") : ""
                ));
            }
        }
    } catch (Exception e) {
        System.err.println("Error loading payments from DB: " + e.getMessage());
        e.printStackTrace();
    }
}

private void setupTableColumns() {
    // ربط أعمدة الجدول بالبيانات
    idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
    tenantIdColumn.setCellValueFactory(cellData -> cellData.getValue().tenantIdProperty());
    apartmentIdColumn.setCellValueFactory(cellData -> cellData.getValue().apartmentIdProperty());
    paymentDateColumn.setCellValueFactory(cellData -> cellData.getValue().paymentDateProperty());
    amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    paymentMethodColumn.setCellValueFactory(cellData -> cellData.getValue().paymentMethodProperty());
    paymentStatusColumn.setCellValueFactory(cellData -> cellData.getValue().paymentStatusProperty());
    descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

    // تخصيص عمود الحالة بالألوان
    paymentStatusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<PaymentDetails, String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                setStyle("");
            } else {
                setText(item);
                if ("Completed".equals(item)) {
                    setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else if ("Pending".equals(item)) {
                    setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        }
    });
    
    // إضافة قائمة سياق للجدول (Right-click menu)
    javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
    javafx.scene.control.MenuItem viewInvoiceItem = new javafx.scene.control.MenuItem("View Invoice");
    javafx.scene.control.MenuItem payRentItem = new javafx.scene.control.MenuItem("Pay Rent");
    
    viewInvoiceItem.setOnAction(e -> {
        PaymentDetails selected = paymentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openInvoiceView(Integer.parseInt(selected.getId()));
        }
    });
    
    payRentItem.setOnAction(e -> {
        try {
            openModalWindow("/fxml/PayRentView.fxml", "Pay Rent");
            // إعادة تحميل البيانات بعد إضافة دفعة جديدة
            loadPaymentsFromDatabase();
            paymentsTable.setItems(paymentData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    });
    
    contextMenu.getItems().addAll(viewInvoiceItem, payRentItem);
    paymentsTable.setContextMenu(contextMenu);
    
    // Double-click لفتح الفاتورة
    paymentsTable.setRowFactory(tv -> {
        javafx.scene.control.TableRow<PaymentDetails> row = new javafx.scene.control.TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !row.isEmpty()) {
                PaymentDetails selected = row.getItem();
                openInvoiceView(Integer.parseInt(selected.getId()));
            }
        });
        return row;
    });

    paymentsTable.setItems(paymentData);
}

private void openInvoiceView(int paymentId) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InvoiceView.fxml"));
        Parent root = loader.load();
        InvoiceController invoiceController = loader.getController();
        
        // تحميل بيانات الفاتورة
        invoiceController.loadInvoiceData(paymentId);
        
        Stage stage = new Stage();
        stage.setTitle("Invoice #" + paymentId);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        
    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot open invoice");
        alert.setContentText("Failed to load the invoice view.");
        alert.showAndWait();
    }
}

@FXML
private void handleAddPayment(ActionEvent event) throws IOException {
    openModalWindow("/fxml/PayRentView.fxml", "Add New Payment");
    // إعادة تحميل البيانات
    loadPaymentsFromDatabase();
    paymentsTable.setItems(paymentData);
}

// باقي الدوال تبقى كما هي...  
    
}
