package ru.uproom.domain;

/**
 * Created by hedin on 13.07.2014.
 */
public class Device {
    private int id;
    private String name;
    private DeviceState state;

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
}
