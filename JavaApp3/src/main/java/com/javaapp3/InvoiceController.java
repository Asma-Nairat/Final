package com.javaapp3;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoiceController implements Initializable {

    // --- عناصر رأس الفاتورة ---
    @FXML private Label invoiceIdLabel;
    @FXML private Label invoiceDateLabel;

    // --- عناصر معلومات المستأجر ---
    @FXML private Label tenantNameLabel;
    @FXML private Label tenantPhoneLabel;
    @FXML private Label tenantEmailLabel;
    @FXML private Label tenantAddressLabel;

    // --- عناصر معلومات المالك ---
    @FXML private Label landlordNameLabel;
    @FXML private Label landlordPhoneLabel;
    @FXML private Label landlordEmailLabel;
    @FXML private Label landlordAddressLabel;

    // --- عناصر تفاصيل الفاتورة ---
    @FXML private Label apartmentAddressLabel;
    @FXML private Label rentMonthLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label paymentStatusLabel;
    @FXML private Label totalAmountLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Invoice View and Controller loaded.");
        
        // تعيين التاريخ الحالي كافتراضي
        invoiceDateLabel.setText("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
    /**
     * دالة لتحميل بيانات الفاتورة من قاعدة البيانات
     */
    public void loadInvoiceData(int paymentId) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT p.id, p.payment_date, p.amount, p.month, p.payment_method, p.payment_status, p.description,
                       t.name as tenant_name, t.phone as tenant_phone, t.email as tenant_email, t.address as tenant_address,
                       o.name as owner_name, o.phone as owner_phone, o.email as owner_email, o.address as owner_address,
                       a.name as apartment_name, a.town, a.location
                FROM payments p
                JOIN tenants t ON p.tenant_id = t.id
                JOIN apartments a ON p.apartment_id = a.id
                JOIN owners o ON a.owner_id = o.id
                WHERE p.id = ?
            """;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, paymentId);
                java.sql.ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    // رأس الفاتورة
                    invoiceIdLabel.setText("INVOICE #" + rs.getInt("id"));
                    String paymentDate = rs.getString("payment_date");
                    if (paymentDate != null) {
                        invoiceDateLabel.setText("Date: " + paymentDate);
                    }
                    
                    // معلومات المستأجر
                    tenantNameLabel.setText(rs.getString("tenant_name"));
                    tenantPhoneLabel.setText(rs.getString("tenant_phone") != null ? rs.getString("tenant_phone") : "N/A");
                    tenantEmailLabel.setText(rs.getString("tenant_email") != null ? rs.getString("tenant_email") : "N/A");
                    tenantAddressLabel.setText(rs.getString("tenant_address") != null ? rs.getString("tenant_address") : "N/A");
                    
                    // معلومات المالك
                    landlordNameLabel.setText(rs.getString("owner_name"));
                    landlordPhoneLabel.setText(rs.getString("owner_phone") != null ? rs.getString("owner_phone") : "N/A");
                    landlordEmailLabel.setText(rs.getString("owner_email") != null ? rs.getString("owner_email") : "N/A");
                    landlordAddressLabel.setText(rs.getString("owner_address") != null ? rs.getString("owner_address") : "N/A");
                    
                    // تفاصيل الفاتورة
                    String apartmentInfo = rs.getString("apartment_name");
                    if (rs.getString("town") != null) {
                        apartmentInfo += ", " + rs.getString("town");
                    }
                    if (rs.getString("location") != null) {
                        apartmentInfo += ", " + rs.getString("location");
                    }
                    apartmentAddressLabel.setText(apartmentInfo);
                    
                    rentMonthLabel.setText(rs.getString("month") != null ? rs.getString("month") : "N/A");
                    paymentMethodLabel.setText(rs.getString("payment_method") != null ? rs.getString("payment_method") : "Cash");
                    
                    String status = rs.getString("payment_status");
                    paymentStatusLabel.setText(status != null ? status : "Completed");
                    
                    // تلوين الحالة
                    if ("Completed".equals(status)) {
                        paymentStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("Pending".equals(status)) {
                        paymentStatusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        paymentStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                    
                    // المبلغ الإجمالي
                    String amount = rs.getString("amount");
                    totalAmountLabel.setText(amount != null ? amount + " JD" : "0.00 JD");
                    
                } else {
                    System.err.println("Payment not found with ID: " + paymentId);
                    loadDefaultData();
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading invoice data: " + e.getMessage());
            e.printStackTrace();
            loadDefaultData();
        }
    }
    
    /**
     * دالة لإنشاء فاتورة بناءً على معرف المستأجر والشقة (لدفعة جديدة)
     */
    public void createInvoiceForRent(int tenantId, int apartmentId, double amount, String month, String paymentMethod) {
        try (java.sql.Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT t.name as tenant_name, t.phone as tenant_phone, t.email as tenant_email, t.address as tenant_address,
                       o.name as owner_name, o.phone as owner_phone, o.email as owner_email, o.address as owner_address,
                       a.name as apartment_name, a.town, a.location
                FROM tenants t, apartments a, owners o
                WHERE t.id = ? AND a.id = ? AND a.owner_id = o.id
            """;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, tenantId);
                stmt.setInt(2, apartmentId);
                java.sql.ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    // رأس الفاتورة
                    invoiceIdLabel.setText("INVOICE #" + System.currentTimeMillis()); // معرف مؤقت
                    invoiceDateLabel.setText("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    
                    // معلومات المستأجر
                    tenantNameLabel.setText(rs.getString("tenant_name"));
                    tenantPhoneLabel.setText(rs.getString("tenant_phone") != null ? rs.getString("tenant_phone") : "N/A");
                    tenantEmailLabel.setText(rs.getString("tenant_email") != null ? rs.getString("tenant_email") : "N/A");
                    tenantAddressLabel.setText(rs.getString("tenant_address") != null ? rs.getString("tenant_address") : "N/A");
                    
                    // معلومات المالك
                    landlordNameLabel.setText(rs.getString("owner_name"));
                    landlordPhoneLabel.setText(rs.getString("owner_phone") != null ? rs.getString("owner_phone") : "N/A");
                    landlordEmailLabel.setText(rs.getString("owner_email") != null ? rs.getString("owner_email") : "N/A");
                    landlordAddressLabel.setText(rs.getString("owner_address") != null ? rs.getString("owner_address") : "N/A");
                    
                    // تفاصيل الفاتورة
                    String apartmentInfo = rs.getString("apartment_name");
                    if (rs.getString("town") != null) {
                        apartmentInfo += ", " + rs.getString("town");
                    }
                    if (rs.getString("location") != null) {
                        apartmentInfo += ", " + rs.getString("location");
                    }
                    apartmentAddressLabel.setText(apartmentInfo);
                    
                    rentMonthLabel.setText(month);
                    paymentMethodLabel.setText(paymentMethod);
                    paymentStatusLabel.setText("Completed");
                    paymentStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    
                    totalAmountLabel.setText(String.format("%.2f JD", amount));
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating invoice: " + e.getMessage());
            e.printStackTrace();
            loadDefaultData();
        }
    }
    
    /**
     * تحميل بيانات افتراضية في حالة الخطأ
     */
    private void loadDefaultData() {
        invoiceIdLabel.setText("INVOICE #0000");
        tenantNameLabel.setText("N/A");
        tenantPhoneLabel.setText("N/A");
        tenantEmailLabel.setText("N/A");
        tenantAddressLabel.setText("N/A");
        landlordNameLabel.setText("N/A");
        landlordPhoneLabel.setText("N/A");
        landlordEmailLabel.setText("N/A");
        landlordAddressLabel.setText("N/A");
        apartmentAddressLabel.setText("N/A");
        rentMonthLabel.setText("N/A");
        paymentMethodLabel.setText("N/A");
        paymentStatusLabel.setText("N/A");
        totalAmountLabel.setText("0.00 JD");
    }
}