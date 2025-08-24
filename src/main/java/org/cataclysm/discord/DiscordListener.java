package org.cataclysm.discord;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.player.survival.resurrect.totems.events.PlayerUseTotemEvent;

//This listener is registered in the main class if Cataclysm.isMainHost() is true.
public class DiscordListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (!Cataclysm.isMainHost()) return;

        var player = event.getPlayer();
        var message = event.deathMessage();
        var location = player.getLocation();

        DiscordMessenger.sendDeathMessage(player, message, location);
    }

    @EventHandler
    private void onAsyncChat(AsyncChatEvent event) {
        if (!Cataclysm.isMainHost()) return;

        var sender = event.getPlayer();
        var message = event.message();

        DiscordMessenger.sendChatMessage(sender, message);
    }

    @EventHandler
    private void onPlayerUseTotem(PlayerUseTotemEvent event) {
        if (!Cataclysm.isMainHost()) return;

        var player = event.getPlayer();
        var cause = event.getCause();
        var number = event.getNumber();
        var mortality = event.getMortality();
        var location = player.getLocation();

        var totemId = event.getTotemId();
        if (totemId == null) totemId = "totem_of_undying";

        DiscordMessenger.sendTotemMessage(player, cause, totemId, number, mortality, location);
    }

}
