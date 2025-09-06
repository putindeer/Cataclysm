package org.cataclysm.game.events.pantheon.orchestrator;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.PantheonBosses;
import org.cataclysm.game.events.pantheon.orchestrator.fountain.PantheonFountain;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonTimer;
import org.cataclysm.game.events.pantheon.utils.PantheonWarper;

import java.time.Duration;

public class PantheonEvents {
    private final PantheonOrchestrator orchestrator;
    private final PantheonDispatcher dispatcher;
    private final PantheonOfCataclysm pantheon;

    public PantheonEvents(PantheonOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        this.dispatcher = orchestrator.getDispatcher();
        this.pantheon = orchestrator.getPantheon();
    }

    public void castFountain(PantheonLevels nextLevel) {
        PantheonWarper.warp(PantheonLevels.PANTHEON_FOUNTAIN);

        PantheonFountain fountain = new PantheonFountain(this.pantheon, PantheonLevels.PANTHEON_FOUNTAIN.getLocation());
        fountain.setStopTask(() -> this.orchestrator.startLevel(true, nextLevel));
        fountain.start();

        PantheonTimer timer = new PantheonTimer(this.pantheon, 420);
        timer.setDisplay("el panteón continuará en: ##:##");
        timer.start();
        timer.setStopTask(() ->
                this.orchestrator.startLevel(true, nextLevel));
    }

    public void castEventCountdown(boolean elapse) {
        PantheonTimer timer = new PantheonTimer(this.pantheon, 3600);

        timer.setDisplay("el evento iniciará en: ##:##");
        timer.start();

        if (elapse) timer.setStopTask(() ->
                this.orchestrator.startLevel(true, PantheonLevels.PANTHEON_ENTRANCE));
    }

    public void castPantheonReopenEvent(boolean elapse) {
        Location location = PantheonLevels.PANTHEON_ENTRANCE.getLocation();
        int coord = location.getBlockX();

        dispatcher.playSounds(
                Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.MASTER, 5F, 0.56F),
                Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.MASTER, 5F, 0.5F)
        );
        dispatcher.addEffects(
                new PotionEffect(ImmunityEffect.EFFECT_TYPE, 100, 0, false, false),
                new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false),
                new PotionEffect(PotionEffectType.SLOWNESS, 100, 9, false, false)
        );

        dispatcher.sendActionBar("las puertas del Abismo se reabren al fin.");
        dispatcher.sendActionBar("en las lejanías...");
        dispatcher.sendActionBar("en tierras yermas...");
        dispatcher.sendActionBar("en " + coord + ", " + coord + ".", 1);

        if (!elapse) return;

        dispatcher.addDelay(3000);
        dispatcher.schedule(() -> {
            PantheonFountain fountain = new PantheonFountain(this.pantheon, location);
            fountain.setStopTask(() -> this.orchestrator.startLevel(true, PantheonLevels.TWISTED_CITY));
            fountain.start();
        });
    }

    public void castStartEvent(boolean elapse) {
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
        dispatcher.addDelay(900);
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
        if (!elapse) return;
        dispatcher.addDelay(7000);
        dispatcher.schedule(() -> this.orchestrator.startBossFight(PantheonBosses.TWISTED_WARDEN));
    }
}
