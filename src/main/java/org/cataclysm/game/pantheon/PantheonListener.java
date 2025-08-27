package org.cataclysm.game.pantheon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.pantheon.entrance.EntranceGUI;

@Registrable
public class PantheonListener implements Listener {

    @EventHandler
    private void onEntranceInteract(PlayerInteractAtEntityEvent event) {
        var data = PersistentData.get(event.getRightClicked(), "CUSTOM", PersistentDataType.STRING);
        if (data == null || !data.equals("void_essence")) return;

        new EntranceGUI(event.getPlayer()).open();
    }

}
