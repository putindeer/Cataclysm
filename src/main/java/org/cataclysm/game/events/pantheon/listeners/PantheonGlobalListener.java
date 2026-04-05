package org.cataclysm.game.events.pantheon.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;

@Registrable
public class PantheonGlobalListener implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        Player player = event.getPlayer();

        if (pantheon.getTimer() == null) return;
        pantheon.getTimer().setPlayerVisibility(player, true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        if (pantheon.getBoard() != null) {
            pantheon.getBoard().remove(event.getPlayer()); // Quita scoreboard del jugador que salió
        }
    }
}
