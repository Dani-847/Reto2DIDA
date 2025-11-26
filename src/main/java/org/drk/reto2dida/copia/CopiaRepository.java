package org.drk.reto2dida.copia;

import org.drk.reto2dida.user.User;
import org.drk.reto2dida.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class CopiaRepository implements Repository<Copia> {

    private final SessionFactory sessionFactory;

    public CopiaRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Copia save(Copia entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
            return entity;
        }
    }

    @Override
    public Optional<Copia> delete(Copia entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(session.merge(entity));
            session.getTransaction().commit();
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<Copia> deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Copia copy = session.find(Copia.class, id);
            if (copy != null) {
                session.beginTransaction();
                session.remove(copy);
                session.getTransaction().commit();
            }
            return Optional.ofNullable(copy);
        }
    }

    @Override
    public Optional<Copia> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Copia.class, id));
        }
    }

    @Override
    public List<Copia> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Copia", Copia.class).list();
        }
    }

    @Override
    public Long count() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(c) from Copia c", Long.class).getSingleResult();
        }
    }

    public List<Copia> findByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Copia c where c.user = :user", Copia.class)
                    .setParameter("user", user)
                    .list();
        }
    }
}
