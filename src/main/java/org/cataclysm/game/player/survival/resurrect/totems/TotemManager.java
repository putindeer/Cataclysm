package org.cataclysm.game.player.survival.resurrect.totems;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.cataclysm.game.player.data.PlayerData;

public record TotemManager(PlayerData playerData) {
    public void setPoppedTotems(int totems) {
        this.playerData.setPoppedTotems(totems);
    }

    public int getPoppedTotems() {
        return this.playerData.getPoppedTotems();
    }

    public void addPoppedTotem() {
        this.setPoppedTotems(this.getPoppedTotems() + 1);
    }

    public void updateStatistic() {
        Player player = this.playerData.getHolder();

        if (player == null) throw new NullPointerException(this.playerData.getNickname() + " is not online.");

        int poppedTotems = this.getPoppedTotems();

        if (player.getStatistic(Statistic.USE_ITEM, Material.TOTEM_OF_UNDYING) == poppedTotems) return;

        player.setStatistic(Statistic.USE_ITEM, Material.TOTEM_OF_UNDYING, poppedTotems);
    }
}