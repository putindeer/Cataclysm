package org.cataclysm.game.events.pantheon.utils;

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
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PantheonDispatcher {
    private @Getter @Setter int acumulatedMillis;

    private final ScheduledExecutorService executor;
    private final Audience audience;

    public PantheonDispatcher(PantheonOfCataclysm pantheon) {
        this.executor = pantheon.getExecutor();
        this.audience = Audience.audience(Bukkit.getOnlinePlayers());
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
                        audience.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.35F, 2F));
                    });
                }, (i * (long) interval), TimeUnit.MILLISECONDS);
            }
        }, this.acumulatedMillis, TimeUnit.MILLISECONDS);
        this.acumulatedMillis += (totalDuration + cooldown);
    }

    public void sendActionBar(String text) {
        sendActionBar(text, 10.0);
    }

    public void sendActionBar(String text, double lps) {
        long letterCount = text.chars().filter(Character::isLetter).count();
        int totalDuration = (int) ((letterCount / lps) * 1000);
        if (totalDuration < 1000 && letterCount > 0) totalDuration = 1000;
        this.sendActionBar(text, totalDuration, totalDuration/2);
    }

    /**
     * Sends a formatted message to the audience with a prefixed label.
     * The prefix and message are colorized using CataclysmColor and formatted with MiniMessage.
     *
     * @param text the message content to send
     */
    public void sendMessage(String text) {
        String prefix = wrapPrefix("<gradient:#c28d3c:#c4b59f>ᴘᴀɴᴛʜᴇᴏɴ");
        audience.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "  <#727272>» <#c2bcb2>" + text));
        audience.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.MASTER, 3F, .5F));
        audience.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.MASTER, 3F, 1.5F));
    }

    /**
     * Plays the given sounds for the audience.
     *
     * @param sounds the sounds to play
     */
    public void playSounds(Sound... sounds) {
        executor.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                for (Sound sound : sounds) audience.playSound(sound);
            });
        }, this.acumulatedMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Adds the specified potion effects to all players in the audience.
     *
     * @param effects the potion effects to add
     */
    public void addEffects(PotionEffect... effects) {
        for (PotionEffect effect : effects) audience.forEachAudience(ad -> {
            if (ad instanceof Player player) player.addPotionEffect(effect);
        });
    }

    public void schedule(Runnable runnable, int millis) {
        executor.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), runnable);
        }, this.acumulatedMillis + millis, TimeUnit.MILLISECONDS);
    }

    public void schedule(Runnable runnable) {
        executor.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), runnable);
        }, this.acumulatedMillis, TimeUnit.MILLISECONDS);
    }

    public void addDelay(int millis) {
        this.acumulatedMillis += millis;
    }

    public void resetDelay() {
        this.acumulatedMillis = 0;
    }

    /**
     * Wraps the given prefix string in a formatted tag for display.
     * Applies a color and bold style to the brackets, resets formatting inside,
     * and converts the prefix to tiny caps using TinyCaps.
     *
     * @param prefix the prefix string to format
     * @return the formatted prefix string for MiniMessage
     */
    private String wrapPrefix(String prefix) {
        return "<#8c8c8c><b>[<reset>" + prefix + "<#8c8c8c><b>]<reset>";
    }
}
