package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
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
@GateNotificationHandlerAnnotation(value = GateNotificationType.SendDeviceList)
public class SendDeviceListGateNotificationHandler implements NotificationHandler {
    @Override
    public boolean execute(int gateId, ZWaveHome home, ServerTransportMarker transport, Notification notification) {
        if (transport == null) return false;

        // create device list
        List<DeviceDTO> devices = home.getDeviceList();
        // send device list if z-wave network ready to use
        if (!home.isReady()) return false;
        return transport.sendCommand(new SendDeviceListCommand(gateId, devices));
    }
}
