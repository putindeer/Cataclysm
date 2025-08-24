package org.cataclysm.game.block.arcane.table;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.listener.registrable.Registrable;

@Registrable
public class ArcaneTableListener implements Listener {

    @EventHandler
    private void onPlayerAtInteract(PlayerInteractAtEntityEvent event) {
        var data = PersistentData.get(event.getRightClicked(), "CUSTOM", PersistentDataType.STRING);
        if (data == null || !data.equals("arcane_table")) return;

        new ArcaneTableMenu(event.getPlayer()).open();
    }

}
