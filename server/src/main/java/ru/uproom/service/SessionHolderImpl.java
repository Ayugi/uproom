package ru.uproom.service;

import org.springframework.stereotype.Component;
import ru.uproom.domain.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HEDIN on 11.09.2014.
 */
public class SessionHolderImpl implements SessionHolder{
    private static SessionHolderImpl instance = new SessionHolderImpl();

    public static SessionHolderImpl getInstance() {
        return instance;
    }

    private ThreadLocal<User> user = new ThreadLocal<>();
    Map<String, User> userSessions= new HashMap<>();

    @Override
    public void touchSession(String sid) {
        user.set(userSessions.get(sid));
    }

    @Override
    public void newSession(String sid, User user) {
        userSessions.put(sid, user);
    }

    @Override
    public User currentUser() {
        return user.get();
    }
}
