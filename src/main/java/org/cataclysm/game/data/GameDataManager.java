package org.cataclysm.game.data;

import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.GameManager;

public class GameDataManager {
    private final JsonConfig jsonConfig;

    public GameDataManager() throws Exception {
        this.jsonConfig = JsonConfig.cfg("/Data/game.json", Cataclysm.getInstance());
    }

    public void save() {
        Cataclysm.getGameManager().save(this.jsonConfig);
    }

    public void restore() {
        GameManager.restore(this.jsonConfig);
    }
}
