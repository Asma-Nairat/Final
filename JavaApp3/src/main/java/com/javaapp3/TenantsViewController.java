package com.javaapp3;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TenantsViewController implements Initializable {

    @FXML private TableView<Tenant> tenantsTable;
    @FXML private TableColumn<Tenant, String> tenantColumn;
    @FXML private TableColumn<Tenant, String> phoneColumn;
    @FXML private TableColumn<Tenant, String> occupationColumn;
    @FXML private TableColumn<Tenant, String> addressColumn;
    @FXML private TableColumn<Tenant, Void> detailsColumn;
    @FXML private Button addTenantButton;
    @FXML private TableColumn<Tenant, String> statusColumn;
    
    private final ObservableList<Tenant> tenantData = FXCollections.observableArrayList();

// هذا هو الكود الكامل لدالة initialize في ملف TenantsViewController.java

// هذا هو الكود الكامل لدالة initialize في ملف TenantsViewController.java

@Override
public void initialize(URL url, ResourceBundle rb) {
    // 1. جلب بيانات المستأجرين من قاعدة البيانات
    tenantData.clear();
    try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
        String sql = "SELECT name, email, phone, occupation, address, status FROM tenants";
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tenantData.add(new Tenant(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("occupation"),
                    rs.getString("address"),
                    rs.getString("status")
                ));
            }
        }
    } catch (Exception e) {
        System.err.println("Error loading tenants from DB: " + e.getMessage());
    }

    // 2. ربط أعمدة الجدول بـ "Property" الخاصة بكلاس Tenant للتحديث التلقائي
    tenantColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
    
    // === ربط عمود "Status" الجديد ===
    statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

    occupationColumn.setCellValueFactory(cellData -> cellData.getValue().occupationProperty());
    addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
    
    // تخصيص شكل خلية الحالة (Status) لتلوينها
    statusColumn.setCellFactory(column -> {
        return new TableCell<Tenant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("● " + item); // إضافة نقطة قبل النص
                    if (item.equalsIgnoreCase("Active")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
                    }
                }
            }
        };
    });


    // 3. إنشاء وتخصيص عمود "Details" الذي يحتوي على الزر
detailsColumn.setCellFactory(param -> new TableCell<Tenant, Void>() {
        private final Button detailsButton = new Button("Details");
        {
            detailsButton.getStyleClass().add("details-button");
            detailsButton.setOnAction(event -> {
                Tenant selectedTenant = getTableView().getItems().get(getIndex());
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("TenantDetailsView.fxml"));
                    Parent root = loader.load();
                    TenantDetailsController controller = loader.getController();
                    controller.loadTenantData(selectedTenant);
                    Stage stage = new Stage();
                    stage.setTitle("Tenant Details: " + selectedTenant.getName());
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();

                    // التحقق بعد إغلاق النافذة
                    if (controller.isApartmentDeleted()) {
                        tenantData.remove(selectedTenant);
                    } 
                    // إذا تم التحديث (سواء تعديل البيانات أو إنهاء العقد)
                    else if (controller.isTenantUpdated()) {
                        // لا حاجة لفعل شيء، الجدول يحدث نفسه تلقائيًا
                        System.out.println("Table should have updated automatically.");
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : detailsButton);
        }
    });

    // 4. تعيين كل البيانات للجدول لعرضها
    tenantsTable.setItems(tenantData);
}


    
    @FXML
    private void handleAddTenantButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AddTenantView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add New Tenant");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            System.out.println("Add Tenant window was closed.");

            // إعادة تحميل بيانات المستأجرين من قاعدة البيانات بعد الإضافة
            tenantData.clear();
            try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
                String sql = "SELECT name, email, phone, occupation, address, status FROM tenants";
                try (java.sql.Statement stmt = conn.createStatement();
                     java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        tenantData.add(new Tenant(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("occupation"),
                            rs.getString("address"),
                            rs.getString("status")
                        ));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reloading tenants from DB: " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleGoToApartments(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("ApartmentsView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // كلاس داخلي لتمثيل بيانات المستأجر
  // استبدل الكلاس القديم بهذا الكود في TenantsViewController.java
// استبدل الكلاس القديم بهذا في TenantsViewController.java

public static class Tenant {
    private final SimpleStringProperty name, email, phone, occupation, address, status;

    // Constructor for new tenants, status is always "Active"
    public Tenant(String name, String email, String phone, String occupation, String address) {
        this(name, email, phone, occupation, address, "Active");
    }
    
    // Constructor to handle status from database
  
    
      public Tenant(String name, String email, String phone, String occupation, String address, String status) {
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.occupation = new SimpleStringProperty(occupation);
        this.address = new SimpleStringProperty(address);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
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
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleStringProperty emailProperty() { return email; }
    public SimpleStringProperty phoneProperty() { return phone; }
    public SimpleStringProperty occupationProperty() { return occupation; }
    public SimpleStringProperty addressProperty() { return address; }
    public SimpleStringProperty statusProperty() { return status; }
}
}

/*
+ Record New Payment: لتسجيل دفعة جديدة.
Edit Tenant Info: لتعديل بيانات المستأجر.
Terminate Contract: لإنهاء عقد المستأجر.

*/
