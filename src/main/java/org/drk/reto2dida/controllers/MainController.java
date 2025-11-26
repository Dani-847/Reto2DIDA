package org.drk.reto2dida.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.drk.reto2dida.pelicula.Pelicula;
import org.drk.reto2dida.pelicula.PeliculaRepository;
import org.drk.reto2dida.session.SimpleSessionService;
import org.drk.reto2dida.user.User;
import org.drk.reto2dida.user.UserService;
import org.drk.reto2dida.utils.DataProvider;
import org.drk.reto2dida.utils.JavaFXUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private TableColumn<Pelicula,String> cDescripcion;
    @FXML
    private TableColumn<Pelicula,String> cPlataforma;
    @FXML
    private TableColumn<Pelicula,String> cTitulo;
    @FXML
    private TableColumn<Pelicula,String> cAño;
    @FXML
    private TableView<Pelicula> tabla;
    @FXML
    private TableColumn<Pelicula,String> cId;
    @FXML
    private Label lblUsuario;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnAñadir;

    SimpleSessionService simpleSessionService = new SimpleSessionService();
    PeliculaRepository peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());
    UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lblUsuario.setText("Juegos del usuario " + simpleSessionService.getActive().getEmail() );

        cId.setCellValueFactory( (row)->{
            return new SimpleStringProperty(String.valueOf(row.getValue().getId()));
        });
        cTitulo.setCellValueFactory( (row)->{
            String title = row.getValue().getTitle();
            /* ..... */
            return new SimpleStringProperty(title);
        });
        cAño.setCellValueFactory( (row)->{
            if(row.getValue().getYear()==null){
                return new SimpleStringProperty("-");
            }
            Integer year = row.getValue().getYear();
            Integer antigüedad = LocalDateTime.now().getYear() - year;
            return new SimpleStringProperty(antigüedad.toString()+" años");
        });
        cPlataforma.setCellValueFactory( (row)->{
            return new SimpleStringProperty(row.getValue().getPlatform());
        });
        cDescripcion.setCellValueFactory( (row)->{
            return new SimpleStringProperty(row.getValue().getDescription());
        });

        tabla.getSelectionModel().selectedItemProperty().addListener(showGame());

        simpleSessionService.getActive().getPeliculas().forEach(game -> {
            tabla.getItems().add(game);
        });

    }

    private ChangeListener<Pelicula> showGame() {
        return (obs, old, news) -> {
            if (news != null) {
                JavaFXUtil.showModal(
                        Alert.AlertType.INFORMATION,
                        news.getTitle(),
                        news.getTitle(),
                        news.toString()
                );
            }
        };
    }

    @FXML
    public void borrar(ActionEvent actionEvent) {

        if(tabla.getSelectionModel().getSelectedItem()==null) return;
        Pelicula selectedPelicula = tabla.getSelectionModel().getSelectedItem();

        // Actualizar el usuario local
        User user = userService.deleteGameFromUser(simpleSessionService.getActive(), selectedPelicula);
        simpleSessionService.update(user);

        tabla.getItems().clear();
        simpleSessionService.getActive().getPeliculas().forEach(game -> {
            tabla.getItems().add(game);
        });
    }

    @FXML
    public void añadir(ActionEvent actionEvent) {
        Pelicula newPelicula = new Pelicula();
        newPelicula.setTitle("Juego random");
        newPelicula.setPlatform("random");

        User actualUser = simpleSessionService.getActive();
        User user = userService.createNewGame(newPelicula, actualUser);
        simpleSessionService.update(user);

        tabla.getItems().clear();
        simpleSessionService.getActive().getPeliculas().forEach(game -> {
            tabla.getItems().add(game);
        });
    }
}
