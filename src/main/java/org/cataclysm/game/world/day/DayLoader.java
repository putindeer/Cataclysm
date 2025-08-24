package org.cataclysm.game.world.day;

import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;

public class DayLoader {
    private final JsonConfig jsonConfig;

    public DayLoader() throws Exception {
        this.jsonConfig = JsonConfig.cfg("/Data/day.json", Cataclysm.getInstance());
    }

    public void save() {
        var manager = Cataclysm.getDayManager();
        if (manager != null) manager.save(this.jsonConfig);
    }

    public void restore() {
        DayManager.restore(this.jsonConfig);
    }
}
