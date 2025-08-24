package org.cataclysm.game.world.ragnarok;

import lombok.Getter;
import lombok.Setter;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.jetbrains.annotations.NotNull;

@Getter
public class Ragnarok {
    private @NotNull @Setter RagnarokData data;
    private final @NotNull RagnarokThread thread;
    private final @NotNull RagnarokBossBar bossBar;

    public Ragnarok(@NotNull RagnarokData data) {
        this.data = data;
        this.bossBar = new RagnarokBossBar(data);
        this.thread = new RagnarokThread(this);
    }

    public Ragnarok() {
        this(RagnarokData.autoBuild());
    }

    public void save(@NotNull JsonConfig jsonConfig) {
        jsonConfig.setJsonObject(Cataclysm.getGson().toJsonTree(this.data).getAsJsonObject());
        try {
            jsonConfig.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void restore(@NotNull JsonConfig jsonConfig) {
        if (!jsonConfig.getJsonObject().entrySet().isEmpty()) {
            var ragnarok = new Ragnarok(Cataclysm.getGson().fromJson(jsonConfig.getJsonObject(), RagnarokData.class));
            ragnarok.thread.run();
            Cataclysm.setRagnarok(ragnarok);
        }
    }
}