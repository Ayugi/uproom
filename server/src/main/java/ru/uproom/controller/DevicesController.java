package ru.uproom.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.uproom.domain.Device;
import ru.uproom.domain.DeviceState;

import java.util.ArrayList;

/**
 * Created by hedin on 13.07.2014.
 */
@Controller
@RequestMapping(value = "device")
public class DevicesController {
    @RequestMapping(method = RequestMethod.GET, value = "list")
    @ResponseBody
    public List<Device> listDevices() {
        Device device1 = deviceStub();

        Device device2 = new Device();
        device2.setId(43);
        device2.setName("on");
        device2.setState(DeviceState.Off);

        List<Device> deviceList = new ArrayList<>();
        deviceList.add(device1);
        deviceList.add(device2);
        return deviceList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "add")
    @ResponseBody
    public Device addDevice(@RequestParam String name) {
        return deviceStub();
    }

    @RequestMapping(method = RequestMethod.GET, value = "remove/{id}")
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