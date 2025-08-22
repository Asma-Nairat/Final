package com.javaapp3;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadApartmentsFromDatabase();
    }

    private void loadApartmentsFromDatabase() {
        apartmentsGrid.getChildren().clear();
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT a.id, a.name, a.type, a.town, a.location, a.status,
                       o.name as owner_name
                FROM apartments a
                LEFT JOIN owners o ON a.owner_id = o.id
                ORDER BY a.id DESC
            """;
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Apartment apartment = new Apartment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("town"),
                        0.0,
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

    private VBox createApartmentCard(Apartment apartment, String ownerName) {
        VBox card = new VBox();
        card.setPrefSize(280, 350);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);");
        card.setPadding(new Insets(15));
        card.setSpacing(12);

        Rectangle imagePlaceholder = new Rectangle(250, 140);
        imagePlaceholder.setStyle("-fx-fill: linear-gradient(to bottom, #e8f4fd, #d1e7f5); -fx-arc-width: 10; -fx-arc-height: 10; -fx-stroke: #bdd7ee; -fx-stroke-width: 1;");
        Label nameLabel = new Label(apartment.getAddress());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label locationLabel = new Label("📍 " + apartment.getCity());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        Label ownerLabel = new Label("👤 Owner: " + (ownerName != null ? ownerName : "N/A"));
        ownerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        
        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusIcon = new Label();
        Label statusLabel = new Label(apartment.getAvailabilityStatus());
        if ("Available".equals(apartment.getAvailabilityStatus())) {
            statusIcon.setText("🟢");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13px;");
        } else {
            statusIcon.setText("🔴");
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13px;");
        }
        statusBox.getChildren().addAll(statusIcon, statusLabel);

        HBox buttonsContainer = new HBox(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setPadding(new Insets(10, 0, 0, 0));
        Button viewButton = new Button("View");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 12;");
        editButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 12;");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 12;");
        
        addHoverEffect(viewButton, "#2980b9");
        addHoverEffect(editButton, "#8e44ad");
        addHoverEffect(deleteButton, "#c0392b");
        
        buttonsContainer.getChildren().addAll(viewButton, editButton, deleteButton);
        card.getChildren().addAll(imagePlaceholder, nameLabel, locationLabel, ownerLabel, statusBox, buttonsContainer);

        viewButton.setOnAction(e -> openDetailsView(apartment.getId()));
        // *** تعديل: تمرير apartmentId إلى دالة التعديل ***
        editButton.setOnAction(e -> openEditView(apartment.getId()));
        deleteButton.setOnAction(e -> handleDeleteApartment(apartment.getId()));
        
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                openDetailsView(apartment.getId());
            }
        });
        
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 3); -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2); -fx-cursor: hand;"));

        return card;
    }

    private void addHoverEffect(Button button, String hoverColor) {
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> button.setStyle(originalStyle.replaceFirst("-fx-background-color: (.*?);", "-fx-background-color: " + hoverColor + ";")));
        button.setOnMouseExited(e -> button.setStyle(originalStyle));
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
            if (controller.isApartmentDeleted() || controller.isEditRequested()) {
                loadApartmentsFromDatabase();
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
            
            // *** تعديل: نمرر apartmentId مباشرة إلى الكنترولر ليقوم هو بجلب البيانات ***
            editController.loadApartmentForEditing(apartmentId);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Apartment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            if (editController.isSaved()) {
                loadApartmentsFromDatabase();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteApartment(int apartmentId) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Delete Apartment");
        confirmationAlert.setContentText("Are you sure you want to delete this apartment?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteApartmentFromDatabase(apartmentId)) {
                loadApartmentsFromDatabase();
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setContentText("Apartment deleted successfully!");
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setContentText("Failed to delete apartment.");
                errorAlert.showAndWait();
            }
        }
    }
    
    private boolean deleteApartmentFromDatabase(int apartmentId) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
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
                loadApartmentsFromDatabase();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleGoToDashboard(ActionEvent event) throws IOException { navigateTo(event, "/fxml/DashboardView.fxml", "Dashboard"); }
    @FXML private void handleGoToOwners(ActionEvent event) throws IOException { navigateTo(event, "/fxml/OwnersView.fxml", "Owners Management"); }
    @FXML private void handleGoToTenants(ActionEvent event) throws IOException { navigateTo(event, "/fxml/TenantsView.fxml", "Tenants Management"); }
    @FXML private void handleGoToPayments(ActionEvent event) throws IOException { navigateTo(event, "/fxml/PaymentsView.fxml", "Payments History"); }
    
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
        public int getId() { return id.get(); }
        public String getAddress() { return address.get(); }
        public String getCity() { return city.get(); }
        public double getRentPrice() { return rentPrice.get(); }
        public String getAvailabilityStatus() { return availabilityStatus.get(); }
        public SimpleIntegerProperty idProperty() { return id; }
        public SimpleStringProperty addressProperty() { return address; }
        public SimpleStringProperty cityProperty() { return city; }
        public SimpleDoubleProperty rentPriceProperty() { return rentPrice; }
        public SimpleStringProperty availabilityStatusProperty() { return availabilityStatus; }
    }
}