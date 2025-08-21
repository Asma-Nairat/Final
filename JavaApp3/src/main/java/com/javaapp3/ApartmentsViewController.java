package com.javaapp3;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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
        // تحميل الشقق من قاعدة البيانات عند فتح الواجهة
        loadApartmentsFromDatabase();
    }

    /**
     * دالة لجلب الشقق من قاعدة البيانات وعرضها كبطاقات.
     */
    private void loadApartmentsFromDatabase() {
        apartmentsGrid.getChildren().clear(); // مسح البطاقات القديمة
        ObservableList<Apartment> apartments = DatabaseManager.getAllApartments();
        for (Apartment apartment : apartments) {
            VBox apartmentCard = createApartmentCard(apartment);
            apartmentsGrid.getChildren().add(apartmentCard);
        }
    }

    /**
     * دالة لإنشاء بطاقة عرض لشقة واحدة.
     * @param apartment كائن الشقة الذي يحتوي على البيانات.
     * @return VBox يمثل البطاقة.
     */
    private VBox createApartmentCard(Apartment apartment) {
        VBox card = new VBox();
        card.setPrefSize(220, 280);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("apartment-card");
        card.setPadding(new Insets(10));
        card.setSpacing(10); 

        HBox topSection = new HBox();
        Rectangle imagePlaceholder = new Rectangle(140, 140);
        imagePlaceholder.setStyle("-fx-fill: #e0e0e0; -fx-arc-width: 10; -fx-arc-height: 10;");

        VBox buttonsContainer = new VBox(10);
        buttonsContainer.setPadding(new Insets(0, 0, 0, 10));
        Button editButton = new Button("✏️ Edit");
        Button deleteButton = new Button("🗑️ Delete");
        
        editButton.getStyleClass().add("edit-button");
        deleteButton.getStyleClass().add("delete-button");
        
        editButton.setPrefWidth(80);
        deleteButton.setPrefWidth(80);

        buttonsContainer.getChildren().addAll(editButton, deleteButton);
        topSection.getChildren().addAll(imagePlaceholder, buttonsContainer);

        VBox infoSection = new VBox(5);
        // استخدام البيانات من كائن Apartment
        Label nameLabel = new Label(apartment.getAddress());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label locationLabel = new Label(apartment.getCity());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");

        infoSection.getChildren().addAll(nameLabel, locationLabel);
        card.getChildren().addAll(topSection, infoSection);

        // --- برمجة الأحداث ---
        editButton.setOnAction(e -> {
            System.out.println("Edit button clicked for apartment ID: " + apartment.getId());
            // openEditView(apartment); // سنبرمجها لاحقاً
            e.consume();
        });

        deleteButton.setOnAction(e -> {
            System.out.println("Delete button clicked for apartment ID: " + apartment.getId());
            // handleDeleteApartment(apartment, card); // سنبرمجها لاحقاً
            e.consume();
        });
        
        card.setOnMouseClicked(event -> {
            System.out.println("Card clicked for apartment ID: " + apartment.getId());
            // openDetailsView(apartment); // سنبرمجها لاحقاً
        });

        return card;
    }
    
    // --- دوال التنقل ---
    // يجب أن تكون هذه الدوال موجودة لديكِ بالفعل
    @FXML private void handleGoToDashboard(ActionEvent event) throws IOException { navigateTo(event, "/fxml/DashboardView.fxml", "Dashboard"); }
    @FXML private void handleGoToOwners(ActionEvent event) throws IOException { navigateTo(event, "/fxml/OwnersView.fxml", "Owners Management"); }
    @FXML private void handleGoToTenants(ActionEvent event) throws IOException { navigateTo(event, "/fxml/TenantsView.fxml", "Tenants Management"); }
    @FXML private void handleGoToPayments(ActionEvent event) throws IOException { navigateTo(event, "/fxml/PaymentsView.fxml", "Payments History"); }
    @FXML private void handleGoToAssignApartment(ActionEvent event) throws IOException { /* ... */ }

    private void navigateTo(ActionEvent event, String fxmlFile, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
    
    // ... دالة handleAddApartment تبقى كما هي حالياً ...
    @FXML
    private void handleAddApartment(ActionEvent event) {
        // سنقوم بربطها بقاعدة البيانات في الخطوة التالية
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
    // =======================================================
}