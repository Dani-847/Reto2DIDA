package org.drk.reto2dida.user;

import org.drk.reto2dida.utils.DataProvider;
import org.hibernate.Session;

public class UserService {

    public User findById(Integer id) {
        try (Session s = DataProvider.getSessionFactory().openSession()) {
            return s.find(User.class, id);
        }
    }

    public User refresh(User user) {
        try (Session s = DataProvider.getSessionFactory().openSession()) {
            return s.find(User.class, user.getId());
        }
    }
}
