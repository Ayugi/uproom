package ru.uproom.gate.notifications;

import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.SendDeviceListCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.zwave.ZWaveHome;

import java.util.List;

/**
 * Handler which create device list and send it to server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class GateSendDeviceListNotificationHandler implements NotificationHandler {
    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport) {
        if (transport == null) return false;

        // create device list
        List<DeviceDTO> devices = home.getDeviceList();
        // send device list
        return transport.sendCommand(new SendDeviceListCommand(
                String.format("%d", home.getHomeId()),
                devices
        ));

    }
}
