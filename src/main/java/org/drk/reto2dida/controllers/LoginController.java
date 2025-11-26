package org.drk.reto2dida.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import org.drk.reto2dida.session.AuthService;
import org.drk.reto2dida.session.SimpleSessionService;
import org.drk.reto2dida.user.User;
import org.drk.reto2dida.user.UserRepository;
import org.drk.reto2dida.utils.DataProvider;
import org.drk.reto2dida.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtContraseña;
    @FXML private TextField txtCorreo;
    @FXML private Label info;
    @FXML private ComboBox<User> cmbUsuarios;

    private UserRepository userRepository;
    private AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userRepository = new UserRepository(DataProvider.getSessionFactory());
        authService = new AuthService(userRepository);

        var users = userRepository.findAll();
        cmbUsuarios.setItems(FXCollections.observableArrayList(users));

        cmbUsuarios.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatUser(item));
            }
        });
        cmbUsuarios.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatUser(item));
            }
        });

        cmbUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) {
                txtCorreo.setText(sel.getEmail());
                txtContraseña.setText(sel.getPassword());
            }
        });

        if (!users.isEmpty()) cmbUsuarios.getSelectionModel().selectFirst();
    }

    private String formatUser(User u) {
        String base = u.getEmail();
        if (isAdmin(u)) return base + " *";
        return base;
    }

    private boolean isAdmin(User u) {
        try {
            var m = u.getClass().getMethod("getRol");
            Object rol = m.invoke(u);
            return rol != null && "admin".equalsIgnoreCase(rol.toString());
        } catch (Exception ignore) {
            try {
                var m2 = u.getClass().getMethod("isAdmin");
                Object v = m2.invoke(u);
                return v instanceof Boolean && (Boolean) v;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    @FXML
    public void entrar(ActionEvent e) {
        var selected = cmbUsuarios.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SimpleSessionService session = new SimpleSessionService();
            session.login(selected);
            session.setObject("id", selected.getId());
            JavaFXUtil.setScene("/org/drk/reto2dida/main-view.fxml");
            return;
        }
        var user = authService.validateUser(txtCorreo.getText(), txtContraseña.getText());
        if (user.isPresent()) {
            SimpleSessionService session = new SimpleSessionService();
            session.login(user.get());
            session.setObject("id", user.get().getId());
            JavaFXUtil.setScene("/org/drk/reto2dida/main-view.fxml");
        } else {
            info.setText("Credenciales inválidas");
        }
    }

    @FXML
    public void Salir(ActionEvent e) {
        System.exit(0);
    }
}
