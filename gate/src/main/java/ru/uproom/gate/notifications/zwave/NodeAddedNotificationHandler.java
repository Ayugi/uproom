package ru.uproom.gate.notifications.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.notifications.NotificationHandler;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NODE_ADDED)
public class NodeAddedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeAddedNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        home.addGateDevice((int) notification.getNodeId());

        LOG.debug("z-wave notification : {};" +
                        "\n\tnode ID : {}; node type : {};" +
                        "\n\tproduct ID : {}; product type : {}; product name : {};" +
                        "\n\tmanufacturer ID : {}; manufacturer name : {}",
                new Object[]{
                        notification.getType(),
                        notification.getNodeId(),
                        Manager.get().getNodeType(home.getHomeId(), notification.getNodeId()),
                        Manager.get().getNodeProductId(home.getHomeId(), notification.getNodeId()),
                        Manager.get().getNodeProductType(home.getHomeId(), notification.getNodeId()),
                        Manager.get().getNodeProductName(home.getHomeId(), notification.getNodeId()),
                        Manager.get().getNodeManufacturerId(home.getHomeId(), notification.getNodeId()),
                        Manager.get().getNodeManufacturerName(home.getHomeId(), notification.getNodeId())
                }
        );

        return true;
    }
}
