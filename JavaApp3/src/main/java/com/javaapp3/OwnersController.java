package com.javaapp3;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class OwnersController {

    @FXML private TableView<Owner> ownersTable;
    @FXML private TableColumn<Owner, String> nameColumn;
    @FXML private TableColumn<Owner, String> emailColumn;
    @FXML private TableColumn<Owner, String> phoneColumn;
    @FXML private TableColumn<Owner, String> addressColumn;
    @FXML private TableColumn<Owner, Void> actionsColumn;

    private ObservableList<Owner> ownersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadOwnersData();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(5, editButton, deleteButton);
            {
                editButton.setStyle("-fx-background-color: #5b2c87; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Owner owner = getTableView().getItems().get(getIndex());
                    handleEditOwner(owner);
                });
                deleteButton.setOnAction(event -> {
                    Owner owner = getTableView().getItems().get(getIndex());
                    handleDeleteOwner(owner);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadOwnersData() {
        ownersList.clear();
        // *** تعديل: إضافة id إلى استعلام SELECT ***
        String sql = "SELECT id, name, email, phone, address FROM owners ORDER BY name";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ownersList.add(new Owner(
                        // *** تعديل: جلب الـ id من قاعدة البيانات ***
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
            ownersTable.setItems(ownersList);
            System.out.println("Loaded " + ownersList.size() + " owners from database.");
        } catch (Exception e) {
            System.err.println("Error loading owners from DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddOwner(ActionEvent event) {
        openOwnerForm(null, "Add New Owner");
    }

    private void handleEditOwner(Owner owner) {
        openOwnerForm(owner, "Edit Owner: " + owner.getName());
    }

    private void openOwnerForm(Owner owner, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddOwner.fxml"));
            Parent root = loader.load();
            AddOwnerController controller = loader.getController();
            if (owner != null) {
                controller.setOwnerToEdit(owner);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadOwnersData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open owner form", "Failed to load the FXML file.");
        }
    }

    private void handleDeleteOwner(Owner owner) {
        // *** تعديل: الحذف أصبح الآن باستخدام الـ id الآمن ***
        String sql = "DELETE FROM owners WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // *** تعديل: نستخدم getId() بدلاً من getName() ***
            stmt.setInt(1, owner.getId());
            stmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database Error", "Failed to delete owner.");
        }
        loadOwnersData();
    }

       // --- دوال التنقل في القائمة الجانبية (Sidebar Navigation) ---

    @FXML
    private void handleGoToDashboard(ActionEvent event) throws IOException {
        navigateTo(event, "/fxml/DashboardView.fxml", "Dashboard");
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
    private void handleGoToPayments(ActionEvent event) throws IOException {
        navigateTo(event, "/fxml/PaymentsView.fxml", "Payments History");
    }
    
    @FXML
    private void handleGoToAssignApartment(ActionEvent event) throws IOException {
        // يمكن استخدام دالة النافذة المنبثقة إذا كانت موجودة، أو التنقل العادي
        openModalWindow("/fxml/AssignApartmentView.fxml", "Assign Apartment to Tenant");
    }

    /**
     * دالة موحدة للتنقل بين الصفحات الرئيسية
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
     * دالة موحدة لفتح النوافذ المنبثقة (Modal)
     */
    private void openModalWindow(String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- الكلاس الداخلي Owner (تم تعديله ليشمل id) ---
    public static class Owner {
        private final SimpleIntegerProperty id; // *** إضافة: حقل للـ id ***
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty address;

        public Owner(int id, String name, String email, String phone, String address) {
            this.id = new SimpleIntegerProperty(id); // *** إضافة: تهيئة الـ id ***
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
            this.address = new SimpleStringProperty(address);
        }

        // --- Getters & Properties ---
        public int getId() { return id.get(); } // *** إضافة: دالة للحصول على الـ id ***
        public SimpleIntegerProperty idProperty() { return id; }
        public String getName() { return name.get(); }
        public SimpleStringProperty nameProperty() { return name; }
        public String getEmail() { return email.get(); }
        public SimpleStringProperty emailProperty() { return email; }
        public String getPhone() { return phone.get(); }
        public SimpleStringProperty phoneProperty() { return phone; }
        public String getAddress() { return address.get(); }
        public SimpleStringProperty addressProperty() { return address; }
    }
}