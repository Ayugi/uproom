package ru.uproom.gate.devices.zwave;

import org.zwave4j.Notification;

/**
 * Created by osipenko on 19.08.14.
 */
public interface ZWaveNodeCallback {
    void onCallback(ZWaveNode node, Notification notification);
}
