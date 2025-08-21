package com.javaapp3;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TenantsViewController implements Initializable {

    @FXML private TableView<Tenant> tenantsTable;
    @FXML private TableColumn<Tenant, String> tenantColumn;
    @FXML private TableColumn<Tenant, String> phoneColumn;
    @FXML private TableColumn<Tenant, String> statusColumn;
    @FXML private TableColumn<Tenant, String> occupationColumn;
    @FXML private TableColumn<Tenant, String> addressColumn;
    @FXML private TableColumn<Tenant, Void> detailsColumn;
    @FXML private Button addTenantButton;

    
    
    
    // أضيفي كل هذه الدوال إلى TenantsViewController.java

// --- دوال التنقل ---

@FXML
private void handleGoToDashboard(ActionEvent event) throws IOException {
    navigateTo(event, "/fxml/DashboardView.fxml", "Dashboard");
}

@FXML
private void handleGoToOwners(ActionEvent event) throws IOException {
    navigateTo(event, "/fxml/OwnersView.fxml", "Owners Management");
}

@FXML
private void handleGoToApartments(ActionEvent event) throws IOException {
    navigateTo(event, "/fxml/ApartmentsView.fxml", "Apartments Management");
}

@FXML
private void handleGoToPayments(ActionEvent event) throws IOException {
    navigateTo(event, "/fxml/PaymentsView.fxml", "Payments History");
}
// أضيفي هذه الدالة إلى كل ملف Controller رئيسي

@FXML
private void handleGoToAssignApartment(ActionEvent event) throws IOException {
    // اسم الملف يجب أن يتطابق مع الملف الذي أنشأناه
    String fxmlFile = "/fxml/AssignApartmentView.fxml"; 
    String title = "Assign Apartment to Tenant";
    
    try {
        // نستخدم طريقة تحميل مختلفة قليلاً للنوافذ المنبثقة
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        
        // initModality يمنع المستخدم من التفاعل مع النافذة الخلفية
        stage.initModality(Modality.APPLICATION_MODAL); 
        
        // showAndWait يعرض النافذة وينتظر حتى يتم إغلاقها
        stage.showAndWait();

    } catch (IOException e) {
        System.err.println("Failed to load the Assign Apartment view: " + fxmlFile);
        e.printStackTrace();
    }
}
// دالة التنقل الموحدة (قد تكون موجودة لديكِ بالفعل)
private void navigateTo(ActionEvent event, String fxmlFile, String title) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle(title);
    stage.show();
}
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. ربط أعمدة الجدول بالخصائص (Properties)
        tenantColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        occupationColumn.setCellValueFactory(cellData -> cellData.getValue().occupationProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        
        // تخصيص شكل خلية الحالة (Status) لتلوينها
        statusColumn.setCellFactory(column -> new TableCell<Tenant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("● " + item);
                    setStyle(item.equalsIgnoreCase("Active") ? "-fx-text-fill: green; -fx-font-weight: bold;" : "-fx-text-fill: grey; -fx-font-weight: bold;");
                }
            }
        });

        // 2. إنشاء وتخصيص عمود "Details"
        detailsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Details");
            {
                detailsButton.getStyleClass().add("details-button");
                detailsButton.setOnAction(event -> {
                    Tenant selectedTenant = getTableView().getItems().get(getIndex());
                    openTenantDetails(selectedTenant);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailsButton);
            }
        });

        // 3. تحميل البيانات من قاعدة البيانات عند فتح الواجهة
        loadTenantsFromDatabase();
    }
    
    /**
     * دالة لجلب كل المستأجرين من قاعدة البيانات وتعبئة الجدول
     */
    private void loadTenantsFromDatabase() {
        ObservableList<Tenant> tenantsFromDb = DatabaseManager.getAllTenants();
        tenantsTable.setItems(tenantsFromDb);
        System.out.println("Loaded " + tenantsFromDb.size() + " tenants from the database.");
    }
    
    /**
     * تفتح نافذة تفاصيل المستأجر وتتعامل مع النتائج (تحديث أو حذف)
     */
    private void openTenantDetails(Tenant tenant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TenantDetailsView.fxml"));
            Parent root = loader.load();
            TenantDetailsController controller = loader.getController();
            controller.loadTenantData(tenant);

            Stage stage = new Stage();
            stage.setTitle("Tenant Details: " + tenant.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // التحقق بعد إغلاق نافذة التفاصيل
            if (controller.isApartmentDeleted()) {
                tenantsTable.getItems().remove(tenant);
            } else if (controller.isTenantUpdated()) {
                tenantsTable.refresh(); // إعادة رسم الجدول ليعكس التغييرات
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAddTenantButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddTenantView.fxml"));
            Parent root = loader.load();
            AddTenantController addController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add New Tenant");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Tenant newlyAddedTenant = addController.getEditedTenant();
            if (newlyAddedTenant != null) {
                // بدلاً من إضافة عنصر واحد، نعيد تحميل الجدول بالكامل
                // هذا يضمن أن الـ ID الذي تم إنشاؤه في قاعدة البيانات يظهر لدينا
                loadTenantsFromDatabase();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    


    // --- الكلاس الداخلي لتمثيل المستأجر ---
    public static class Tenant {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name, email, phone, occupation, address, status;

        public Tenant(int id, String name, String email, String phone, String occupation, String address, String status) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
            this.occupation = new SimpleStringProperty(occupation);
            this.address = new SimpleStringProperty(address);
            this.status = new SimpleStringProperty(status);
        }

        // Getters
        public int getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getEmail() { return email.get(); }
        public String getPhone() { return phone.get(); }
        public String getOccupation() { return occupation.get(); }
        public String getAddress() { return address.get(); }
        public String getStatus() { return status.get(); }

        // Setters
        public void setName(String name) { this.name.set(name); }
        public void setEmail(String email) { this.email.set(email); }
        public void setPhone(String phone) { this.phone.set(phone); }
        public void setOccupation(String occupation) { this.occupation.set(occupation); }
        public void setAddress(String address) { this.address.set(address); }
        public void setStatus(String status) { this.status.set(status); }

        // Property Getters
        public SimpleIntegerProperty idProperty() { return id; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty phoneProperty() { return phone; }
        public SimpleStringProperty occupationProperty() { return occupation; }
        public SimpleStringProperty addressProperty() { return address; }
        public SimpleStringProperty statusProperty() { return status; }
    }
}