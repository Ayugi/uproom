package ru.uproom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.uproom.domain.Device;
import ru.uproom.gate.transport.dto.DeviceState;
import ru.uproom.domain.User;
import ru.uproom.gate.transport.command.AddModeOnCommand;
import ru.uproom.gate.transport.command.CancelCommand;
import ru.uproom.gate.transport.command.RemoveModeOnCommand;
import ru.uproom.service.*;

import java.util.Collection;

/**
 * Created by hedin on 13.07.2014.
 */
@Controller
@RequestMapping(value = "devices")
public class DevicesController {
    @Autowired
    private DeviceStorageService storageService;

    @Autowired
    private GateTransport gateTransport;

    private static final Logger LOG = LoggerFactory.getLogger(DevicesController.class);

    private SessionHolder sessionHolder = SessionHolderImpl.getInstance();

    @RequestMapping(method = RequestMethod.GET, value = "status")
    @ResponseBody
    public GateTransportStatus gateStatus(){
        User user = sessionHolder.currentUser();
        if (null == user)
            return null;
        GateSocketHandler handler = gateTransport.getHandler(user.getId());
        if (null == handler)
            return GateTransportStatus.Red;
        return handler.getStatus();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Collection<Device> listDevices() {
        LOG.info("listDevices");
        LOG.info("storageService " + storageService + " sessionHolder.currentUser() " + sessionHolder.currentUser());
        return storageService.fetchDevices(sessionHolder.currentUserId());

    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Device updateDevice(@RequestBody Device device) {
        return storageService.updateDevice(sessionHolder.currentUserId(), device);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{id}")
    @ResponseBody
    public String removeDevice(@PathVariable String id) {
        return "ok";
    }

    @RequestMapping(method = RequestMethod.GET, value = "turn/{id}")
    @ResponseBody
    public Device turnDevice(@PathVariable String id,
                             @RequestParam boolean on) {
        return deviceStub();
    }


    @RequestMapping(method = RequestMethod.GET, value = "add")
    @ResponseBody
    public String addDeviceMode(){
        gateTransport.sendCommand(new AddModeOnCommand(),sessionHolder.currentUserId());
        return "ok";
    }

    @RequestMapping(method = RequestMethod.GET, value = "remove")
    @ResponseBody
    public String removeDeviceMode(){
        gateTransport.sendCommand(new RemoveModeOnCommand(),sessionHolder.currentUserId());
        return "ok";
    }

    @RequestMapping(method = RequestMethod.GET, value = "cancel")
    @ResponseBody
    public String cancelAddDeviceMode(){
        gateTransport.sendCommand(new CancelCommand(),sessionHolder.currentUserId());
        return "ok";
    }
    private Device deviceStub() {
        Device device1 = new Device();
        device1.setId(42);
        device1.setName("off");
        device1.setState(DeviceState.On);
        return device1;
    }


}