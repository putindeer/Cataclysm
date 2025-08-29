package org.cataclysm.game.pantheon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.pantheon.handlers.PlayerHandler;
import org.cataclysm.game.pantheon.level.entrance.EntranceGUI;
import org.cataclysm.game.pantheon.phase.PantheonPhase;
import org.jetbrains.annotations.NotNull;

@Registrable
public class PantheonPlayerListener implements Listener {

    @EventHandler
    private void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        if (Cataclysm.getPantheon() == null) return;
        PlayerHandler.setReady(event.getPlayer(), false);
    }

    @EventHandler
    private void onEntranceInteract(PlayerInteractAtEntityEvent event) {
        if (Cataclysm.getPantheon() == null) return;

        if (!isEntityEntrance(event)) return;
        new EntranceGUI(event.getPlayer()).open();
    }

    private boolean isEntityEntrance(@NotNull PlayerInteractAtEntityEvent event) {
        String data = PersistentData.get(event.getRightClicked(), "CUSTOM", PersistentDataType.STRING);
        return Cataclysm.getPantheon().getPhase() == PantheonPhase.WAITING
                && data != null
                && data.equals("pantheon_entrance");
    }

}
