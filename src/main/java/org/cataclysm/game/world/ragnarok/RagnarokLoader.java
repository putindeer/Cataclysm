package org.cataclysm.game.world.ragnarok;

import lombok.Getter;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;

@Getter
public class RagnarokLoader {
    private final JsonConfig jsonConfig;

    public RagnarokLoader() throws Exception {
        this.jsonConfig = JsonConfig.cfg("/Data/ragnarok.json", Cataclysm.getInstance());
    }

    public void save() {
        Ragnarok ragnarok = Cataclysm.getRagnarok();
        if (ragnarok == null) return;
        ragnarok.save(this.jsonConfig);
    }

    public void restore() {
        Ragnarok.restore(this.jsonConfig);
    }
}