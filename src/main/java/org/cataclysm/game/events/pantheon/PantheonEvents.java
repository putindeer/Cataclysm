package org.cataclysm.game.events.pantheon;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonWarper;

import java.time.Duration;

public class PantheonEvents {
    private final PantheonOfCataclysm pantheon;
    private final PantheonDispatcher dispatcher;

    public PantheonEvents(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
        this.dispatcher = pantheon.getDispatcher();
    }

    public void handleEvents(PantheonLevels level) {
        if (level == PantheonLevels.TWISTED_CITY) this.startPantheon();
    }

    public void startPantheon() {
        dispatcher.addEffects(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, false, false));
        dispatcher.playSounds(
                Sound.sound(Key.key("entity.elder_guardian.ambient"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("entity.elder_guardian.death"), SoundCategory.MASTER, 2F, 0.55F)
        );
        dispatcher.addDelay(2000);

        dispatcher.sendActionBar("Del horizonte asoma el crepúsculo.");
        dispatcher.sendActionBar("Aunque parece el mismo de siempre...");
        dispatcher.sendActionBar("hoy se siente diferente.");
        dispatcher.sendActionBar("Hoy, la mayoría no verá la luz del mediodía.");
        dispatcher.sendActionBar("En esta tumba reposan cinco dioses.");
        dispatcher.sendActionBar("Sus nombres se desvanecen...");
        dispatcher.sendActionBar("mas sus leyendas perduran.");
        dispatcher.sendActionBar("Venced al retorcido.");
        dispatcher.sendActionBar("Enfrentaos a la calamidad.");
        dispatcher.sendActionBar("Derrocad al pálido...");
        dispatcher.sendActionBar("Abrazad el vacío...");
        dispatcher.sendActionBar("...y poned fin al Cataclismo.");
        dispatcher.addDelay(1000);

        dispatcher.playSounds(
                Sound.sound(Key.key("item.trident.thunder"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("item.trident.thunder"), SoundCategory.MASTER, 2F, 0.65F),
                Sound.sound(Key.key("entity.elder_guardian.death"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("cataclysm.pantheon.teleport"), SoundCategory.MASTER, 5F, 0.5F)
        );
        dispatcher.schedule(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(
                        MiniMessage.miniMessage().deserialize("<gradient:#3a0ca3:#4361ee:#4cc9f0><bold>✦ El Panteón de Cataclysm ✦</bold></gradient>"),
                        MiniMessage.miniMessage().deserialize("<color:#a1a1aa>Se abre ante vosotros...</color>"),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4), Duration.ofSeconds(2))
                ));
            }
        });
        dispatcher.addDelay(6000);

        dispatcher.schedule(() -> PantheonWarper.warp(PantheonLevels.TWISTED_CITY));
    }
}
