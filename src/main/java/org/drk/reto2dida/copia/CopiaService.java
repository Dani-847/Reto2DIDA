package org.drk.reto2dida.copia;

import org.drk.reto2dida.pelicula.Pelicula;
import org.drk.reto2dida.user.User;
import org.drk.reto2dida.utils.DataProvider;
import org.hibernate.Session;

import java.util.List;

public class CopiaService {

    private final CopiaRepository copiaRepository;

    public CopiaService() {
        this.copiaRepository = new CopiaRepository(DataProvider.getSessionFactory());
    }

    public List<Copia> findByUser(User user) {
        return copiaRepository.findByUser(user);
    }

    public Copia createCopia(User user, Pelicula pelicula, String estado, String soporte) {
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            session.beginTransaction();
            
            User managedUser = session.find(User.class, user.getId());
            Pelicula managedPelicula = session.find(Pelicula.class, pelicula.getId());
            
            Copia copia = new Copia();
            copia.setUser(managedUser);
            copia.setMovie(managedPelicula);
            copia.setEstado(estado);
            copia.setSoporte(soporte);
            
            session.persist(copia);
            session.getTransaction().commit();
            
            return copia;
        }
    }

    public boolean deleteCopia(Copia copia, User activeUser) {
        // Authorization check: only owner can delete their copies
        if (!copia.getUser().getId().equals(activeUser.getId())) {
            return false;
        }
        copiaRepository.delete(copia);
        return true;
    }

    public Copia updateCopia(Copia copia, String estado, String soporte, User activeUser) {
        // Authorization check: only owner can edit their copies
        if (!copia.getUser().getId().equals(activeUser.getId())) {
            return null;
        }
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            session.beginTransaction();
            Copia managedCopia = session.find(Copia.class, copia.getId());
            managedCopia.setEstado(estado);
            managedCopia.setSoporte(soporte);
            session.getTransaction().commit();
            return managedCopia;
        }
    }
}
