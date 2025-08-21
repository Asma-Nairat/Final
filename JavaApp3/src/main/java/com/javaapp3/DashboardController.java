package com.javaapp3;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    // --- FXML Variables ---
    @FXML private Label landlordsCountLabel;
    @FXML private Label tenantsCountLabel;
    @FXML private Label apartmentsCountLabel;
    
    // ربط البطاقات
    @FXML private VBox landlordsCard;
    @FXML private VBox tenantsCard;
    @FXML private VBox apartmentsCard;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDashboardData();
    }    
    
    private void loadDashboardData() {
        String ownersQuery = "SELECT COUNT(*) FROM owners";
        String tenantsQuery = "SELECT COUNT(*) FROM tenants";
        String apartmentsQuery = "SELECT COUNT(*) FROM apartments";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // جلب وعرض عدد الملاك
            ResultSet rsOwners = stmt.executeQuery(ownersQuery);
            if (rsOwners.next()) {
                landlordsCountLabel.setText(String.valueOf(rsOwners.getInt(1)));
            }

            // جلب وعرض عدد المستأجرين
            ResultSet rsTenants = stmt.executeQuery(tenantsQuery);
            if (rsTenants.next()) {
                tenantsCountLabel.setText(String.valueOf(rsTenants.getInt(1)));
            }

            // جلب وعرض عدد الشقق
            ResultSet rsApartments = stmt.executeQuery(apartmentsQuery);
            if (rsApartments.next()) {
                apartmentsCountLabel.setText(String.valueOf(rsApartments.getInt(1)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // --- دوال التنقل من القائمة الجانبية ---
    @FXML private void handleGoToOwners(ActionEvent event) throws IOException { navigateTo(event, "/fxml/OwnersView.fxml", "Owners Management"); }
    @FXML private void handleGoToTenants(ActionEvent event) throws IOException { navigateTo(event, "/fxml/TenantsView.fxml", "Tenants Management"); }
    @FXML private void handleGoToApartments(ActionEvent event) throws IOException { navigateTo(event, "/fxml/ApartmentsView.fxml", "Apartments Management"); }
    @FXML private void handleGoToPayments(ActionEvent event) throws IOException { navigateTo(event, "/fxml/PaymentsView.fxml", "Payments History"); }

    // --- دوال التنقل من البطاقات ---
    @FXML
    private void handleGoToOwnersCard(MouseEvent event) throws IOException {
        navigateTo(event, "/fxml/OwnersView.fxml", "Owners Management");
    }

    @FXML
    private void handleGoToTenantsCard(MouseEvent event) throws IOException {
        navigateTo(event, "/fxml/TenantsView.fxml", "Tenants Management");
    }

    @FXML
    private void handleGoToApartmentsCard(MouseEvent event) throws IOException {
        navigateTo(event, "/fxml/ApartmentsView.fxml", "Apartments Management");
    }

    // --- دوال التنقل الموحدة ---
    private void navigateTo(ActionEvent event, String fxmlFile, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
    
    private void navigateTo(MouseEvent event, String fxmlFile, String title) throws IOException {
        navigateTo(new ActionEvent(event.getSource(), event.getTarget()), fxmlFile, title);
    }
}