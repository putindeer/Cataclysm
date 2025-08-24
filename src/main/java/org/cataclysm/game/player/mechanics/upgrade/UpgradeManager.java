package org.cataclysm.game.player.mechanics.upgrade;

import org.bukkit.entity.Player;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.data.PlayerData;
import org.cataclysm.game.player.mechanics.upgrade.event.PlayerUpgradeLemegetonEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record UpgradeManager(PlayerData playerData) {

    //This can be null
    public UpgradeManager(Player player) {
        this(CataclysmPlayer.getCataclysmPlayer(player).getData());
    }

    public int getUpgrades() {
        return this.playerData.getUpgrades().values().stream()
                .filter(level -> level > 0)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public void addUpgradeLevel(@NotNull Upgrades upgrade) {
        var newLevel = this.getUpgradeLevel(upgrade) + 1;
        this.setUpgradeLevel(upgrade, newLevel);
        new PlayerUpgradeLemegetonEvent(this.playerData.getHolder(), upgrade, newLevel, this.getUpgrades()).callEvent();
    }

    public void setUpgradeLevel(@NotNull Upgrades upgrade, int level) {
        this.playerData.getUpgrades().put(upgrade.name(), level);
    }

    public int getUpgradeLevel(@NotNull Upgrades upgrade) {
        return this.playerData.getUpgrades().get(upgrade.name());
    }

    public @NotNull HashMap<String, Integer> getActiveUpgrades() {
        HashMap<String, Integer> filtered = new HashMap<>();

        for (Map.Entry<String, Integer> entry : this.playerData.getUpgrades().entrySet()) {
            var value = entry.getValue();
            if (value != null && value > 0) filtered.put(entry.getKey(), value);
        }

        return filtered;
    }
}
