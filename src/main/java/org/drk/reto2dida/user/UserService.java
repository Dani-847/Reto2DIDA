package org.drk.reto2dida.user;

import org.drk.reto2dida.pelicula.Pelicula;
import org.drk.reto2dida.utils.DataProvider;
import org.hibernate.Session;

public class UserService {

    public User deleteGameFromUser(User user, Pelicula pelicula) {
        try(Session s = DataProvider.getSessionFactory().openSession()) {
            s.beginTransaction();

            // Recargar datos desde la BD
            User currentUser = s.find(User.class, user.getId());
            Pelicula peliculaToDelete = s.find(Pelicula.class, pelicula.getId());

            // Buscar y eliminar el juego de la colección y la bbdd
            currentUser.getPeliculas().remove(peliculaToDelete);
            currentUser.getPeliculas().removeIf(g -> g.getId().equals(pelicula.getId()));
            s.remove(peliculaToDelete);

            s.getTransaction().commit();

            return currentUser;
        }
    }

    public User createNewGame(Pelicula newPelicula, User actualUser) {
        try(Session s = DataProvider.getSessionFactory().openSession()) {
            actualUser.addGame(newPelicula);
            s.beginTransaction();
            s.merge(actualUser);
            s.getTransaction().commit();
            return s.find(User.class, actualUser.getId());
        }

    }
}
