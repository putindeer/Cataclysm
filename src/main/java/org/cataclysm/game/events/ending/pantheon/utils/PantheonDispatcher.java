package org.cataclysm.game.events.ending.pantheon.utils;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PantheonDispatcher {
    private @Getter @Setter int acumulatedMillis;

    private final ScheduledExecutorService executor;
    private final Audience audience;

    public PantheonDispatcher(ScheduledExecutorService executor, Audience audience) {
        this.executor = executor;
        this.audience = audience;
        this.acumulatedMillis = 0;
    }

    /**
     * Sends an animated action bar message to the audience, revealing the text one character at a time.
     * Each character is displayed with a sound effect, and the animation duration is distributed evenly.
     *
     * @param text the message to display in the action bar
     * @param totalDuration the total duration of the animation in milliseconds
     */
    public void sendActionBar(String text, int totalDuration, int cooldown) {
        String formatTxt = TinyCaps.tinyCaps(text.toLowerCase());

        var characters = formatTxt.chars().mapToObj(c -> (char) c).toList();
        double interval = ((double) totalDuration / characters.size());

        executor.schedule(() -> {
            for (int i = 0; i < characters.size(); i++) {
                String display = formatTxt.substring(0, i + 1);
                Component message = MiniMessage.miniMessage().deserialize(display);

                executor.schedule(() -> {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                        audience.sendActionBar(message);
                        audience.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.5F, 1.95F));
                    });
                }, (i * (long) interval), TimeUnit.MILLISECONDS);
            }
        }, this.acumulatedMillis, TimeUnit.MILLISECONDS);
        this.acumulatedMillis += (totalDuration + cooldown);
    }

    /**
     * Sends a formatted message to the audience with a prefixed "pantheon of cataclysm" label.
     * The prefix and message are colorized using CataclysmColor and formatted with MiniMessage.
     *
     * @param text the message content to send
     */
    public void sendMessage(String text) {
        String prefix = wrapPrefix(CataclysmColor.PANTHEON.wrap(1) + "pantheon");
        String msg = CataclysmColor.PANTHEON.wrap(3) + text;
        audience.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "  <#727272>» " + msg));
    }

    public void playSounds(Sound @NotNull ... sounds) {
        for (Sound sound : sounds) audience.playSound(sound);
    }

    public void addEffects(PotionEffect @NotNull ... effects) {
        for (PotionEffect effect : effects) audience.forEachAudience(ad -> {
            if (ad instanceof Player player) player.addPotionEffect(effect);
        });
    }

    private @NotNull String wrapPrefix(String prefix) {
        return "<#8c8c8c><b>[<reset>" + TinyCaps.tinyCaps(prefix) + "<#8c8c8c><b>]<reset>";
    }
}
