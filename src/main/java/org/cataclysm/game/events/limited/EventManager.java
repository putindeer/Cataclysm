package org.cataclysm.game.events.limited;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.events.limited.data.EventData;
import org.cataclysm.game.events.limited.data.EventLoader;
import org.jetbrains.annotations.NotNull;

public class EventManager {
    public final EventBossBar barManager;
    public final EventThread thread;
    public EventData data;

    public final String id;
    public final CataclysmColor color;
    public final int duration;
    public int timeLeft;

    public EventManager(@NotNull EventData data) {
        this(data.id, data.duration, data.timeLeft, BossBar.Color.RED, CataclysmColor.valueOf(data.colorValue.toUpperCase()));
    }

    public EventManager(String id, int duration, BossBar.Color barColor, CataclysmColor color) {
        this(id, duration, duration, barColor, color);
    }

    public EventManager(String id, int duration, int timeLeft, BossBar.Color barColor, CataclysmColor color) {
        this.id = id;
        this.duration = duration;
        this.timeLeft = timeLeft;
        this.barManager = new EventBossBar(this, barColor, color);
        this.color = color;
        this.thread = new EventThread(this);
        this.data = new EventData(this);
    }

    public void start() {
        for (var player : Bukkit.getOnlinePlayers()) {
            this.barManager.bossBar.addViewer(player);
        }

        Cataclysm.setEventManager(this);

        this.thread.runTimer();
    }

    public void stop() {
        for (var player : Bukkit.getOnlinePlayers()) {
            this.barManager.bossBar.removeViewer(player);
        }

        Cataclysm.setEventManager(null);

        this.thread.shutdown();

        try {
            var success = new EventLoader().getJsonConfig().getFile().delete();
            if (success) Cataclysm.getInstance().getLogger().info("Event Data has been deleted.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            var event = new EventManager(Cataclysm.getGson().fromJson(jsonConfig.getJsonObject(), EventData.class));
            event.thread.runTimer();
            Cataclysm.setEventManager(event);
        }
    }
}
