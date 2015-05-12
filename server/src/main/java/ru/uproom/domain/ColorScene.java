package ru.uproom.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by HEDIN on 08.01.2015.
 */
@Entity
@Table(name = "scene")
@NamedQueries({
        @NamedQuery(name = "userScenes", query = "select s from ColorScene s " +
                "left join fetch s.deviceParams " +
                "where s.user = :user"),

})
public class ColorScene {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    @JsonIgnore
    private User user;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "scene")
    @JsonIgnore
    private List<ColorSceneDeviceParam> deviceParams = new ArrayList<>();

    @Transient
    private Set<Integer> deviceIds = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColorSceneDeviceParam> getDeviceParams() {
        return deviceParams;
    }

    public void setDeviceParams(List<ColorSceneDeviceParam> deviceParams) {
        this.deviceParams = deviceParams;
    }

    public Set<Integer> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(Set<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public void prepareDeviceIds(){
        deviceIds = new HashSet<>();
        for(ColorSceneDeviceParam param : deviceParams)
            deviceIds.add(param.getDeviceId());
    }
}
