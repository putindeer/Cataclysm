package org.cataclysm.game.world.ragnarok;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.cataclysm.Cataclysm;
import org.cataclysm.global.utils.math.MathUtils;
import org.jetbrains.annotations.NotNull;

public class RagnarokUtils {

    public static void setUpWorld(boolean ragnarok) {
        World world = Bukkit.getWorld("world");
        if (world == null) return;
        world.setStorm(ragnarok);

        if (Cataclysm.getDay() >= 14) {
            world.setTime(18000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        } else {
            if (ragnarok) {
                world.setTime(18000);
            } else world.setTime(0);

            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !ragnarok);
        }
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, !ragnarok);
    }

    public static @NotNull Component getFormattedTime(@NotNull RagnarokData ragnarokData) {
        return MiniMessage.miniMessage().deserialize(MathUtils.formatSeconds(ragnarokData.getTimeLeft()));
    }
}