package org.cataclysm.game.pantheon.phase;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
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
import org.cataclysm.game.pantheon.utils.PantheonPlayerUtils;
import org.cataclysm.game.pantheon.utils.PantheonSender;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
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

    public void tryElapseWaitroom() {
        int ready = PantheonPlayerUtils.getReadyCount();
        int size = PantheonPlayerUtils.getParticipants().size();

        if (ready >= size) {
            pantheon.getPhaseChanger().castEntranceTransition();
        }
    }

    public void changePhase(@NotNull PantheonPhase phase) {
        this.castDefaults(this.phase, phase);
        this.phase = phase;
        switch (phase) {
            case WAITING -> this.castWaiting();
            case WARDEN_FIGHT -> this.castWardenFight();
        }
    }

    public void castWardenFight() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonPlayerUtils.teleport(player, PantheonAreas.WARDEN_ARENA.getCoreLocation());
        }
    }

    public void castWaiting() {
        this.runEntranceParticles();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonSender.sendPantheonMessage(player, "El Panteón de Cataclysm ha abierto sus puertas.");
        }
    }

    public void castDefaults(PantheonPhase previousPhase, PantheonPhase phase) {
        if (entranceParticlesTask != null) {
            entranceParticlesTask.cancel(true);
            entranceParticlesTask = null;
        }
        if (previousPhase == PantheonPhase.WAITING) {
            for (Player player : Bukkit.getOnlinePlayers()) PantheonPlayerUtils.setReady(player, false);
        }
    }

    public void castEntranceTransition() {
        World world = PantheonAreas.PANTHEON_ENTRANCE.getCoreLocation().getWorld();

        this.castDefaults(PantheonPhase.WAITING, PantheonPhase.WARDEN_FIGHT);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.stopAllSounds();
            world.playSound(player, Sound.BLOCK_BEACON_DEACTIVATE, 12.0F, 0.5F);
            world.playSound(player, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 12.0F, 0.65F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0, false, false, false));
        }

        sendActionMessage("Del horizonte asoma el crepúsculo.", 2000);
        sendActionMessage("Aunque parece el mismo de siempre...", 1000);
        sendActionMessage("hoy se siente diferente.", 1000);
        sendActionMessage("Hoy, la mayoría no verá la luz del mediodía.", 1500);
        sendActionMessage("En esta tumba reposan cinco dioses.", 2500);
        sendActionMessage("Sus nombres se desvanecen...", 700);
        sendActionMessage("pero sus leyendas perduran.", 900);
        sendActionMessage("Venced al retorcido.", 1000);
        sendActionMessage("Haced frente a la calamidad.", 900);
        sendActionMessage("Derrocad al pálido...", 800);
        sendActionMessage("abrazad el vacío...", 700);
        sendActionMessage("y poned fin al cataclismo.", 0, 2500);

        int elapseDelay = (delay/50);
        delay = 0;

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(
                        MiniMessage.miniMessage().deserialize("<gradient:#FC8C03:#B5A16B>El Panteón de Cataclysm</gradient>"),
                        MiniMessage.miniMessage().deserialize("<gradient:#FC8C03:#B5A16B>se abre ante vosotros</gradient>"),
                        Title.Times.times(
                                Duration.ofMillis(500),
                                Duration.ofMillis(3500),
                                Duration.ofMillis(500)
                        )
                ));
                world.playSound(player, Sound.MUSIC_DISC_STAL, 12.0F, 0.5F);
                world.playSound(player, Sound.ITEM_TRIDENT_THUNDER, 12.0F, 0.65F);
                world.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 12.0F, 0.5F);
                world.playSound(player, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 12.0F, 0.65F);
            }
        }, elapseDelay + 60);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () ->
                this.changePhase(PantheonPhase.WARDEN_FIGHT), elapseDelay + 180);
    }

    private int delay = 0;
    private void sendActionMessage(String message, int delayMs) {
        sendActionMessage(message, delayMs, 1000);
    }
    private void sendActionMessage(String message, int delayMs, int interval) {
        delay += delayMs + interval;
        pantheon.getService().schedule(() ->
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        PantheonSender.sendAnimatedActionBar(player, message, interval);
                    });
                }), delay, TimeUnit.MILLISECONDS);
    }

    private void runEntranceParticles() {
        ScheduledExecutorService service = pantheon.getService();
        entranceParticlesTask = service.scheduleAtFixedRate(PantheonTasks::tickEntrance, 0, 350, TimeUnit.MILLISECONDS);
    }
}
