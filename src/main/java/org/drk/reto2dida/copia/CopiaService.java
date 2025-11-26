package org.drk.reto2dida.copia;

import org.drk.reto2dida.pelicula.Pelicula;
import org.drk.reto2dida.user.User;
import org.drk.reto2dida.utils.DataProvider;

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
        Copia copia = new Copia();
        copia.setUser(user);
        copia.setMovie(pelicula);
        copia.setEstado(estado);
        copia.setSoporte(soporte);
        return copiaRepository.save(copia);
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
        copia.setEstado(estado);
        copia.setSoporte(soporte);
        return copiaRepository.update(copia);
    }
}
