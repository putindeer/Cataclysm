package org.cataclysm.api.event.data;

import lombok.Getter;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.api.event.EventManager;

@Getter
public class EventLoader {
    private final JsonConfig jsonConfig;

    public EventLoader() throws Exception {
        this.jsonConfig = JsonConfig.cfg("/Data/event.json", Cataclysm.getInstance());
    }

    public void save() {
        var event = Cataclysm.getEventManager();
        if (event == null) return;
        event.save(this.jsonConfig);
    }

    public void restore() {
        EventManager.restore(this.jsonConfig);
    }
}
