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
import javafx.stage.Modality;
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
    
// في ملف DashboardController.java

private void loadDashboardData() {
    // 1. استدعاء الدوال من DatabaseManager لجلب الأرقام
    int ownersCount = DatabaseManager.getOwnersCount();
    int tenantsCount = DatabaseManager.getTenantsCount();
    int apartmentsCount = DatabaseManager.getApartmentsCount();
    
    // 2. تحديث الليبلات (Labels) في الواجهة بالأرقام الجديدة
    // نستخدم String.valueOf() لتحويل الرقم إلى نص
    landlordsCountLabel.setText(String.valueOf(ownersCount));
    tenantsCountLabel.setText(String.valueOf(tenantsCount));
    apartmentsCountLabel.setText(String.valueOf(apartmentsCount));
    
    System.out.println("Dashboard data loaded: Owners=" + ownersCount + ", Tenants=" + tenantsCount + ", Apartments=" + apartmentsCount);
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