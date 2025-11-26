// java
package org.drk.reto2dida.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import org.drk.reto2dida.copia.Copia;
import org.drk.reto2dida.copia.CopiaRepository;
import org.drk.reto2dida.pelicula.Pelicula;
import org.drk.reto2dida.pelicula.PeliculaRepository;
import org.drk.reto2dida.session.SimpleSessionService;
import org.drk.reto2dida.user.User;
import org.drk.reto2dida.utils.DataProvider;
import org.drk.reto2dida.utils.JavaFXUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula,String> colPeliId;
    @FXML private TableColumn<Pelicula,String> colPeliTitulo;
    @FXML private TableColumn<Pelicula,String> colPeliAnio;
    @FXML private TableColumn<Pelicula,String> colPeliGenero;
    @FXML private TableColumn<Pelicula,String> colPeliDirector;

    @FXML private TableView<Copia> tablaCopias;
    @FXML private TableColumn<Copia,String> colCopiaId;
    @FXML private TableColumn<Copia,String> colCopiaTitulo;
    @FXML private TableColumn<Copia,String> colCopiaEstado;
    @FXML private TableColumn<Copia,String> colCopiaSoporte;

    @FXML private Label lblUsuarioActual;
    @FXML private HBox boxAdmin;

    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbSoporte;

    @FXML private Button btnAddPeli;
    @FXML private Button btnEditPeli;
    @FXML private Button btnDelPeli;

    private final SimpleSessionService sessionService = new SimpleSessionService();
    private final PeliculaRepository peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());
    private final CopiaRepository copiaRepository = new CopiaRepository(DataProvider.getSessionFactory());

    private User active;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        active = sessionService.getActive();
        setupTables();
        loadPeliculas();
        loadCopias();
        lblUsuarioActual.setText(formatUser(active));

        boolean admin = isAdmin(active);
        boxAdmin.setVisible(admin);
        boxAdmin.setManaged(admin);

        String winTitle = "Gestor de copias y películas" + (admin ? " (admin)" : "");
        if (JavaFXUtil.getStage() != null) JavaFXUtil.getStage().setTitle(winTitle);

        cmbEstado.setItems(FXCollections.observableArrayList("bueno","nuevo","regular","dañado"));
        cmbEstado.getSelectionModel().selectFirst();
        cmbSoporte.setItems(FXCollections.observableArrayList("DVD","BluRay","Digital","VHS"));
        cmbSoporte.getSelectionModel().selectFirst();
    }

    private void setupTables() {
        colPeliId.setCellValueFactory(r -> new SimpleStringProperty(String.valueOf(r.getValue().getId())));
        colPeliTitulo.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getTitulo()));
        colPeliAnio.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getAnio() != null ? r.getValue().getAnio().toString() : "-"));
        colPeliGenero.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getGenero()));
        colPeliDirector.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getDirector()));

        colCopiaId.setCellValueFactory(r -> new SimpleStringProperty(String.valueOf(r.getValue().getId())));
        colCopiaTitulo.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getMovie() != null ? r.getValue().getMovie().getTitulo() : "-"));
        colCopiaEstado.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getEstado()));
        colCopiaSoporte.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getSoporte()));

        // Fijar modo de selección correcto
        tablaPeliculas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tablaCopias.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadPeliculas() {
        tablaPeliculas.setItems(FXCollections.observableArrayList(peliculaRepository.findAll()));
    }

    private void loadCopias() {
        tablaCopias.setItems(FXCollections.observableArrayList(copiaRepository.findByUser(active)));
    }

    private String formatUser(User u) {
        return isAdmin(u) ? u.getEmail() + " *" : u.getEmail();
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
    public void onCreateCopia(ActionEvent e) {
        Pelicula selected = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (selected == null) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Copia", "Película no seleccionada", "Selecciona una película primero.");
            return;
        }
        Copia c = new Copia();
        c.setMovie(selected);
        c.setUser(active);
        c.setEstado(cmbEstado.getValue());
        c.setSoporte(cmbSoporte.getValue());
        copiaRepository.save(c);
        loadCopias();
    }

    @FXML
    public void onEditCopia(ActionEvent e) {
        Copia copia = tablaCopias.getSelectionModel().getSelectedItem();
        if (copia == null) return;
        if (!copia.getUser().getId().equals(active.getId())) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Editar", "No permitido", "Solo tus copias.");
            return;
        }
        copia.setEstado("dañado".equalsIgnoreCase(copia.getEstado()) ? "bueno" : "dañado");
        copiaRepository.update(copia);
        tablaCopias.refresh();
    }

    @FXML
    public void onDeleteCopia(ActionEvent e) {
        Copia copia = tablaCopias.getSelectionModel().getSelectedItem();
        if (copia == null) return;
        if (!copia.getUser().getId().equals(active.getId())) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Eliminar", "No permitido", "Solo tus copias.");
            return;
        }
        copiaRepository.delete(copia);
        loadCopias();
    }

    @FXML
    public void onAddPelicula(ActionEvent e) {
        if (!isAdmin(active)) return;
        Pelicula p = new Pelicula();
        p.setTitulo("Nueva película " + System.currentTimeMillis());
        p.setAnio(LocalDateTime.now().getYear());
        p.setGenero("Desconocido");
        p.setDirector("N/A");
        peliculaRepository.save(p);
        loadPeliculas();
    }

    @FXML
    public void onEditPelicula(ActionEvent e) {
        if (!isAdmin(active)) return;
        Pelicula sel = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        TextInputDialog dlg = new TextInputDialog(sel.getTitulo());
        dlg.setTitle("Editar película");
        dlg.setHeaderText("Modificar título");
        dlg.setContentText("Título:");
        dlg.showAndWait().ifPresent(nuevo -> {
            sel.setTitulo(nuevo);
            peliculaRepository.update(sel);
            tablaPeliculas.refresh();
        });
    }

    @FXML
    public void onDeletePelicula(ActionEvent e) {
        if (!isAdmin(active)) return;
        Pelicula sel = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        peliculaRepository.delete(sel);
        loadPeliculas();
    }
}
