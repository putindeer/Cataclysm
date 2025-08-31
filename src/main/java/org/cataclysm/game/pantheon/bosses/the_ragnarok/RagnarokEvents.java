package org.cataclysm.game.pantheon.bosses.the_ragnarok;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class RagnarokEvents {
    private final TheRagnarok ragnarok;

    public RagnarokEvents(TheRagnarok ragnarok) {
        this.ragnarok = ragnarok;
    }

    public void changePhase() {
        ragnarok.getSoundtrack().stopAll();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000, 0, false, false, false));
        }

        ragnarok.dispatcher.sendActionBar("Parece que aquí se acabo todo.", 1200);
        ragnarok.dispatcher.sendActionBar("No queda nadie más para salvaros.", 1200, 1000);
        ragnarok.dispatcher.sendActionBar("Este será el fin de vuestra aventura.", 1200, 1000);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            ragnarok.getSoundtrack().loop("PHASE_2", 360);
        }, 120);

        ragnarok.dispatcher.sendActionBar("Que algo más pueda ayudaros...", 1500);
        ragnarok.dispatcher.sendActionBar("Que ELLOS puedan ayudaros.", 1000);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            ragnarok.dispatcher.sendMessage("La defensa ha bajado a 0!");
        }, 200);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            ragnarok.dispatcher.sendMessage("Muertos, escriban el nombre de un superviviente para ayudarle en la pelea!");
        }, 270);
    }

    private final int startDelay = 31 * 20;
    public void entrance() {
        this.ragnarok.getController().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, false));

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            this.show();
            this.ragnarok.getController().removePotionEffect(PotionEffectType.INVISIBILITY);
        }, this.startDelay);
    }

    public void show() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.showTitle(getTitle());
            players.playSound(Sound.sound(Key.key("cataclysm.ragnarok"), Sound.Source.MASTER, 1, 0.85F));
            players.playSound(Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.AMBIENT, 1.35F, 0.55F));
            players.playSound(Sound.sound(Key.key("entity.skeleton_horse.death"), Sound.Source.AMBIENT, 1.35F, 0.75F));
            players.playSound(Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.AMBIENT, 1.15F, 0.5F));
        }
    }

    private @NotNull Title getTitle() {
        return Title.title(
                MiniMessage.miniMessage().deserialize("<#478db6><obf><b>||<reset> <#478db6>Ragnarök <#478db6><obf><b>||"),
                MiniMessage.miniMessage().deserialize("<#9b9b9b>Mighty god of Cataclysm"),
                Title.Times.times(
                        Duration.ofMillis(1000),
                        Duration.ofMillis(2500),
                        Duration.ofMillis(3000)
                ));
    }
}
