package ru.uproom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.uproom.domain.User;
import ru.uproom.prsistence.UserDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HEDIN on 12.07.2014.
 */
@Controller(value = "user")
@RequestMapping(value = "user")
public class UserController {
    @Autowired
    private UserDao userDao;

    @RequestMapping(method = RequestMethod.GET, value = "list")
    @ResponseBody
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @RequestMapping(method = RequestMethod.GET, value = "register")
    @ResponseBody
    public String register(@RequestParam String login, @RequestParam String password){
        userDao.saveNewUser(new User(login, password));
        return "ok";
    }
}
