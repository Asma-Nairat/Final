package com.javaapp3; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class for PayRentView.fxml
 */
public class PayRentController implements Initializable {

    // --- عناصر البحث ---
    @FXML
    private TextField searchApartmentIdField;
    @FXML
    private Button searchButton;

    // --- عناصر حساب الدفعة ---
    @FXML
    private ComboBox<String> rentTypeComboBox;
    @FXML
    private Label rentPerMonthLabel;
    @FXML
    private Label totalMonthsLabel;
    @FXML
    private Label totalRentLabel;

    // --- عناصر تفاصيل المستأجر ---
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label occupationLabel;
    @FXML
    private Label addressLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO: سنكتب الكود هنا لاحقاً
        System.out.println("Pay Rent View and Controller loaded.");
        
        // تعبئة قائمة الشهور بشكل مبدئي
        rentTypeComboBox.getItems().addAll(
                "1 Month Rent",
                "2 Month Rent",
                "3 Month Rent",
                "6 Month Rent",
                "12 Month Rent"
        );
    }    
    
}