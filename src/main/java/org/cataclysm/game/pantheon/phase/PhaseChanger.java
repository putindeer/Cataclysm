package org.cataclysm.game.pantheon.phase;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.PantheonTasks;
import org.cataclysm.game.pantheon.level.PantheonAreas;
import org.cataclysm.game.pantheon.utils.PantheonGlobalUtils;
import org.cataclysm.game.pantheon.utils.PantheonMessenger;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PhaseChanger {
    private @Getter PantheonPhase phase;
    private final PantheonOfCataclysm pantheon;

    private ScheduledFuture<?> entranceParticlesTask;

    public PhaseChanger(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    public void changePhase(@NotNull PantheonPhase phase) {
        this.castDefaults(this.phase, phase);
        this.phase = phase;
        switch (phase) {
            case WAITING -> this.castWaiting();
            case WARDEN_FIGHT -> this.castWardenPhase();
        }
    }

    public void castWardenPhase() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonGlobalUtils.teleport(player, PantheonAreas.WARDEN_ARENA.getCoreLocation());
        }
    }

    public void castWaiting() {
        this.runEntranceParticles();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonMessenger.sendPantheonMessage(player, "El Panteón de Cataclysm ha abierto sus puertas.");
        }
    }

    public void castEntranceTransition() {
        World world = pantheon.getWorld();

        this.castDefaults(PantheonPhase.WAITING, PantheonPhase.WARDEN_FIGHT);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.stopAllSounds();

            world.playSound(player, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 12.0F, 0.5F);
            world.playSound(player, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 12.0F, 0.65F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0, false, false, false));
        }

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () ->
                this.changePhase(PantheonPhase.WARDEN_FIGHT), 120L);
    }

    public void castDefaults(PantheonPhase previousPhase, PantheonPhase phase) {
        if (entranceParticlesTask != null) {
            entranceParticlesTask.cancel(true);
            entranceParticlesTask = null;
        }

        if (previousPhase == PantheonPhase.WAITING) {
            for (Player player : Bukkit.getOnlinePlayers()) PantheonGlobalUtils.setReady(player, false);
        }
    }

    private void runEntranceParticles() {
        ScheduledExecutorService service = pantheon.getService();
        entranceParticlesTask = service.scheduleAtFixedRate(PantheonTasks::tickEntrance, 0, 350, TimeUnit.MILLISECONDS);
    }
}
