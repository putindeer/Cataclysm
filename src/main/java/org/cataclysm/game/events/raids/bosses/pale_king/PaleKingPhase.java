package org.cataclysm.game.events.raids.bosses.pale_king;

import lombok.Getter;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PaleKingPhase {
    public final PaleKing king;
    public @Getter int current;
    public int currentModel;
    public boolean elapsing;
    public ScheduledFuture<?> future;

    public PaleKingPhase(PaleKing king) {
        this.king = king;
        this.elapsing = false;
    }

    public void tryElapse() {
        if (this.elapsing) return;
        if (this.king.health <= 100 && this.current == 1) this.start(2);
    }

    public void start(int phase) {
        switch (phase) {
            case 1 -> this.firstPhase();
            case 2 -> this.secondPhase();
        }
    }

    private void firstPhase() {
        ScheduledExecutorService service = this.king.getThread().getService();

        this.setCurrent(1);
        this.updateModel(this.king.getController(), "Pale King");
        this.king.getSoundtrack().loop("THEME", 170);

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                this.king.getArena().getPlayersInArena().forEach(player -> {
                    player.showTitle(this.getTitle(1));
                    player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1.57F, 0.65F);
                    this.elapsing = false;
                });
            });
        }, 3, TimeUnit.SECONDS);
    }

    private void secondPhase() {
        ScheduledExecutorService service = this.king.getThread().getService();

        Player controller = this.king.getController();
        controller.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false));

        this.setCurrent(2);
        this.king.resetAttribute(Attribute.SCALE);
        this.king.health = 0;
        this.king.updateBar();
        this.updateModel(this.king.getController(), "Void Lord");

        for (var player : Bukkit.getOnlinePlayers()) {
            var title = Title.title(MiniMessage.miniMessage().deserialize("<#a18d60>¡Incursión Finalizada!"), MiniMessage.miniMessage().deserialize("<#b0a897>Pale King derrotado"), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2)));
            player.showTitle(title);

            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 0.9F);
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1.50F);
        }

        int delay1 = 16;
        int delay2 = 22;
        int delay3 = 38;
        int delay4 = 43;

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> this.king.getSoundtrack().loop("VOID_LORD", 346), delay1 * 20);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            int interval = 7;
            this.future = service.scheduleAtFixedRate(() -> {
                this.king.health += 10;
                this.king.updateBar();

                if (this.king.health >= this.king.maxHealth) {
                    this.future.cancel(true);
                    this.future = null;
                }
            }, 0, interval, TimeUnit.MILLISECONDS);
        }, (delay1 + delay2) * 20);

        service.schedule(() -> {
            this.king.display = "<" + CataclysmColor.PALE.getColor() +"> ❖ <" + CataclysmColor.PALE.getColor2() + "><obf>||<reset> <gradient:" + CataclysmColor.PALE.getColor() + ":#ffffff:" + CataclysmColor.PALE.getColor() + "> " +
                    "<obf>" + TinyCaps.tinyCaps(this.king.getName()) +
                    "</gradient> <" + CataclysmColor.PALE.getColor2() + "> <obf>||<reset> <" + CataclysmColor.PALE.getColor() + ">❖";

            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0, true, true));
                }
            });

            this.king.getBossBar().name(MiniMessage.miniMessage().deserialize(this.king.display));
            this.king.updateBar();
        }, delay3 + delay1, TimeUnit.SECONDS);

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                this.king.setAttribute(Attribute.SCALE, 2);
                this.king.getArena().getPlayersInArena().forEach(player -> {
                    player.showTitle(this.getTitle(2));
                    this.elapsing = false;
                    controller.removePotionEffect(PotionEffectType.INVISIBILITY);

                    String display = "<" + CataclysmColor.VOID.getColor() +"> ❖ <" + CataclysmColor.VOID.getColor2() + "><obf>||<reset> <gradient:" + CataclysmColor.VOID.getColor() + ":" + CataclysmColor.VOID.getColor2() + "> " +
                            TinyCaps.tinyCaps("Void Lord") +
                            "</gradient> <" + CataclysmColor.VOID.getColor2() + "> <obf>||<reset> <" + CataclysmColor.VOID.getColor() + ">❖";

                    this.king.getBossBar().name(MiniMessage.miniMessage().deserialize(display));
                    this.king.getBossBar().color(BossBar.Color.PURPLE);
                });
            });
        }, (delay4 + delay1), TimeUnit.SECONDS);
    }

    public void setCurrent(int phase) {
        this.elapsing = true;
        this.current = phase;
        this.king.getSoundtrack().stopAll();
    }

    public void updateModel(Player player, String model) {
        Location location = player.getLocation();

        if (DisguiseAPI.getDisguise(player) != null) DisguiseAPI.getDisguise(player).removeDisguise();
        DisguiseConfig.setPlayerNameType(DisguiseConfig.PlayerNameType.VANILLA);

        Entity ravager = location.getWorld().spawnEntity(location, EntityType.CREEPER, CreatureSpawnEvent.SpawnReason.CUSTOM);
        ravager.customName(MiniMessage.miniMessage().deserialize(model));
        ravager.setCustomNameVisible(false);

        DisguiseAPI.disguiseToAll(player, DisguiseAPI.constructDisguise(ravager));
        DisguiseAPI.setActionBarShown(player, false);
        DisguiseAPI.getDisguise(player).getInternals().setSelfDisguiseTallScaleMax(0.01);

        ravager.remove();
    }

    private @NotNull Title getTitle(int ordinal) {
        String display = "";
        switch (ordinal) {
            case 1 -> display = "<gradient:" + CataclysmColor.PALE.getColor() + ":" + CataclysmColor.PALE.getColor2() + ">" + TinyCaps.tinyCaps("Pale King") + "</gradient>";
            case 2 -> display = "<gradient:" + CataclysmColor.VOID.getColor() + ":" + CataclysmColor.VOID.getColor2() + ">" + TinyCaps.tinyCaps("Void Lord") + "</gradient>";
        }
        return Title.title(
                MiniMessage.miniMessage().deserialize("<#FFFFFF><obf>||<reset> " + display + " <#FFFFFF><obf>||"),
                MiniMessage.miniMessage().deserialize("<#FFFFFF>Mighty god of nothingness"),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2))
        );
    }
}
