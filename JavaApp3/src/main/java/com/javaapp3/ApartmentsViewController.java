package com.javaapp3;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ApartmentsViewController implements Initializable {

    @FXML private TilePane apartmentsGrid;
    @FXML private Button addApartmentButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadApartmentsFromDatabase();
    }

    /**
     * دالة لجلب الشقق من قاعدة البيانات وعرضها كبطاقات
     */
    private void loadApartmentsFromDatabase() {
        apartmentsGrid.getChildren().clear();
        
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT a.id, a.name, a.type, a.town, a.location, a.status,
                       o.name as owner_name
                FROM apartments a
                JOIN owners o ON a.owner_id = o.id
                ORDER BY a.id DESC
            """;
            
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Apartment apartment = new Apartment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("town"),
                        0.0, // rent_price - يمكن إضافة هذا الحقل لاحقاً
                        rs.getString("status")
                    );
                    
                    VBox apartmentCard = createApartmentCard(apartment, rs.getString("owner_name"));
                    apartmentsGrid.getChildren().add(apartmentCard);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading apartments from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * دالة لإنشاء بطاقة عرض لشقة واحدة
     */
    private VBox createApartmentCard(Apartment apartment, String ownerName) {
        VBox card = new VBox();
        card.setPrefSize(250, 300);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPadding(new Insets(15));
        card.setSpacing(10);

        // الصورة (مؤقتة)
        Rectangle imagePlaceholder = new Rectangle(220, 120);
        imagePlaceholder.setStyle("-fx-fill: #e0e0e0; -fx-arc-width: 10; -fx-arc-height: 10;");

        // معلومات الشقة
        Label nameLabel = new Label(apartment.getAddress());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        
        Label locationLabel = new Label(apartment.getCity());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");
        
        Label ownerLabel = new Label("Owner: " + ownerName);
        ownerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
        
        // حالة الشقة
        Label statusLabel = new Label("● " + apartment.getAvailabilityStatus());
        if ("Available".equals(apartment.getAvailabilityStatus())) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
        }

        // أزرار التحكم
        HBox buttonsContainer = new HBox(10);
        buttonsContainer.setAlignment(Pos.CENTER);
        
        Button viewButton = new Button("View Details");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        
        viewButton.setStyle("-fx-background-color: #000080; -fx-text-fill: white; -fx-background-radius: 5;");
        editButton.setStyle("-fx-background-color: #5b2c87; -fx-text-fill: white; -fx-background-radius: 5;");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
        
        buttonsContainer.getChildren().addAll(viewButton, editButton, deleteButton);

        card.getChildren().addAll(imagePlaceholder, nameLabel, locationLabel, ownerLabel, statusLabel, buttonsContainer);

        // برمجة الأحداث
        viewButton.setOnAction(e -> openDetailsView(apartment.getId()));
        editButton.setOnAction(e -> openEditView(apartment.getId()));
        deleteButton.setOnAction(e -> handleDeleteApartment(apartment.getId()));
        
        // الضغط على البطاقة يفتح التفاصيل
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double click
                openDetailsView(apartment.getId());
            }
        });

        return card;
    }
    
    private void openDetailsView(int apartmentId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ApartmentDetailsView.fxml"));
            Parent root = loader.load();
            ApartmentDetailsController controller = loader.getController();
            
            controller.loadApartmentData(apartmentId);
            
            Stage stage = new Stage();
            stage.setTitle("Apartment Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // التحقق من التحديثات
            if (controller.isApartmentDeleted() || controller.isEditRequested()) {
                loadApartmentsFromDatabase(); // إعادة تحميل القائمة
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openEditView(int apartmentId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddApartmentView.fxml"));
            Parent root = loader.load();
            AddApartmentController editController = loader.getController();
            
            // تحميل البيانات للتعديل
            loadApartmentForEditing(apartmentId, editController);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Apartment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            if (editController.isSaved()) {
                loadApartmentsFromDatabase(); // إعادة تحميل القائمة
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadApartmentForEditing(int apartmentId, AddApartmentController editController) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT a.name, a.type, a.town, a.location, a.description,
                       o.name as owner_name
                FROM apartments a
                JOIN owners o ON a.owner_id = o.id
                WHERE a.id = ?
            """;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                java.sql.ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    editController.loadApartmentForEditing(
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("owner_name"),
                        rs.getString("town"),
                        rs.getString("location"),
                        rs.getString("description")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading apartment for editing: " + e.getMessage());
        }
    }
    
    private void handleDeleteApartment(int apartmentId) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Delete Apartment");
        confirmationAlert.setContentText("Are you sure you want to delete this apartment? This action cannot be undone.");
        
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteApartmentFromDatabase(apartmentId)) {
                loadApartmentsFromDatabase(); // إعادة تحميل القائمة
                
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Apartment deleted successfully!");
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Failed to delete apartment");
                errorAlert.setContentText("The apartment may be in use or there was a database error.");
                errorAlert.showAndWait();
            }
        }
    }
    
    private boolean deleteApartmentFromDatabase(int apartmentId) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // حذف البيانات المرتبطة أولاً
                String[] deleteSqls = {
                    "DELETE FROM maintenance_requests WHERE apartment_id = ?",
                    "DELETE FROM payments WHERE apartment_id = ?",
                    "DELETE FROM apartments WHERE id = ?"
                };
                
                for (String sql : deleteSqls) {
                    try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, apartmentId);
                        stmt.executeUpdate();
                    }
                }
                
                conn.commit();
                return true;
                
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting apartment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleAddApartment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddApartmentView.fxml"));
            Parent root = loader.load();
            AddApartmentController addController = loader.getController();
            
            Stage stage = new Stage();
            stage.setTitle("Add New Apartment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            if (addController.isSaved()) {
                loadApartmentsFromDatabase(); // إعادة تحميل القائمة
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- دوال التنقل ---
    @FXML private void handleGoToDashboard(ActionEvent event) throws IOException { 
        navigateTo(event, "/fxml/DashboardView.fxml", "Dashboard"); 
    }
    @FXML private void handleGoToOwners(ActionEvent event) throws IOException { 
        navigateTo(event, "/fxml/OwnersView.fxml", "Owners Management"); 
    }
    @FXML private void handleGoToTenants(ActionEvent event) throws IOException { 
        navigateTo(event, "/fxml/TenantsView.fxml", "Tenants Management"); 
    }
    @FXML private void handleGoToPayments(ActionEvent event) throws IOException { 
        navigateTo(event, "/fxml/PaymentsView.fxml", "Payments History"); 
    }
    @FXML private void handleGoToAssignApartment(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AssignApartmentView.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Assign Apartment to Tenant");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void navigateTo(ActionEvent event, String fxmlFile, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    // =======================================================
    // ===          الكلاس الداخلي لتمثيل الشقة            ===
    // =======================================================
    public static class Apartment {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty address;
        private final SimpleStringProperty city;
        private final SimpleDoubleProperty rentPrice;
        private final SimpleStringProperty availabilityStatus;

        public Apartment(int id, String address, String city, double rentPrice, String status) {
            this.id = new SimpleIntegerProperty(id);
            this.address = new SimpleStringProperty(address);
            this.city = new SimpleStringProperty(city);
            this.rentPrice = new SimpleDoubleProperty(rentPrice);
            this.availabilityStatus = new SimpleStringProperty(status);
        }

        // Getters
        public int getId() { return id.get(); }
        public String getAddress() { return address.get(); }
        public String getCity() { return city.get(); }
        public double getRentPrice() { return rentPrice.get(); }
        public String getAvailabilityStatus() { return availabilityStatus.get(); }

        // Property Getters
        public SimpleIntegerProperty idProperty() { return id; }
        public SimpleStringProperty addressProperty() { return address; }
        public SimpleStringProperty cityProperty() { return city; }
        public SimpleDoubleProperty rentPriceProperty() { return rentPrice; }
        public SimpleStringProperty availabilityStatusProperty() { return availabilityStatus; }
    }
}