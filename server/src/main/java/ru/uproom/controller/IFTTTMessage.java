package ru.uproom.controller;

import java.util.Date;

/**
 * Created by HEDIN on 02.07.2015.
 *  * {
 * at: "July 02, 2015 at 12:29AM",
 * img: "http://maps.google.com/maps/api/staticmap?center=55.73135500000001,37.474097700000016&zoom=19&size=640x440&scale=1&maptype=roadmap&sensor=false&markers=color:red%7C55.73135500000001,37.474097700000016" ,
 * map: "https://maps.google.com/?q=55.73135500000001,37.474097700000016&z=19",
 * event: "exited" }

 */
public class IFTTTMessage {
    private String at;
    private String img;
    private String map;
    private String event;
    private String key;

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "IFTTTMessage{" +
                "at='" + at + '\'' +
                ", img='" + img + '\'' +
                ", map='" + map + '\'' +
                ", event='" + event + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
