package org.cataclysm.game.player.systems.cooldown;

import org.bukkit.Material;
import org.cataclysm.game.player.data.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record CooldownManager(PlayerData data) {

    public void restore() {
        if (this.data.getCooldowns() == null) return;
        for (var entry : this.data.getCooldowns().entrySet()) {
            var material = Material.valueOf(entry.getKey());
            new PlayerCooldown(this.data.getHolder(), entry.getValue(), material);
        }
    }

    public @NotNull List<PlayerCooldown> getCooldowns() {
        List<PlayerCooldown> list = new ArrayList<>();

        if (this.data.getCooldowns() == null) return list;

        for (var entry : this.data.getCooldowns().entrySet()) {
            list.add(new PlayerCooldown(entry.getValue(), Material.valueOf(entry.getKey())));
        }
        return list;
    }

}
