package org.drk.reto2dida.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *  Clase de utilidad para gestionar la ventana de JavaFX
 */
public class JavaFXUtil {

    private static Stage stage;

    private JavaFXUtil() {}

    public static void initStage(Stage stage) {
        JavaFXUtil.stage = stage;
    }

    public static Stage getStage() {
        return stage;
    }

    public static <T> T setScene(String fxml){
        try{
            FXMLLoader loader = new FXMLLoader(JavaFXUtil.class.getResource(fxml));
            Parent root = loader.load();
            T controller = loader.getController();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            return controller;
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static void showModal(Alert.AlertType type, String title, String header, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        // Evitar NPE cuando la Stage todavía no tiene Scene (p. ej. durante FXMLLoader.load())
        if (stage != null && stage.getScene() != null) {
            alert.initOwner(stage);
        }
        alert.showAndWait();
    }

}
