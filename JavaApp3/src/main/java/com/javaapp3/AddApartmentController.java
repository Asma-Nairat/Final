package com.javaapp3;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddApartmentController implements Initializable {

    @FXML private TextField apartmentNameField;
    @FXML private ComboBox<String> apartmentTypeCombo;
    @FXML private TextField apartmentOwnerField;
    @FXML private TextField townField;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionArea;
    @FXML private Button chooseImageButton;
    @FXML private Label imageNameLabel;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private File selectedImageFile;
    private boolean saved = false;
    
    // *** إضافة: متغير لتخزين ID الشقة في حالة التعديل ***
    private Integer apartmentIdToEdit = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        apartmentTypeCombo.getItems().addAll("Flat", "Studio", "Villa", "Bungalow");
    }

    public boolean isSaved() {
        return saved;
    }

    /**
     * دالة جديدة كلياً: يتم استدعاؤها عند فتح النافذة للتعديل.
     * هي تقوم بجلب البيانات من قاعدة البيانات وتعبئة الحقول.
     * @param apartmentId الـ ID الخاص بالشقة التي سيتم تعديلها
     */
    public void loadApartmentForEditing(int apartmentId) {
        this.apartmentIdToEdit = apartmentId;
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT a.name, a.type, a.town, a.location, a.description, a.image_path,
                       o.name as owner_name
                FROM apartments a
                LEFT JOIN owners o ON a.owner_id = o.id
                WHERE a.id = ?
            """;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    apartmentNameField.setText(rs.getString("name"));
                    apartmentTypeCombo.setValue(rs.getString("type"));
                    apartmentOwnerField.setText(rs.getString("owner_name"));
                    townField.setText(rs.getString("town"));
                    locationField.setText(rs.getString("location"));
                    descriptionArea.setText(rs.getString("description"));
                    String imagePath = rs.getString("image_path");
                    if (imagePath != null && !imagePath.isEmpty()) {
                        selectedImageFile = new File(imagePath);
                        imageNameLabel.setText(selectedImageFile.getName());
                    }
                    
                    addButton.setText("Save Changes");
                    Stage stage = (Stage) addButton.getScene().getWindow();
                    if (stage != null) {
                        stage.setTitle("Edit Apartment");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading apartment for editing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        String name = apartmentNameField.getText().trim();
        String type = apartmentTypeCombo.getValue();
        String ownerName = apartmentOwnerField.getText().trim();
        String town = townField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (name.isEmpty() || type == null || ownerName.isEmpty() || town.isEmpty()) {
            showAlert("Input Error", "Please fill in all required fields (*)");
            return;
        }
        
        String imagePath = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : null;
        
        boolean success;
        // *** تعديل: التحقق إذا كنا في وضع التعديل أم الإضافة ***
        if (apartmentIdToEdit != null) {
            success = updateApartmentInDatabase(name, type, ownerName, town, location, description, imagePath);
        } else {
            success = DatabaseManager.addApartment(name, type, ownerName, town, location, description, imagePath);
        }
        
        if (success) {
            this.saved = true;
            closeWindow();
        } else {
            showAlert("Database Error", "Operation failed. Please check the owner's name and try again.");
        }
    }
    
    /**
     * دالة جديدة: لتنفيذ استعلام UPDATE في قاعدة البيانات.
     */
    private boolean updateApartmentInDatabase(String name, String type, String ownerName, String town, String location, String description, String imagePath) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            int ownerId = -1;
            String ownerQuery = "SELECT id FROM owners WHERE name = ?";
            try (PreparedStatement ownerStmt = conn.prepareStatement(ownerQuery)) {
                ownerStmt.setString(1, ownerName);
                ResultSet rs = ownerStmt.executeQuery();
                if (rs.next()) {
                    ownerId = rs.getInt("id");
                } else {
                    System.err.println("Owner not found: " + ownerName);
                    return false;
                }
            }

            String sql = """
                UPDATE apartments 
                SET owner_id = ?, name = ?, type = ?, town = ?, location = ?, description = ?, image_path = ?
                WHERE id = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ownerId);
                pstmt.setString(2, name);
                pstmt.setString(3, type);
                pstmt.setString(4, town);
                pstmt.setString(5, location);
                pstmt.setString(6, description);
                pstmt.setString(7, imagePath);
                pstmt.setInt(8, this.apartmentIdToEdit); // استخدام ID الشقة الحالية
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("Error updating apartment in DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleChooseImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Apartment Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) chooseImageButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);
        if (selectedImageFile != null) {
            imageNameLabel.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}