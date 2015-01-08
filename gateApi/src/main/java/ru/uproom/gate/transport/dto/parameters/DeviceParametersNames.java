package ru.uproom.gate.transport.dto.parameters;

    // TODO consider reflection based implementation.
/**
 * contains names of device parameters which not included in z-wave conception
 * <p/>
 * Created by osipenko on 18.09.14.
 */
public enum DeviceParametersNames {
    // gate parameters
    Unknown, // unknown
    Switch, // boolean
    Level, // int
    Color; // int

    public <T> T restoreObjectFromString(String rawValue){
        switch (this){
            case Unknown:
                return (T) rawValue;
            case Switch:
                return (T) new Boolean(rawValue);
            case Level:
            case Color:
                return (T) new Integer(rawValue);
        }
        throw new RuntimeException("restoreObjectFromString unhandled type " + this);
    }

}
