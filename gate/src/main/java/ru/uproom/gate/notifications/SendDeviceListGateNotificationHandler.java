package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.dto.DeviceDTO;

import java.util.List;

/**
 * Handler which create device list and send it to server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@GateNotificationHandlerAnnotation(value = GateNotificationType.SendDeviceList)
public class SendDeviceListGateNotificationHandler implements NotificationHandler {
    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        // create device list
        List<DeviceDTO> devices = null;//home.getDeviceList();
        // send device list if z-wave network ready to use
        //if (!home.isReady()) return false;
        //transport.sendCommand(new SendDeviceListCommand(devices));
        return true;
    }
}
