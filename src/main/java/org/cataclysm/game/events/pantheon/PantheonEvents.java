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
import org.cataclysm.global.utils.text.font.TinyCaps;

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

    public void startTwistedWardenFight() {

    }

    public void startPantheon() {
        dispatcher.addEffects(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false));
        dispatcher.playSounds(
                Sound.sound(Key.key("entity.elder_guardian.ambient"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("entity.elder_guardian.death"), SoundCategory.MASTER, 2F, 0.55F)
        );
        dispatcher.addDelay(2000);
        dispatcher.sendActionBar("Del horizonte asoma el crepúsculo.");
        dispatcher.sendActionBar("Aunque parece el mismo de siempre...");
        dispatcher.sendActionBar("hoy se siente diferente.");
        dispatcher.sendActionBar("Hoy, la mayoría no verá la luz del mediodía.");
        dispatcher.addDelay(2000);
        dispatcher.sendActionBar("En esta tumba reposan cinco dioses.", 12);
        dispatcher.sendActionBar("Sus nombres se desvanecen...", 12);
        dispatcher.sendActionBar("mas sus leyendas perduran.", 12);
        dispatcher.addDelay(1500);
        dispatcher.sendActionBar("Mortales...");
        dispatcher.sendActionBar("Venced al retorcido.");
        dispatcher.sendActionBar("Enfrentaos a la calamidad.");
        dispatcher.sendActionBar("Derrocad al pálido...");
        dispatcher.sendActionBar("Abrazad el vacío...");
        dispatcher.addDelay(1000);
        dispatcher.sendActionBar("...y poned fin al Cataclismo.", 5);
        dispatcher.addDelay(1000);
        dispatcher.playSounds(
                Sound.sound(Key.key("item.trident.thunder"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("item.trident.thunder"), SoundCategory.MASTER, 2F, 0.65F),
                Sound.sound(Key.key("entity.elder_guardian.death"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("cataclysm.pantheon.teleport"), SoundCategory.MASTER, 5F, 0.5F),
                Sound.sound(Key.key("cataclysm.pantheon.teleport"), SoundCategory.MASTER, 5F, 0.65F),
                Sound.sound(Key.key("music_disc.stal"), SoundCategory.MASTER, 2F, 0.65F)
        );
        dispatcher.schedule(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(
                        MiniMessage.miniMessage().deserialize("<gradient:#D3AF37:#E1B768><b>Pantheon Of Cataclysm</b></gradient>"),
                        MiniMessage.miniMessage().deserialize("<#DBC094>ꜱᴇᴇᴋ ᴏᴠᴇʀᴡᴏʀʟᴅ'ꜱ ꜰᴏʀɢᴏᴛᴛᴇɴ ʟᴏʀᴅ"),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4), Duration.ofSeconds(2))
                ));
            }
        });
        dispatcher.addDelay(7000);
        dispatcher.schedule(() -> PantheonWarper.warp(PantheonLevels.TWISTED_CITY));
        dispatcher.addDelay(7000);
        dispatcher.schedule(this::startTwistedWardenFight);
    }
}
