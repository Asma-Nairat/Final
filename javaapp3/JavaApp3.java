
    
/*
package javaapp3;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaApp3 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ApartmentsView.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Apartment Renting Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/
package javaapp3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *  هذا هو ملف تشغيل التطبيق الرئيسي
 */
public class JavaApp3 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // يجب أن يقوم بتحميل الواجهة الرئيسية فقط
        Parent root = FXMLLoader.load(getClass().getResource("ApartmentsView.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Apartment Renting Management System");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}   