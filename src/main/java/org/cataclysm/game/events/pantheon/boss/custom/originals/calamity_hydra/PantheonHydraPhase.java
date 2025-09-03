package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra;

import lombok.Getter;
import lombok.Setter;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PantheonHydraPhase {
    private int model;
    private int phase;
    private boolean elapsing;

    private final PantheonHydra hydra;

    public PantheonHydraPhase(PantheonHydra hydra) {
        this.hydra = hydra;
        this.elapsing = false;
    }

    public void tryElapse() {
        if (this.elapsing) return;
        if (this.hydra.health <= 0 && this.phase == 1) this.start(2);
        if (this.hydra.health <= getPhase3MinHealth() && this.phase == 2) this.start(3);
        if (this.phase == 3) {
            if (this.hydra.health <= 0) this.death();
            this.updateHeads();
        }
    }

    public void setPhase(int phase) {
        double amplifier = (this.phase * 0.3);
        double initScale = 5.0;
        double scale = initScale + (initScale * amplifier);

        this.hydra.setAttribute(Attribute.SCALE, scale);
        this.phase = phase;
    }

    public void start(int phase) {
        switch (phase) {
            case 1 -> this.firstPhase();
            case 2 -> this.secondPhase();
            case 3 -> this.thirdPhase();
        }
    }

    public void stop() {
        this.removeDisguise(this.hydra.getController());
        this.removeSleepingHydra();
    }

    private void updateHeads() {
        if (this.hydra.rageManager.isOvercharged()) return;

        double headHealth = this.getHeadHealth();
        int ordinal = 0;
        for (int i = 0; i < 5; i++) {
            double healthVerification = this.hydra.maxHealth - (headHealth * i);
            if (this.hydra.health <= healthVerification) ordinal = 6 + i;
        }
        if (this.model == ordinal) return;

        Title title = Title.title(
                MiniMessage.miniMessage().deserialize(""),
                MiniMessage.miniMessage().deserialize("<#ab8559>¡Cabeza destruida!"),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2))
        );
        for (Player player : Bukkit.getOnlinePlayers()) player.showTitle(title);

        this.updateModel(ordinal);
    }

    private void death() {
        this.elapsing = true;
        this.phase = 0;
        this.updateModel(this.model);

        this.hydra.getSoundtrack().stopAll();
    }

    private void thirdPhase() {
        Bukkit.getConsoleSender().sendMessage("[Calamity Hydra] Starting third phase...");

        ScheduledExecutorService service = this.hydra.getThread().getService();

        this.hydra.heads = 2;
        this.elapsing = true;

        this.updateModel(4);
        this.hydra.getSoundtrack().stopAll();

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                this.setPhase(3);
                this.updateModel(6);

                this.hydra.heads = 5;
                this.hydra.health = this.hydra.maxHealth;
                this.elapsing = false;

                this.hydra.getSoundtrack().loop("PHASE_3", 383);
            });
        }, 4, TimeUnit.SECONDS);
    }

    private void secondPhase() {
        Bukkit.getConsoleSender().sendMessage("[Calamity Hydra] Starting second phase...");

        ScheduledExecutorService service = this.hydra.getThread().getService();

        this.hydra.heads = 0;
        this.elapsing = true;

        this.updateModel(2);
        this.hydra.getSoundtrack().stopAll();

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                this.hydra.getArena().getPlayersInArena().forEach(player -> {
                    player.showTitle(this.getTitle(2));
                    player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1.57F, 0.65F);
                });
            });
        }, 3, TimeUnit.SECONDS);

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                this.setPhase(2);
                this.updateModel(3);

                this.hydra.heads = 3;
                this.hydra.health = this.hydra.maxHealth;
                this.elapsing = false;

                this.hydra.getSoundtrack().loop("PHASE_2", 316);
            });
        }, 4, TimeUnit.SECONDS);
    }

    private void firstPhase() {
        Bukkit.getConsoleSender().sendMessage("[Calamity Hydra] Starting first phase...");

        ScheduledExecutorService service = this.hydra.getThread().getService();

        this.hydra.heads = 1;
        this.setPhase(1);
        this.updateModel(1);

        this.removeSleepingHydra();
        this.hydra.getSoundtrack().loop("PHASE_1", 251);

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                this.hydra.getArena().getPlayersInArena().forEach(player -> {
                    player.showTitle(this.getTitle(1));
                    player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1.57F, 0.65F);
                });
            });
        }, 3, TimeUnit.SECONDS);
    }

    public void updateModel(int ordinal) {
        this.model = ordinal;

        Location location = this.hydra.getArena().center();
        String name = this.getName(ordinal);

        this.removeDisguise(this.hydra.getController());
        DisguiseConfig.setPlayerNameType(DisguiseConfig.PlayerNameType.VANILLA);

        Entity ravager = location.getWorld().spawnEntity(location, EntityType.RAVAGER, CreatureSpawnEvent.SpawnReason.CUSTOM);
        ravager.customName(MiniMessage.miniMessage().deserialize(name));
        ravager.setCustomNameVisible(false);

        DisguiseAPI.disguiseToAll(this.hydra.getController(), DisguiseAPI.constructDisguise(ravager));
        DisguiseAPI.setActionBarShown(this.hydra.getController(), false);
        DisguiseAPI.getDisguise(this.hydra.getController()).getInternals().setSelfDisguiseTallScaleMax(2.5f);

        ravager.remove();
    }

    private void removeSleepingHydra() {
        this.hydra.getArena().getLivingEntitiesInArena().forEach(livingEntity -> {
            if (!(livingEntity instanceof ArmorStand armorStand)) return;

            Boolean disguised = PersistentData.get(armorStand, "DISGUISE", PersistentDataType.BOOLEAN);
            if (disguised != null && disguised) armorStand.remove();
        });
    }

    private double getHeadHealth() {
        return ((double) this.hydra.maxHealth / this.hydra.heads);
    }

    private int getPhase3MinHealth() {
        return (int) (this.hydra.maxHealth - getHeadHealth());
    }

    private @NotNull Title getTitle(int ordinal) {
        String display = "";
        switch (ordinal) {
            case 1 -> display = "<gradient:" + CataclysmColor.CALAMITY.getColor() + ":" + CataclysmColor.CALAMITY.getColor2() + ">" + TinyCaps.tinyCaps("Calamity Dragon") + "</gradient>";
            case 2 -> display = "<gradient:" + CataclysmColor.CALAMITY.getColor() + ":" + CataclysmColor.CALAMITY.getColor2() + ">" + TinyCaps.tinyCaps("Calamity Hydra") + "</gradient>";
        }
        return Title.title(
                MiniMessage.miniMessage().deserialize("<#ab8559><obf>||<reset> " + display + " <#ab8559><obf>||"),
                MiniMessage.miniMessage().deserialize("<" + CataclysmColor.CALAMITY.getColor3() + ">Mighty god of calamities"),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2))
        );
    }

    private @NotNull String getName(int ordinal) {
        var name = "ch";
        return switch (ordinal) {
            case 1 -> name + "1head";
            case 2 -> name + "1-nohead";
            case 3 -> name + "3head";
            case 4 -> name + "2head";
            case 5 -> name + "5head-overraged";
            case 6 -> name + "5head";
            case 7 -> name + "5head-1nohead";
            case 8 -> name + "5head-2nohead";
            case 9 -> name + "5head-3nohead";
            case 10 -> name + "5head-4nohead";
            case 11 -> name + "5head-5nohead";
            default -> throw new IllegalStateException("Unexpected value: " + ordinal);
        };
    }

    private void removeDisguise(Player controller) {
        if (DisguiseAPI.getDisguise(controller) != null) DisguiseAPI.getDisguise(controller).removeDisguise();
    }
}
