package ru.uproom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.uproom.domain.Device;
import ru.uproom.domain.DeviceState;
import ru.uproom.service.DeviceStorageService;
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

    private SessionHolder sessionHolder = SessionHolderImpl.getInstance();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Collection<Device> listDevices() {
        return storageService.fetchDevices(sessionHolder.currentUser().getId());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Device addDevice(@RequestParam String name) {
        return deviceStub();
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

    private Device deviceStub() {
        Device device1 = new Device();
        device1.setId(42);
        device1.setName("off");
        device1.setState(DeviceState.On);
        return device1;
    }
}