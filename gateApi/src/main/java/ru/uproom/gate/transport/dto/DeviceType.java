package ru.uproom.gate.transport.dto;

/**
 * map of device type for server
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public enum DeviceType {
    None(""),
    Controller("Static PC Controller"),
    PowerSwitch("Binary Power Switch"),
    Dimmer("Multilevel Power Switch");

    private String stringKey;

    DeviceType(String stringKey) {
        this.stringKey = stringKey;
    }

    public static DeviceType byStringKey(String stringKey) {
        for (DeviceType value : values()) {
            if (value.getStringKey().equalsIgnoreCase(stringKey)) return value;
        }
        return None;
    }

    public String getStringKey() {
        return stringKey;
    }
}
