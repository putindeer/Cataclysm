package org.cataclysm.server.tablist;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TablistUtils {

    public static void setScoreboardTeam(@NotNull Player player, String teamName) {
        var scoreboard = player.getScoreboard();
        var minecraftTeam = scoreboard.getTeam(teamName);

        if (minecraftTeam == null) minecraftTeam = scoreboard.registerNewTeam(teamName);
        minecraftTeam.addPlayer(player);
    }

}
