package org.cataclysm.game.events.limited.data;

import org.cataclysm.game.events.limited.EventManager;

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
