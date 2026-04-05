package org.cataclysm.game.events.pantheon.orchestrator;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.events.pantheon.PantheonBosses;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.orchestrator.fountain.PantheonFountain;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonTimer;
import org.cataclysm.game.events.pantheon.utils.PantheonWarper;

import java.time.Duration;
import java.util.List;

public class PantheonEvents {
    private static final int FOUNTAIN_TIMER = 420;
    private static final int EVENT_TIMER = 3600;
    private static final int EFFECT_DURATION = 100;

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

        var timer = new PantheonTimer(pantheon, FOUNTAIN_TIMER);
        timer.setDisplay("el panteón continuará en: ##:##");
        timer.start();
        timer.setStopTask(() -> orchestrator.startLevel(true, nextLevel));
    }

    public void castEventCountdown(boolean elapse) {
        var timer = new PantheonTimer(pantheon, EVENT_TIMER);
        timer.setDisplay("el evento iniciará en: ##:##");
        timer.start();

        if (elapse) timer.setStopTask(() ->
                orchestrator.startLevel(true, PantheonLevels.PANTHEON_ENTRANCE));
    }

    public void castPantheonReopenEvent(boolean elapse) {
        var location = PantheonLevels.PANTHEON_ENTRANCE.getLocation();
        int coord = location.getBlockX();

        dispatcher.playSounds(
                Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.MASTER, 5F, 0.56F),
                Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.MASTER, 5F, 0.5F)
        );
        dispatcher.addEffects(
                new PotionEffect(ImmunityEffect.EFFECT_TYPE, EFFECT_DURATION, 0, false, false),
                new PotionEffect(PotionEffectType.BLINDNESS, EFFECT_DURATION, 0, false, false),
                new PotionEffect(PotionEffectType.SLOWNESS, EFFECT_DURATION, 9, false, false)
        );

        sendActionBars(
                "las puertas del Abismo se reabren al fin.",
                "en las lejanías...",
                "en tierras yermas...",
                "en " + coord + ", " + coord + "."
        );

        if (!elapse) return;

        dispatcher.addDelay(3000);
        dispatcher.schedule(() -> {
            var fountain = new PantheonFountain(pantheon, location);
            fountain.setStopTask(() -> orchestrator.startLevel(true, PantheonLevels.TWISTED_CITY));
            fountain.start();
        });
    }

    public void castStartEvent(boolean elapse) {
        dispatcher.addEffects(new PotionEffect(PotionEffectType.BLINDNESS, EFFECT_DURATION, 0, false, false));
        dispatcher.playSounds(
                Sound.sound(Key.key("entity.elder_guardian.ambient"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("entity.elder_guardian.death"), SoundCategory.MASTER, 2F, 0.55F)
        );

        dispatcher.addDelay(2000);
        sendActionBars(
                "Del horizonte asoma el crepúsculo.",
                "Aunque parece el mismo de siempre...",
                "hoy se siente diferente.",
                "Hoy, la mayoría no verá la luz del mediodía."
        );

        dispatcher.addDelay(2000);
        sendActionBars(List.of(
                "En esta tumba reposan cinco dioses.",
                "Sus nombres se desvanecen...",
                "mas sus leyendas perduran."
        ), 12);

        dispatcher.addDelay(1500);
        sendActionBars("Mortales...");

        dispatcher.addDelay(900);
        sendActionBars(
                "Venced al retorcido.",
                "Enfrentaos a la calamidad.",
                "Derrocad al pálido...",
                "Abrazad el vacío..."
        );

        dispatcher.addDelay(1000);
        sendActionBars("...y poned fin al Cataclismo.", 5);

        dispatcher.addDelay(1000);
        dispatcher.playSounds(
                Sound.sound(Key.key("item.trident.thunder"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("item.trident.thunder"), SoundCategory.MASTER, 2F, 0.65F),
                Sound.sound(Key.key("entity.elder_guardian.death"), SoundCategory.MASTER, 2F, 0.55F),
                Sound.sound(Key.key("cataclysm.pantheon.teleport"), SoundCategory.MASTER, 5F, 0.5F),
                Sound.sound(Key.key("cataclysm.pantheon.teleport"), SoundCategory.MASTER, 5F, 0.65F),
                Sound.sound(Key.key("music_disc.stal"), SoundCategory.MASTER, 2F, 0.65F)
        );

        dispatcher.schedule(() -> Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(
                Title.title(
                        MiniMessage.miniMessage().deserialize("<gradient:#D3AF37:#E1B768><b>Pantheon Of Cataclysm</b></gradient>"),
                        MiniMessage.miniMessage().deserialize("<#DBC094>ꜱᴇᴇᴋ ᴏᴠᴇʀᴡᴏʀʟᴅ'ꜱ ꜰᴏʀɢᴏᴛᴛᴇɴ ʟᴏʀᴅ"),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4), Duration.ofSeconds(2))
                )
        )));

        if (!elapse) return;

        dispatcher.addDelay(7000);
        dispatcher.schedule(() -> orchestrator.startBossFight(PantheonBosses.TWISTED_WARDEN));
    }

    // --- helpers ---
    private void sendActionBars(String message, int duration) {
        dispatcher.sendActionBar(message, duration);
    }

    private void sendActionBars(String... messages) {
        for (var msg : messages) dispatcher.sendActionBar(msg);
    }

    private void sendActionBars(List<String> messages, int duration) {
        for (var msg : messages) dispatcher.sendActionBar(msg, duration);
    }
}