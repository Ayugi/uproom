package ru.uproom.gate.transport.dto.parameters;

/**
 * Created by osipenko on 11.09.14.
 */
public enum DeviceStateEnum {
    Unknown,
    Sleep,
    Down,
    Work,
    Add,
    Remove,
    Cancel;

    public static DeviceStateEnum fromString(String name) {
        for (DeviceStateEnum state : values()) {
            if (state.name().equalsIgnoreCase(name)) return state;
        }
        return Unknown;
    }
}
