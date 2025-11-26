package org.drk.reto2dida.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.drk.reto2dida.copia.Copia;
import org.drk.reto2dida.copia.CopiaService;
import org.drk.reto2dida.pelicula.Pelicula;
import org.drk.reto2dida.pelicula.PeliculaRepository;
import org.drk.reto2dida.session.SimpleSessionService;
import org.drk.reto2dida.user.User;
import org.drk.reto2dida.utils.DataProvider;
import org.drk.reto2dida.utils.JavaFXUtil;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private TableColumn<Copia, String> cTitulo;
    @FXML
    private TableColumn<Copia, String> cEstado;
    @FXML
    private TableColumn<Copia, String> cSoporte;
    @FXML
    private TableColumn<Copia, String> cAnio;
    @FXML
    private TableView<Copia> tabla;
    @FXML
    private TableColumn<Copia, String> cId;
    @FXML
    private Label lblUsuario;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnAñadir;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnAñadirPelicula;

    SimpleSessionService simpleSessionService = new SimpleSessionService();
    CopiaService copiaService = new CopiaService();
    PeliculaRepository peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User activeUser = simpleSessionService.getActive();
        lblUsuario.setText("Copias del usuario: " + activeUser.getEmail());

        // Hide admin button if user is not admin
        if (activeUser.getIsAdmin() == null || !activeUser.getIsAdmin()) {
            btnAñadirPelicula.setVisible(false);
            btnAñadirPelicula.setManaged(false);
        }

        cId.setCellValueFactory((row) -> {
            return new SimpleStringProperty(String.valueOf(row.getValue().getId()));
        });
        cTitulo.setCellValueFactory((row) -> {
            Pelicula movie = row.getValue().getMovie();
            return new SimpleStringProperty(movie != null ? movie.getTitulo() : "-");
        });
        cAnio.setCellValueFactory((row) -> {
            Pelicula movie = row.getValue().getMovie();
            if (movie == null || movie.getAnio() == null) {
                return new SimpleStringProperty("-");
            }
            return new SimpleStringProperty(String.valueOf(movie.getAnio()));
        });
        cEstado.setCellValueFactory((row) -> {
            return new SimpleStringProperty(row.getValue().getEstado());
        });
        cSoporte.setCellValueFactory((row) -> {
            return new SimpleStringProperty(row.getValue().getSoporte());
        });

        tabla.getSelectionModel().selectedItemProperty().addListener(showCopiaDetail());

        loadCopias();
    }

    private void loadCopias() {
        tabla.getItems().clear();
        User activeUser = simpleSessionService.getActive();
        List<Copia> copias = copiaService.findByUser(activeUser);
        tabla.getItems().addAll(copias);
    }

    private ChangeListener<Copia> showCopiaDetail() {
        return (obs, old, newCopia) -> {
            if (newCopia != null) {
                Pelicula movie = newCopia.getMovie();
                String details = "Copia ID: " + newCopia.getId() + "\n" +
                        "Estado: " + newCopia.getEstado() + "\n" +
                        "Soporte: " + newCopia.getSoporte() + "\n\n" +
                        "Película: " + (movie != null ? movie.getTitulo() : "N/A") + "\n" +
                        "Año: " + (movie != null && movie.getAnio() != null ? movie.getAnio() : "N/A") + "\n" +
                        "Género: " + (movie != null ? movie.getGenero() : "N/A") + "\n" +
                        "Director: " + (movie != null ? movie.getDirector() : "N/A");
                JavaFXUtil.showModal(
                        Alert.AlertType.INFORMATION,
                        "Detalle de Copia",
                        movie != null ? movie.getTitulo() : "Copia",
                        details
                );
            }
        };
    }

    @FXML
    public void borrar(ActionEvent actionEvent) {
        Copia selectedCopia = tabla.getSelectionModel().getSelectedItem();
        if (selectedCopia == null) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Atención", "Sin selección", "Seleccione una copia para borrar.");
            return;
        }

        User activeUser = simpleSessionService.getActive();
        boolean deleted = copiaService.deleteCopia(selectedCopia, activeUser);
        if (deleted) {
            loadCopias();
            JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Copia eliminada", "La copia ha sido eliminada correctamente.");
        } else {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No autorizado", "No puede eliminar copias que no le pertenecen.");
        }
    }

    @FXML
    public void añadir(ActionEvent actionEvent) {
        List<Pelicula> peliculas = peliculaRepository.findAll();
        if (peliculas.isEmpty()) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Atención", "Sin películas", "No hay películas disponibles. Un administrador debe añadir películas primero.");
            return;
        }

        // Create dialog to select movie
        ChoiceDialog<Pelicula> movieDialog = new ChoiceDialog<>(peliculas.get(0), peliculas);
        movieDialog.setTitle("Añadir Copia");
        movieDialog.setHeaderText("Seleccione una película");
        movieDialog.setContentText("Película:");
        Optional<Pelicula> selectedMovie = movieDialog.showAndWait();

        if (selectedMovie.isEmpty()) {
            return;
        }

        // Create dialog to select estado
        ChoiceDialog<String> estadoDialog = new ChoiceDialog<>("bueno", "bueno", "dañado");
        estadoDialog.setTitle("Añadir Copia");
        estadoDialog.setHeaderText("Seleccione el estado de la copia");
        estadoDialog.setContentText("Estado:");
        Optional<String> selectedEstado = estadoDialog.showAndWait();

        if (selectedEstado.isEmpty()) {
            return;
        }

        // Create dialog to select soporte
        ChoiceDialog<String> soporteDialog = new ChoiceDialog<>("DVD", "DVD", "Blu-ray", "VHS");
        soporteDialog.setTitle("Añadir Copia");
        soporteDialog.setHeaderText("Seleccione el soporte de la copia");
        soporteDialog.setContentText("Soporte:");
        Optional<String> selectedSoporte = soporteDialog.showAndWait();

        if (selectedSoporte.isEmpty()) {
            return;
        }

        User activeUser = simpleSessionService.getActive();
        copiaService.createCopia(activeUser, selectedMovie.get(), selectedEstado.get(), selectedSoporte.get());
        loadCopias();
        JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Copia añadida", "La copia ha sido añadida correctamente.");
    }

    @FXML
    public void editar(ActionEvent actionEvent) {
        Copia selectedCopia = tabla.getSelectionModel().getSelectedItem();
        if (selectedCopia == null) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Atención", "Sin selección", "Seleccione una copia para editar.");
            return;
        }

        // Select new estado
        ChoiceDialog<String> estadoDialog = new ChoiceDialog<>(selectedCopia.getEstado(), "bueno", "dañado");
        estadoDialog.setTitle("Editar Copia");
        estadoDialog.setHeaderText("Seleccione el nuevo estado");
        estadoDialog.setContentText("Estado:");
        Optional<String> newEstado = estadoDialog.showAndWait();

        if (newEstado.isEmpty()) {
            return;
        }

        // Select new soporte
        ChoiceDialog<String> soporteDialog = new ChoiceDialog<>(selectedCopia.getSoporte(), "DVD", "Blu-ray", "VHS");
        soporteDialog.setTitle("Editar Copia");
        soporteDialog.setHeaderText("Seleccione el nuevo soporte");
        soporteDialog.setContentText("Soporte:");
        Optional<String> newSoporte = soporteDialog.showAndWait();

        if (newSoporte.isEmpty()) {
            return;
        }

        User activeUser = simpleSessionService.getActive();
        Copia updated = copiaService.updateCopia(selectedCopia, newEstado.get(), newSoporte.get(), activeUser);
        if (updated != null) {
            loadCopias();
            JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Copia actualizada", "La copia ha sido actualizada correctamente.");
        } else {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No autorizado", "No puede editar copias que no le pertenecen.");
        }
    }

    @FXML
    public void añadirPelicula(ActionEvent actionEvent) {
        User activeUser = simpleSessionService.getActive();
        if (activeUser.getIsAdmin() == null || !activeUser.getIsAdmin()) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No autorizado", "Solo los administradores pueden añadir películas.");
            return;
        }

        // Dialog for titulo
        TextInputDialog tituloDialog = new TextInputDialog();
        tituloDialog.setTitle("Añadir Película");
        tituloDialog.setHeaderText("Introduce el título de la película");
        tituloDialog.setContentText("Título:");
        Optional<String> titulo = tituloDialog.showAndWait();
        if (titulo.isEmpty() || titulo.get().trim().isEmpty()) {
            return;
        }

        // Dialog for año
        TextInputDialog anioDialog = new TextInputDialog();
        anioDialog.setTitle("Añadir Película");
        anioDialog.setHeaderText("Introduce el año de la película");
        anioDialog.setContentText("Año:");
        Optional<String> anioStr = anioDialog.showAndWait();
        Integer anio = null;
        if (anioStr.isPresent() && !anioStr.get().trim().isEmpty()) {
            try {
                anio = Integer.parseInt(anioStr.get().trim());
            } catch (NumberFormatException e) {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "Año inválido", "El año debe ser un número.");
                return;
            }
        }

        // Dialog for genero
        TextInputDialog generoDialog = new TextInputDialog();
        generoDialog.setTitle("Añadir Película");
        generoDialog.setHeaderText("Introduce el género de la película");
        generoDialog.setContentText("Género:");
        Optional<String> genero = generoDialog.showAndWait();
        if (genero.isEmpty() || genero.get().trim().isEmpty()) {
            return;
        }

        // Dialog for director
        TextInputDialog directorDialog = new TextInputDialog();
        directorDialog.setTitle("Añadir Película");
        directorDialog.setHeaderText("Introduce el director de la película");
        directorDialog.setContentText("Director:");
        Optional<String> director = directorDialog.showAndWait();

        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(titulo.get().trim());
        pelicula.setAnio(anio);
        pelicula.setGenero(genero.get().trim());
        pelicula.setDirector(director.isPresent() ? director.get().trim() : null);

        peliculaRepository.save(pelicula);
        JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Película añadida", "La película '" + pelicula.getTitulo() + "' ha sido añadida correctamente.");
    }
}
