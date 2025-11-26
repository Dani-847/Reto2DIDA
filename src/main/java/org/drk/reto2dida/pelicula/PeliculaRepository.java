package org.drk.reto2dida.pelicula;

import org.drk.reto2dida.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class PeliculaRepository implements Repository<Pelicula> {

    private final SessionFactory sessionFactory;

    public PeliculaRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Pelicula save(Pelicula entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Pelicula managed = session.merge(entity);
            session.getTransaction().commit();
            return managed;
        }
    }

    public Pelicula update(Pelicula entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Pelicula managed = session.merge(entity);
            session.getTransaction().commit();
            return managed;
        }
    }

    @Override
    public Optional<Pelicula> delete(Pelicula entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(session.merge(entity));
            session.getTransaction().commit();
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public Optional<Pelicula> deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Pelicula pelicula = session.find(Pelicula.class, id);
            if (pelicula != null) {
                session.beginTransaction();
                session.remove(pelicula);
                session.getTransaction().commit();
            }
            return Optional.ofNullable(pelicula);
        }
    }

    @Override
    public Optional<Pelicula> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Pelicula.class, id));
        }
    }

    @Override
    public List<Pelicula> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Pelicula", Pelicula.class).list();
        }
    }

    @Override
    public Long count() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(p) from Pelicula p", Long.class).getSingleResult();
        }
    }
}
