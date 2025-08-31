package org.cataclysm.game.events.limited;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;

@Registrable
public class EventListener implements Listener {

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        var cataclysmEvent = Cataclysm.getEventManager();
        if (cataclysmEvent == null) return;
        cataclysmEvent.barManager.bossBar.addViewer(event.getPlayer());
    }

}
