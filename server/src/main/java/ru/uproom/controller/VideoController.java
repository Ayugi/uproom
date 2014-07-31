package ru.uproom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.uproom.domain.Video;

import java.util.Collections;
import java.util.List;

/**
 * Created by hedin on 13.07.2014.
 */
@Controller
@RequestMapping(value = "video")
public class VideoController {
    @RequestMapping(method = RequestMethod.GET, value = "list")
    public List<Video> listDevices() {
        Video video = new Video();
        video.setId(24);
        video.setName("someVideo");
        video.setUrl("http://somewhere.ru/some");
        return Collections.singletonList(video);
    }

}
