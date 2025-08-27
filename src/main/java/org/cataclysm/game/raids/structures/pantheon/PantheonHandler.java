package org.cataclysm.game.raids.structures.pantheon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.block.arcane.table.ArcaneTableMenu;
import org.cataclysm.game.raids.structures.pantheon.entrance.PantheonGUI;

@Registrable
public class PantheonHandler implements Listener {

    @EventHandler
    private void onEntranceInteract(PlayerInteractAtEntityEvent event) {
        var data = PersistentData.get(event.getRightClicked(), "CUSTOM", PersistentDataType.STRING);
        if (data == null || !data.equals("void_essence")) return;

        new PantheonGUI(event.getPlayer()).open();
    }

}
