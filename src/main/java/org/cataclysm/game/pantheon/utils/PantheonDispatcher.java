package org.cataclysm.game.pantheon.utils;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PantheonDispatcher {
    private @Getter @Setter int acumulatedMillis;

    private final ScheduledExecutorService service;
    private final Audience audience;

    public PantheonDispatcher(PantheonOfCataclysm pantheon) {
        this.service = pantheon.getService().getExecutor();
        this.audience = Audience.audience(Bukkit.getOnlinePlayers());
        this.acumulatedMillis = 0;
    }

    public void sendActionBar(String text, int totalDuration) {
        String formatTxt = TinyCaps.tinyCaps(text.toLowerCase());
        var characters = formatTxt.chars().mapToObj(c -> (char) c).toList();
        double interval = ((double) totalDuration / characters.size());

        this.service.schedule(() -> {
            for (int i = 0; i < characters.size(); i++) {
                String display = formatTxt.substring(0, i + 1);
                Component message = MiniMessage.miniMessage().deserialize(display);

                this.service.schedule(() -> {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                        this.audience.sendActionBar(message);
                        this.audience.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.5F, 1.95F));
                    });
                }, (i * (long) interval), TimeUnit.MILLISECONDS);
            }
        }, this.acumulatedMillis, TimeUnit.MILLISECONDS);
        this.acumulatedMillis += totalDuration;
    }

    public void sendMessage(String text) {
        String prefix = wrapPrefix(CataclysmColor.PANTHEON.wrap(1) + "pantheon of cataclysm");
        String msg = CataclysmColor.PANTHEON.wrap(3) + text;
        this.audience.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "  <#727272>» " + msg));
    }

    private static String wrapPrefix(String prefix) {
        return "<#8c8c8c><b>[<reset>" + TinyCaps.tinyCaps(prefix) + "<#8c8c8c><b>]<reset>";
    }
}
