package org.cataclysm.game.events.pantheon.orchestrator.fountain.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;
import org.cataclysm.game.events.pantheon.orchestrator.fountain.gui.FountainGUI;
import org.cataclysm.game.events.pantheon.orchestrator.fountain.gui.InteractableMob;

@Registrable
public class PreparationHandler implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        PantheonProfile profile = PantheonProfile.fromPlayer(pantheon, event.getPlayer());
        profile.setReady(false);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        if (InteractableMob.isEntrance(event.getRightClicked()))
            new FountainGUI(event.getPlayer()).open();
    }

}
