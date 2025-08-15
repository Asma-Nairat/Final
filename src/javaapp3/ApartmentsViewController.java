package javaapp3; // <-- تأكد من أن اسم الحزمة صحيح

// ==================== استيراد المكتبات الضرورية ====================
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.Node;
/**
 * هذا هو المتحكم الرئيسي لواجهة عرض الشقق على شكل بطاقات
 */
public class ApartmentsViewController implements Initializable {

    // يتم ربط هذه المتغيرات بالعناصر المقابلة لها في ملف ApartmentsView.fxml
    @FXML
    private TilePane apartmentsGrid;

    @FXML
    private Button addApartmentButton;

    /**
     * هذه الدالة تعمل تلقائياً عند تحميل الواجهة لأول مرة.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // لاحقاً، ستقوم هنا بجلب البيانات من قاعدة البيانات.
        // حالياً، سنضيف بيانات وهمية للتجربة لنرى كيف ستبدو الواجهة.
        for (int i = 1; i <= 10; i++) {
            // لكل شقة، نقوم بإنشاء بطاقة وإضافتها إلى الشبكة
            VBox apartmentCard = createApartmentCard("Apartment #" + i, "Amman, Tla' Al-Ali");
            apartmentsGrid.getChildren().add(apartmentCard);
        }
    }    

    /**
     * هذه الدالة مرتبطة بـ onAction لزر الإضافة في ملف FXML.
     * ستعمل عند الضغط على زر "+ Add Apartment".
     */
  // هذا الكود يوضع داخل ملف ApartmentsViewController.java
/*
@FXML
private void handleAddApartment(ActionEvent event) {
    System.out.println("Attempting to load AddApartmentView.fxml..."); // للتأكد من أننا في المكان الصحيح

    try {
        // تأكد من أننا نقوم بتحميل الملف الصحيح "AddApartmentView.fxml"
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddApartmentView.fxml"));
        
        // إذا كان الملف غير موجود، سيحدث خطأ هنا وسيتم طبعه
        if (loader.getLocation() == null) {
            System.err.println("Error: Cannot find FXML file 'AddApartmentView.fxml'. Check the file name and location.");
            // إظهار رسالة خطأ للمستخدم
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Not Found");
            alert.setHeaderText(null);
            alert.setContentText("The required form file 'AddApartmentView.fxml' could not be found.");
            alert.showAndWait();
            return; // إيقاف التنفيذ
        }
        
        Parent root = loader.load();

        // إنشاء نافذة جديدة (Stage) لعرض الواجهة
        Stage stage = new Stage();
        stage.setTitle("Add New Apartment");
        stage.setScene(new Scene(root));

        // منع المستخدم من التفاعل مع النافذة الرئيسية حتى يتم إغلاق هذه النافذة
        stage.initModality(Modality.APPLICATION_MODAL);

        // عرض النافذة وانتظار إغلاقها
        stage.showAndWait();

        // (اختياري) يمكنك تحديث قائمة الشقق هنا بعد إغلاق النافذة
        System.out.println("Add Apartment window was closed.");

    } catch (IOException e) {
        e.printStackTrace(); // طباعة أي خطأ يحدث أثناء التحميل
    }
}
    
    */
    
    
    
    // هذا الكود يجب أن يكون في ملف ApartmentsViewController.java

// هذا الكود يوضع في ملف ApartmentsViewController.java

@FXML
private void handleAddApartment(ActionEvent event) {
    // هذا الكود يوضع داخل دالة handleAddApartment في ملف ApartmentsViewController.java

try {
    // طباعة رسالة للتأكد من أننا ندخل هنا
    System.out.println("Attempting to load the 'AddApartmentView.fxml' file...");

    // تحميل واجهة الإضافة الجديدة
    FXMLLoader loader = new FXMLLoader(getClass().getResource("AddApartmentView.fxml"));
    Parent root = loader.load();

    // إنشاء نافذة جديدة (Stage) لعرض الواجهة
    Stage stage = new Stage();
    stage.setTitle("Add New Apartment");
    stage.setScene(new Scene(root));

    // منع المستخدم من التفاعل مع النافذة الرئيسية حتى يتم إغلاق هذه النافذة
    stage.initModality(Modality.APPLICATION_MODAL);

    // طباعة رسالة قبل إظهار النافذة
    System.out.println("Showing the 'Add New Apartment' window...");

    // عرض النافذة وانتظار إغلاقها
    stage.showAndWait();

    // طباعة رسالة بعد إغلاق النافذة
    System.out.println("'Add New Apartment' window was closed.");

} catch (IOException e) {
    // في حال حدوث أي خطأ أثناء تحميل الملف، سيتم طبعه هنا
    System.err.println("!!! FAILED TO LOAD FXML FILE !!!");
    e.printStackTrace();
}
}
    
    
    
    
    
    
    
