package org.cataclysm.discord;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.player.survival.resurrect.totems.events.PlayerUseTotemEvent;

import java.util.Objects;

//This listener is registered in the main class if Cataclysm.isMainHost() is true.
public class DiscordListener implements Listener {
    private final boolean broadcastDeath = Cataclysm.getInstance().getConfig().getBoolean("broadcast-death-discord");
    private final boolean broadcastChat = Cataclysm.getInstance().getConfig().getBoolean("broadcast-chat-discord");
    private final boolean broadcastTotem = Cataclysm.getInstance().getConfig().getBoolean("broadcast-totem-discord");

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (!broadcastDeath) return;
        if (Cataclysm.discordIsNotEnabled) return;

        var player = event.getPlayer();
        var message = event.deathMessage();
        var location = player.getLocation();

        DiscordMessenger.sendDeathMessage(player, message, location);
    }

    @EventHandler
    private void onAsyncChat(AsyncChatEvent event) {
        if (!broadcastChat) return;
        if (Cataclysm.discordIsNotEnabled) return;

        var sender = event.getPlayer();
        var message = event.message();

        DiscordMessenger.sendChatMessage(sender, message);
    }

    @EventHandler
    private void onPlayerUseTotem(PlayerUseTotemEvent event) {
        if (!broadcastTotem) return;
        if (Cataclysm.discordIsNotEnabled) return;

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
