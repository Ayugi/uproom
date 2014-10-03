package ru.uproom.gate.devices;

import ru.uproom.gate.devices.zwave.ZWaveValue;

/**
 * Created by osipenko on 17.08.14.
 */
public interface ZWaveValueCallback {
    void onCallback(ZWaveValue value);
}
