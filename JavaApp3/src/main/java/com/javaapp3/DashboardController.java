package com.javaapp3;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;

public class DashboardController {

    // Sidebar Menu Items
    @FXML
    private HBox dashboardMenu;

    @FXML
    private HBox ownersMenu;

    @FXML
    private HBox tenantsMenu;

    @FXML
    private HBox apartmentsMenu;

    @FXML
    private HBox housesMenu;

    @FXML
    private HBox assignHouseMenu;

    @FXML
    private HBox paymentsMenu;

    @FXML
    private HBox paymentsMenu1;

    @FXML
    private HBox payRentMenu;

    @FXML
    private HBox techniciansMenu;

    @FXML
    private HBox maintenanceRequestMenu;

    @FXML
    private HBox logoutMenu;

    // Main content area for dynamic page loading
    @FXML
    private AnchorPane mainContent;

    // Example labels (لو بدك تربطهم لاحقاً)
    @FXML
    private Label dashboardTitle;

    // ================== Event Handlers ================== //

    @FXML
    private void handleDashboardClick() {
        System.out.println("Dashboard clicked");
        setActiveMenu(dashboardMenu);
    }

    @FXML
    private void handleOwnersClick() {
        System.out.println("Landlords clicked");
        setActiveMenu(ownersMenu);
        loadPage("/fxml/OwnersView.fxml");
    }

    @FXML
    private void handleTenantsClick() {
        System.out.println("Tenants clicked");
        setActiveMenu(tenantsMenu);
        loadPage("/fxml/TenantsView.fxml");
    }

    @FXML
    private void handleApartmentsClick() {
        System.out.println("Apartments clicked");
        setActiveMenu(apartmentsMenu);
        loadPage("/fxml/ApartmentsView.fxml");
    }

    @FXML
    private void handleHousesClick() {
        System.out.println("Payments clicked");
        setActiveMenu(housesMenu);
        loadPage("/fxml/PaymentsView.fxml");
    }

    @FXML
    private void handleAssignHouseClick() {
        System.out.println("Assign Apartment clicked");
        setActiveMenu(assignHouseMenu);
        loadPage("/fxml/AssignApartmentView.fxml");
    }

    @FXML
    private void handlePaymentsClick() {
        System.out.println("Payments (extra menu) clicked");
        setActiveMenu(paymentsMenu);
        loadPage("/fxml/PaymentsView.fxml");
    }

    @FXML
    private void handlePayRentClick() {
        System.out.println("Pay Rent (extra menu) clicked");
        setActiveMenu(payRentMenu);
        loadPage("/fxml/PayRentView.fxml");
    }

    @FXML
    private void handleTechniciansClick() {
        System.out.println("Technicians clicked");
        setActiveMenu(techniciansMenu);
        loadPage("/fxml/Technicians.fxml");
    }

    @FXML
    private void handleMaintenanceRequestClick() {
        System.out.println("Maintenance Requests clicked");
        setActiveMenu(maintenanceRequestMenu);
        loadPage("/fxml/MaintenanceRequest.fxml");
    }

    @FXML
    private void handleLogoutClick() {
        System.out.println("Logout clicked");
        // هون بتحط منطق تسجيل الخروج
    }

    // ================== Helper Method ================== //
    private void loadPage(String fxmlPath) {
        try {
            Node page = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveMenu(HBox activeMenu) {
        // Reset all menus with null checks
        if (dashboardMenu != null) dashboardMenu.setStyle("-fx-background-color: transparent;");
        if (ownersMenu != null) ownersMenu.setStyle("-fx-background-color: transparent;");
        if (tenantsMenu != null) tenantsMenu.setStyle("-fx-background-color: transparent;");
        if (apartmentsMenu != null) apartmentsMenu.setStyle("-fx-background-color: transparent;");
        if (housesMenu != null) housesMenu.setStyle("-fx-background-color: transparent;");
        if (assignHouseMenu != null) assignHouseMenu.setStyle("-fx-background-color: transparent;");
        if (paymentsMenu != null) paymentsMenu.setStyle("-fx-background-color: transparent;");
        if (paymentsMenu1 != null) paymentsMenu1.setStyle("-fx-background-color: transparent;");
        if (payRentMenu != null) payRentMenu.setStyle("-fx-background-color: transparent;");
        if (logoutMenu != null) logoutMenu.setStyle("-fx-background-color: transparent;");

        // Highlight active menu
        if (activeMenu != null) {
            activeMenu.setStyle("-fx-background-color: #34495e;");
        }
    }
}
