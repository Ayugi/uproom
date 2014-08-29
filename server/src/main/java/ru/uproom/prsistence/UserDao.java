package ru.uproom.prsistence;

import ru.uproom.domain.User;

import java.util.List;

/**
 * Created by HEDIN on 10.07.2014.
 */
public interface UserDao {
    List<User> listUsers();
    void saveNewUser(User user);

}
