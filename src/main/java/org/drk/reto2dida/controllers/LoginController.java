package org.drk.reto2dida.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.drk.reto2dida.session.AuthService;
import org.drk.reto2dida.session.SimpleSessionService;
import org.drk.reto2dida.user.User;
import org.drk.reto2dida.user.UserRepository;
import org.drk.reto2dida.utils.DataProvider;
import org.drk.reto2dida.utils.JavaFXUtil;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField txtContraseña;
    @FXML
    private TextField txtCorreo;
    @FXML
    private Label info;
    @FXML
    private ComboBox<String> cmbUsuarios;

    private UserRepository userRepository;
    private AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userRepository = new UserRepository(DataProvider.getSessionFactory());
        authService = new AuthService(userRepository);
        configurarComboUsuarios();
        cmbUsuarios.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String email = newValue.replace("*", ""); // Remove asterisk if present
                txtCorreo.setText(email);
            }
        });
    }

    private void configurarComboUsuarios() {
        var users = userRepository.findAll();
        for(User user : users)
            cmbUsuarios.getItems().add(formatCorreoAdmin(user));
    }

    private String formatCorreoAdmin(User u) {
        String base = u.getEmail();
        if (u.getIs_admin()) return base + "*";
        return base;
    }

    @javafx.fxml.FXML
    public void entrar(ActionEvent actionEvent) {
        Optional<User> user = authService.validateUser(txtCorreo.getText(),txtContraseña.getText() );
        if (user.isPresent()){
            SimpleSessionService sessionService = new SimpleSessionService();
            sessionService.login(user.get());
            sessionService.setObject("id", user.get().getId());
            JavaFXUtil.showModal(
                    Alert.AlertType.CONFIRMATION,
                    "Login Exitoso",
                    "Bienvenido " + user.get().getEmail() + "!",
                    "Has iniciado sesión correctamente."
            );
            JavaFXUtil.setScene("/org/drk/reto2dida/main-view.fxml");
        }
    }

    @FXML
    public void Salir(ActionEvent e) {
        System.exit(0);
    }
}
