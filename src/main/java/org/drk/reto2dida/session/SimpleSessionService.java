package org.drk.reto2dida.session;

import org.drk.reto2dida.user.User;
import org.drk.reto2dida.utils.SessionService;

import java.util.HashMap;

public class SimpleSessionService implements SessionService<User> {

    private static User activeUser = null;
    private static HashMap<String,Object> data = new HashMap<String,Object>();

    public void login(User user) {
        activeUser = user;
    }

    public void update(User user) {
        activeUser = user;
    }

    public boolean isLoggedIn(){
        return activeUser != null;
    }

    public void logout() {
        activeUser = null;
        data.clear();
    }

    @Override
    public User getActive() { return activeUser; }

    @Override
    public void setObject(String key, Object o) { data.put(key,o); }

    @Override
    public Object getObject(String key) { return data.get(key); }

}