    /**
     * دالة معدلة لإنشاء بطاقة شقة واحدة بشكل جذاب وتفاعلي.
     * @param name اسم الشقة
     * @param location موقع الشقة
     * @return VBox يمثل بطاقة الشقة الكاملة.
     */
   // هذا هو الكود الكامل والمحدث لدالة createApartmentCard
// الصقه بالكامل في ملف ApartmentsViewController.java

// هذا هو الكود الكامل والمصحح والنهائي لدالة createApartmentCard
// الصقه بالكامل في ملف ApartmentsViewController.java

private VBox createApartmentCard(String name, String location) {
    VBox card = new VBox();
    card.setPrefSize(220, 280);
    card.setAlignment(Pos.TOP_LEFT);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);");
    card.setPadding(new Insets(10));
    card.setSpacing(10); 

    HBox topSection = new HBox();
    Rectangle imagePlaceholder = new Rectangle(140, 140);
    imagePlaceholder.setStyle("-fx-fill: #e0e0e0; -fx-arc-width: 10; -fx-arc-height: 10;");

    VBox buttonsContainer = new VBox(10);
    buttonsContainer.setPadding(new Insets(0, 0, 0, 10));
    Button editButton = new Button("✏️");
    Button deleteButton = new Button("🗑️");
    
    editButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
    
    editButton.setPrefWidth(80);
    deleteButton.setPrefWidth(80);

    buttonsContainer.getChildren().addAll(editButton, deleteButton);
    topSection.getChildren().addAll(imagePlaceholder, buttonsContainer);

    VBox infoSection = new VBox(5);
    Label nameLabel = new Label(name);
    nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    
    Label locationLabel = new Label(location);
    locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");

    infoSection.getChildren().addAll(nameLabel, locationLabel);
    card.getChildren().addAll(topSection, infoSection);

    // --- برمجة الأحداث ---

    
    
    
    
    editButton.setOnAction(e -> {
    System.out.println("--- Card Edit button clicked for: " + name + " ---");
    // === التغيير هنا ===
    openEditView(name, location, card); 
    e.consume(); 
});
    
    
    
    
    
    

    deleteButton.setOnAction(e -> {
        System.out.println("--- Card Delete button clicked for: " + name + " ---");
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this apartment?");
        confirmationAlert.setContentText(name);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apartmentsGrid.getChildren().remove(card); 
                System.out.println("Apartment '" + name + "' was deleted from the view.");
            }
        });
    });

    // === الشرط المصحح هنا ===
    // هذا الشرط يتحقق مما إذا كان مصدر الحدث هو الزر نفسه أو أي شيء داخل الزر
    card.setOnMouseClicked(event -> {
        Object source = event.getTarget();
        if (source instanceof Node) {
            Node sourceNode = (Node) source;
            if (sourceNode == editButton || sourceNode.getParent() == editButton || 
                sourceNode == deleteButton || sourceNode.getParent() == deleteButton) {
                // إذا كان الضغط على أحد الأزرار، لا تفعل شيئًا هنا
                return;
            }
        }
        
        // إذا لم يكن الضغط على الأزرار، افتح نافذة التفاصيل
        System.out.println("--- Card clicked: " + name + ". Opening details view... ---");
        openDetailsView(name,  card);
    });

    return card;
}

    /**
     * دالة لفتح شاشة التفاصيل الجديدة وتمرير البيانات إليها.
     * @param apartmentName اسم الشقة التي تم الضغط عليها
     * @param apartmentLocation موقعها
     */
  // هذا الكود يوضع في ApartmentsViewController.java

