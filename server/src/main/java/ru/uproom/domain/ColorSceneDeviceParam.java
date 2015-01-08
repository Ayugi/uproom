package ru.uproom.domain;

import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by HEDIN on 08.01.2015.
 */
@Entity
@Table(name = "sceneParam")
public class ColorSceneDeviceParam {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "scene")
    private ColorScene scene;

    @Column(name = "device")
    private int deviceId;

    @Column(name = "param")
    private DeviceParametersNames parametersName;

    @Column(name = "value")
    private String value;

    public ColorSceneDeviceParam() {
    }

    public ColorSceneDeviceParam(int deviceId, DeviceParametersNames parametersName, String value) {
        this.deviceId = deviceId;
        this.parametersName = parametersName;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ColorScene getScene() {
        return scene;
    }

    public void setScene(ColorScene scene) {
        this.scene = scene;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceParametersNames getParametersName() {
        return parametersName;
    }

    public void setParametersName(DeviceParametersNames parametersName) {
        this.parametersName = parametersName;
    }

    public String getValue() {
        return value;
    }

    public Object restoreParamValueObject(){
        return parametersName.restoreObjectFromString(value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
