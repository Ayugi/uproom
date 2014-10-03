package ru.uproom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.uproom.domain.Device;
import ru.uproom.domain.DeviceState;
import ru.uproom.gate.transport.command.AddModeOnCommand;
import ru.uproom.gate.transport.command.CancelCommand;
import ru.uproom.gate.transport.command.RemoveModeOnCommand;
import ru.uproom.service.DeviceStorageService;
import ru.uproom.service.GateTransport;
import ru.uproom.service.SessionHolder;
import ru.uproom.service.SessionHolderImpl;

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

    private SessionHolder sessionHolder = SessionHolderImpl.getInstance();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Collection<Device> listDevices() {
        return storageService.fetchDevices(sessionHolder.currentUser().getId());
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Device updateDevice(@RequestBody Device device) {
        return storageService.updateDevice(sessionHolder.currentUser().getId(), device);
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
        gateTransport.sendCommand(new AddModeOnCommand(),sessionHolder.currentUser().getId());
        return "ok";
    }

    @RequestMapping(method = RequestMethod.GET, value = "remove")
    @ResponseBody
    public String removeDeviceMode(){
        gateTransport.sendCommand(new RemoveModeOnCommand(),sessionHolder.currentUser().getId());
        return "ok";
    }

    @RequestMapping(method = RequestMethod.GET, value = "cancel")
    @ResponseBody
    public String cancelAddDeviceMode(){
        gateTransport.sendCommand(new CancelCommand(),sessionHolder.currentUser().getId());
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