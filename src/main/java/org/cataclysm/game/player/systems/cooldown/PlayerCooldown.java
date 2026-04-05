package org.cataclysm.game.player.systems.cooldown;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.data.PlayerData;
import org.jetbrains.annotations.NotNull;

public class PlayerCooldown {

    private final @Getter String id;
    private int taskId = -1;
    private @Setter @Getter int ticks;

    public PlayerCooldown(Player player, int ticks, Material material) {
        this(ticks, material);
        this.startTickTask(CataclysmPlayer.getCataclysmPlayer(player).getData(), 1); // 1 tick = 50ms
        player.setCooldown(material, ticks);
    }

    public PlayerCooldown(int ticks, @NotNull Material material) {
        this.ticks = ticks;
        this.id = material.name();
    }

    private void tick(@NotNull PlayerData data) {
        data.getCooldowns().put(this.id, this.ticks);

        if (this.ticks <= 0 || data.getHolder() == null) {
            this.stop(data);
            return;
        }

        this.ticks--;
    }

    public void stop(@NotNull PlayerData data) {
        var holder = data.getHolder();
        if (holder != null) {
            holder.setCooldown(Material.valueOf(this.id), 0);
        }
        data.getCooldowns().remove(this.id);
        this.stopTickTask();
    }

    /**
     * @param periodTicks Number of Minecraft ticks (20 = 1 second) between updates.
     */
    public void startTickTask(PlayerData data, long periodTicks) {
        if (taskId != -1) return; // already running
        this.taskId = Bukkit.getScheduler().runTaskTimer(
                Cataclysm.getInstance(),
                () -> this.tick(data),
                0L,
                periodTicks
        ).getTaskId();
    }

    public void stopTickTask() {
        if (this.taskId != -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
            this.taskId = -1;
        }
    }

    public boolean isActive() {
        return this.ticks > 0;
    }
}
