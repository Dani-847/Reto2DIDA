package org.drk.reto2dida;

import javafx.application.Application;
import javafx.stage.Stage;
import org.drk.reto2dida.utils.JavaFXUtil;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        JavaFXUtil.initStage(stage);
        JavaFXUtil.setScene("/org/drk/reto2dida/login-view.fxml");
    }
}
