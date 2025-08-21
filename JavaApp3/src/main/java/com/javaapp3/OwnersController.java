package com.javaapp3; // تأكد من أن اسم الحزمة صحيح
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

public class OwnersController {
    @FXML
    private TableView<Owner> ownersTable;
    @FXML
    private TableColumn<Owner, String> nameColumn;
    @FXML
    private TableColumn<Owner, String> emailColumn;
    @FXML
    private TableColumn<Owner, String> phoneColumn;
    @FXML
    private TableColumn<Owner, String> addressColumn;
    @FXML
    private TableColumn<Owner, Void> actionsColumn;

    private ObservableList<Owner> ownersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initialize table columns
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));

        // Initialize actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<Owner, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button detailsButton = new Button("Details");

            {
                editButton.setStyle("-fx-background-color: #5b2c87; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12;");
                deleteButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12;");
                detailsButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12;");

                editButton.setOnAction(event -> {
                    Owner owner = getTableView().getItems().get(getIndex());
                    handleEditOwner(owner);
                });

                deleteButton.setOnAction(event -> {
                    Owner owner = getTableView().getItems().get(getIndex());
                    handleDeleteOwner(owner);
                });

                detailsButton.setOnAction(event -> {
                    Owner owner = getTableView().getItems().get(getIndex());
                    handleShowDetails(owner);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton, detailsButton);
                    setGraphic(buttons);
                }
            }
        });

        // جلب بيانات الملاك من قاعدة البيانات
        ownersList.clear();
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT name, email, phone, address FROM owners";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ownersList.add(new Owner(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading owners from DB: " + e.getMessage());
        }
        ownersTable.setItems(ownersList);
    }

    @FXML
    private void handleAddOwner() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javaapp3/AddOwner.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Owner");
            stage.initModality(Modality.APPLICATION_MODAL);
            AddOwnerController controller = loader.getController();
            controller.setOwnersList(ownersList);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEditOwner(Owner owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javaapp3/AddOwner.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Edit Owner");
            stage.initModality(Modality.APPLICATION_MODAL);
            AddOwnerController controller = loader.getController();
            controller.setOwnersList(ownersList);
            controller.setOwnerToEdit(owner);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteOwner(Owner owner) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = "DELETE FROM owners WHERE name = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, owner.getName());
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Owner '" + owner.getName() + "' deleted from database.");
                } else {
                    System.err.println("No owner deleted from DB. Check name value.");
                }
            }
        } catch (Exception ex) {
            System.err.println("Error deleting owner from DB: " + ex.getMessage());
        }
        // إعادة تحميل قائمة الملاك من قاعدة البيانات
        ownersList.clear();
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT name, email, phone, address FROM owners";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ownersList.add(new Owner(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading owners from DB: " + e.getMessage());
        }
        ownersTable.setItems(ownersList);
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
    private void handleShowDetails(Owner owner) {
        // Implement details display (e.g., open a new window or dialog)
        System.out.println("Details for: " + owner.getName() + ", Email: " + owner.getEmail());
    }

    @FXML
    private void handleDashboardClick() {
        // Implement navigation to Dashboard
    }

    @FXML
    private void handleOwnersClick() {
        // Already on Owners page
    }

    @FXML
    private void handleTenantsClick() {
        // Implement navigation to Tenants
    }

    @FXML
    private void handleApartmentsClick() {
        // Implement navigation to Apartments
    }

    @FXML
    private void handleHousesClick() {
        // Implement navigation to Houses
    }

    @FXML
    private void handleAssignHouseClick() {
        // Implement navigation to Assign House
    }

    @FXML
    private void handlePaymentsClick() {
        // Implement navigation to Payments
    }

    @FXML
    private void handlePayRentClick() {
        // Implement navigation to Pay Rent
    }

    @FXML
    private void handleLogoutClick() {
        // Implement logout functionality
    }

    // Owner class for table data
    public static class Owner {
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty address;

        public Owner(String name, String email, String phone, String address) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
            this.address = new SimpleStringProperty(address);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String email) {
            this.email.set(email);
        }

        public String getPhone() {
            return phone.get();
        }

        public void setPhone(String phone) {
            this.phone.set(phone);
        }

        public String getAddress() {
            return address.get();
        }

        public void setAddress(String address) {
            this.address.set(address);
        }
    }
}
