package ru.uproom.domain;

import ru.uproom.gate.transport.dto.DeviceDTO;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hedin on 13.07.2014.
 */
@Entity
@Table(name = "device")
@NamedQuery(name = "userDevices", query = "select d from Device d where d.user = :user")
public class Device {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "zid")
    private int zid;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    @Transient
    private DeviceState state;
    @Transient
    private Map<String, String> parameters = new HashMap<>();

    public Device() {
    }

    public Device(DeviceDTO dto) {
        id = dto.getId();
        zid = dto.getZId();
        parameters = dto.getParameters();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        this.state = state;
    }

    public int getZid() {
        return zid;
    }

    public void setZid(int zid) {
        this.zid = zid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void mergeById(Device device) {
        if (0 != device.getZid())
            zid = device.getZid();
        if (null != device.getState())
            state = device.getState();
        parameters.putAll(device.getParameters());
    }
}