private void openDetailsView(String apartmentName, VBox apartmentCard) { // <-- لاحظ إضافة البطاقة كبارامتر
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ApartmentDetailsView.fxml"));
        Parent root = loader.load();
        ApartmentDetailsController detailsController = loader.getController();
        detailsController.loadApartmentData(apartmentName, "Some Location"); // يمكنك تعديل هذا لاحقاً

        Stage stage = new Stage();
        stage.setTitle("Apartment Information");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        
        // عرض النافذة وانتظار إغلاقها
        stage.showAndWait();

        // === هنا السحر ===
        // بعد إغلاق النافذة، نتحقق من نتيجة الحذف
   
        
        
        if (detailsController.isApartmentDeleted()) {
    System.out.println("Deletion was confirmed. Removing card from main view.");
    apartmentsGrid.getChildren().remove(apartmentCard);
} 
// === الجزء الجديد ===
else if (detailsController.isEditRequested()) {
    System.out.println("Edit was requested from details view. Opening edit window...");
    // نستدعي دالة التعديل لنفس البطاقة
    openEditView(apartmentName, "Some Location", apartmentCard);
}
else {
    System.out.println("Deletion was cancelled or window was closed.");
}
        


    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    // هذه الدالة تضاف إلى ملف ApartmentsViewController.java

/**
 * دالة لفتح نافذة التعديل مع تعبئة البيانات الحالية للشقة.
 * @param name اسم الشقة
 * @param location موقع الشقة
 */
// هذا الكود يستبدل الدالة القديمة في ApartmentsViewController.java

// هذا الكود يستبدل الدالة القديمة في ApartmentsViewController.java

private void openEditView(String name, String location, VBox apartmentCard) {
    System.out.println("1. Entered openEditView method."); // <-- نقطة تفتيش 1
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddApartmentView.fxml"));
        
        // تحقق مما إذا تم العثور على الملف
        if (loader.getLocation() == null) {
            System.err.println("CRITICAL ERROR: 'AddApartmentView.fxml' not found!");
            return; // أوقف التنفيذ إذا لم يتم العثور على الملف
        }
        System.out.println("2. FXML file 'AddApartmentView.fxml' was found."); // <-- نقطة تفتيش 2

        Parent root = loader.load();
        System.out.println("3. FXML file loaded successfully into 'root'."); // <-- نقطة تفتيش 3

        AddApartmentController editController = loader.getController();
        
        // تحقق مما إذا تم تحميل الكنترولر
        if (editController == null) {
            System.err.println("CRITICAL ERROR: Controller for 'AddApartmentView.fxml' is null. Check fx:controller attribute in the FXML file.");
            return; // أوقف التنفيذ
        }
        System.out.println("4. Controller 'AddApartmentController' loaded successfully."); // <-- نقطة تفتيش 4

        // استدعاء دالة تعبئة الفورم
        editController.loadApartmentForEditing(name, "Flat", "Some Owner", "Amman", location, "Description here.");
        System.out.println("5. loadApartmentForEditing method called successfully."); // <-- نقطة تفتيش 5

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Edit Apartment"); 
        stage.initModality(Modality.APPLICATION_MODAL);
        
        System.out.println("6. Stage is ready. Showing now..."); // <-- نقطة تفتيش 6
        stage.showAndWait();

        // ... (بقية الكود الخاص بالتحديث يبقى كما هو) ...

    } catch (Exception e) { // <-- استخدم Exception الأعم للقبض على أي نوع من الأخطاء
        System.err.println("!!! AN EXCEPTION OCCURRED in openEditView !!!");
        e.printStackTrace(); // طباعة الخطأ بالكامل
    }
}
    
// هذه الدالة يجب أن تكون موجودة في ApartmentsViewController.java

@FXML
private void handleGoToTenants(ActionEvent event) throws IOException {
    // طباعة للتأكد من أن الزر يعمل
    System.out.println("Switching to Tenants View...");
    
    // الحصول على النافذة الحالية
    Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
    
    // تحميل الواجهة الجديدة
    Parent root = FXMLLoader.load(getClass().getResource("TenantsView.fxml"));
    
    // استبدال المشهد الحالي بالمشهد الجديد
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
} 
    
    
}