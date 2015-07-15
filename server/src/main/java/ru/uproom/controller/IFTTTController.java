package ru.uproom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.uproom.domain.Device;

/**
 * Created by HEDIN on 02.07.2015.
 */
@Controller
@RequestMapping(value = "ifttt")
public class IFTTTController {

    private static final Logger LOG = LoggerFactory.getLogger(IFTTTController.class);

    @RequestMapping(method = RequestMethod.POST,produces="text/plain")
    @ResponseBody
    public String updateDevice(@RequestBody IFTTTMessage message) {
        LOG.info("ifttt message " + message);
        return "ok";
    }
}