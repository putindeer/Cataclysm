package org.cataclysm.game.pantheon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.PantheonUtils;
import org.cataclysm.game.pantheon.level.entrance.EntranceGUI;
import org.jetbrains.annotations.NotNull;

public class PantheonPlayerListener implements Listener {

    @EventHandler
    private void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        PantheonUtils.setReady(event.getPlayer(), false);
    }

    @EventHandler
    private void onEntranceInteract(PlayerInteractAtEntityEvent event) {
        if (!isEntityEntrance(event)) return;
        new EntranceGUI(event.getPlayer()).open();
    }

    private boolean isEntityEntrance(@NotNull PlayerInteractAtEntityEvent event) {
        String data = PersistentData.get(event.getRightClicked(), "CUSTOM", PersistentDataType.STRING);
        return Cataclysm.getPantheon().getPhase() == PantheonOfCataclysm.Phase.WAITING_FOR_PLAYERS
                && data != null
                && data.equals("pantheon_entrance");
    }
}
