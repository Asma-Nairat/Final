package com.javaapp3; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class for AssignApartmentView.fxml
 */
public class AssignApartmentController implements Initializable {

    // --- عناصر الاختيار ---
    @FXML
    private ComboBox<String> selectApartmentComboBox;
    @FXML
    private ComboBox<String> selectTenantComboBox;

    // --- عناصر عرض تفاصيل الشقة ---
    @FXML
    private Label apartmentAddressLabel;
    @FXML
    private Label apartmentStatusLabel;
    @FXML
    private Label apartmentRentLabel;

    // --- عناصر عرض تفاصيل المستأجر ---
    @FXML
    private Label tenantNameLabel;
    @FXML
    private Label tenantEmailLabel;
    @FXML
    private Label tenantPhoneLabel;

    // --- عناصر إدخال المبالغ ---
    @FXML
    private TextField depositAmountField;
    @FXML
    private TextField placementFeeField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Assign Apartment View and Controller loaded.");
        // تعبئة قائمة الشقق من قاعدة البيانات
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sqlApt = "SELECT name FROM apartments";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlApt)) {
                javafx.collections.ObservableList<String> apartments = javafx.collections.FXCollections.observableArrayList();
                while (rs.next()) {
                    apartments.add(rs.getString("name"));
                }
                selectApartmentComboBox.setItems(apartments);
            }
            String sqlTenant = "SELECT name FROM tenants";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlTenant)) {
                javafx.collections.ObservableList<String> tenants = javafx.collections.FXCollections.observableArrayList();
                while (rs.next()) {
                    tenants.add(rs.getString("name"));
                }
                selectTenantComboBox.setItems(tenants);
            }
        } catch (Exception e) {
            System.err.println("Error loading apartments/tenants for assignment: " + e.getMessage());
        }
    }    
    
}
