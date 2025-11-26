package org.drk.reto2dida.user;

import org.drk.reto2dida.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserRepository implements Repository<User> {

    private SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User entity) {
        return null;
    }

    @Override
    public Optional<User> delete(User entity) {
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Long count() {
        return 0L;
    }

    public Optional<User> findByEmail(String email) {
        try(Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery(
                    "from User where email=:email",User.class);
            q.setParameter("email", email);
            return Optional.ofNullable(q.uniqueResult());
        }
    }
}
