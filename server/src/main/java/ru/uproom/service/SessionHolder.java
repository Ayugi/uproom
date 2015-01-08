package ru.uproom.service;

import ru.uproom.domain.User;

/**
 * Created by HEDIN on 11.09.2014.
 */
public interface SessionHolder {
    void touchSession(String sid);
    void newSession(String sid, User user);
    User currentUser();
    int currentUserId();
}
