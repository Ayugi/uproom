package ru.uproom.gate.devices;

import org.zwave4j.Notification;
import ru.uproom.gate.devices.zwave.ZWaveNode;

/**
 * Created by osipenko on 19.08.14.
 */
public interface ZWaveNodeCallback {
    void onCallback(ZWaveNode node, Notification notification);
}
