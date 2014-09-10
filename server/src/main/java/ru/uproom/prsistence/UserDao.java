package ru.uproom.prsistence;

import javassist.compiler.ast.StringL;
import ru.uproom.domain.User;

import java.util.List;

/**
 * Created by HEDIN on 10.07.2014.
 */
public interface UserDao {
    List<User> listUsers();
    void saveNewUser(User user);
    User authenticate(String login, String password);
}
