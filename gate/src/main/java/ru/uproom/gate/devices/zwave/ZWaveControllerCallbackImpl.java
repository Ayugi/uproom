package ru.uproom.gate.devices.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwave4j.ControllerCallback;
import org.zwave4j.ControllerError;
import org.zwave4j.ControllerState;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Created by osipenko on 30.09.14.
 */

@Service
public class ZWaveControllerCallbackImpl implements ControllerCallback {

    private static final Logger LOG = LoggerFactory.getLogger(GateDevicesSet.class);

    @Autowired
    GateDevicesSet home;

    @Override
    public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {

        // cancel current mode
        if (controllerState == ControllerState.CANCEL) {
            home.setControllerState(DeviceStateEnum.Work, true);
            return;
        }

        // complete current mode
        if (controllerState == ControllerState.COMPLETED) {
            home.setControllerState(DeviceStateEnum.Work, true);
            return;
        }

        // set requested mode
        if (controllerState == ControllerState.WAITING) {
            home.setControllerState(home.getRequestedMode(), true);
        }

        LOG.debug("Z-Wave controller callback : state = {}, error = {}", new Object[]{
                controllerState,
                controllerError
        });
    }
}
