package org.cataclysm.game.world.time.data;

import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.world.time.TimeManager;

public class TimeLoader {
    private final JsonConfig jsonConfig;

    public TimeLoader() throws Exception {
        this.jsonConfig = JsonConfig.cfg("/Data/day.json", Cataclysm.getInstance());
    }

    public void save() {
        TimeManager manager = Cataclysm.getTimeManager();
        if (manager != null) manager.save(this.jsonConfig);
    }

    public void restore() {
        TimeManager.restore(this.jsonConfig);
    }
}
