package org.cataclysm.api.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Soundtrack {
    private final HashMap<String, Sound> soundtrack = new HashMap<>();
    private ScheduledFuture<?> future;

    public void addTrack(String key, Key sound) {
        this.soundtrack.put(key, Sound.sound(sound, Sound.Source.AMBIENT, 0.65F, 1F));
    }

    public Sound getSound(String key) {
        return this.soundtrack.get(key);
    }

    public void play(Sound sound) {
        for (var player : Bukkit.getOnlinePlayers()) {
            player.playSound(sound);
        }
        this.future.cancel(true);
        this.future = null;
    }

    public void stopAll() {
        for (var player : Bukkit.getOnlinePlayers()) {
            for (var x : this.soundtrack.entrySet()) {
                player.stopSound(x.getValue());
            }
        }
        this.future.cancel(true);
        this.future = null;
    }

    public void loop(String key, int duration) {
        var sound = this.getSound(key);
        if (sound == null) return;
        this.loop(sound, duration);
    }

    public void loop(Sound sound, int duration) {
        var bossFight = Cataclysm.getBossFight();
        if (bossFight == null) return;

        var arena = bossFight.getArena();
        var nearby = arena.getPlayersInArena();
        for (var player : nearby) player.playSound(sound);

        ScheduledExecutorService service = bossFight.getThread().getService();
        this.future = service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.loop(sound, duration));
        }, duration, TimeUnit.SECONDS);
    }
}
