package org.cataclysm.api.event.data;

import org.cataclysm.api.event.EventManager;

public class EventData {
    public String id;
    public String colorValue;
    public int timeLeft;
    public int duration;

    public EventData(EventManager event) {
        this.id = event.id;
        this.timeLeft = event.timeLeft;
        this.duration =  event.duration;
        this.colorValue = event.color.name().toUpperCase();
    }
}
