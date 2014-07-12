package ru.uproom.domain;

import javax.persistence.*;

/**
 * Created by HEDIN on 10.07.2014.
 */
@Entity
@Table(name = "user")
@NamedQueries(
        @NamedQuery(name = "findAllUsers", query = "select u from User u")
)
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "username")
    private String login;
    @Column(name = "password")
    private String password;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
