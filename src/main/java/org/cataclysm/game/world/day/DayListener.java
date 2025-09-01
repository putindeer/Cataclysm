package org.cataclysm.game.world.day;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.game.world.day.events.ChangeDayEvent;
import org.cataclysm.global.utils.chat.ChatMessenger;

@Registrable
public class DayListener implements Listener {

    @EventHandler
    private void onChangeDay(ChangeDayEvent event) {
        int day = event.getDay();
        for (Dimensions dimension : Dimensions.values()) {
            boolean firstWeekPassed = day >= 7;
            dimension.getWorld().setGameRule(GameRule.DO_FIRE_TICK, !firstWeekPassed);
        }

        for (var player : Bukkit.getOnlinePlayers()) {
            ChatMessenger.sendMessage(player, "El día " + ChatMessenger.getCataclysmColor() + day + ChatMessenger.getTextColor() + " ha iniciado.");
            player.playSound(player, "entity.zombie_villager.converted", 0.6F, 1);
            player.playSound(player, "block.beacon.power_select", 0.6F, 1);
            Cataclysm.updateEvents();
        }

        World overworld = Dimensions.OVERWORLD.getWorld();
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, day < 14);
        if (day >= 14) overworld.setTime(18000);
    }

}
