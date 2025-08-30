package org.cataclysm.game.pantheon.level.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.audience.PantheonSurvivor;
import org.cataclysm.game.pantheon.level.levels.entrance.preparation.EntranceMob;
import org.cataclysm.game.pantheon.level.levels.entrance.preparation.PreparationGUI;
import org.cataclysm.game.pantheon.level.timer.PantheonTimer;
import org.cataclysm.game.pantheon.level.audience.PlayerStatus;

public class PantheonPlayerListener extends PantheonListener {
    public PantheonPlayerListener(PantheonOfCataclysm pantheon) {
        super(pantheon);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PantheonSurvivor survivor = getPantheon().getAudience().getProfile(player.getUniqueId());
        if (survivor != null) survivor.setStatus(PlayerStatus.IDDLE);

        PantheonTimer timer = getPantheon().getTimer();
        if (timer != null) timer.handlePlayerView(player, true);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Bukkit.getConsoleSender().sendMessage(event.getRightClicked().getType().name());
        if (EntranceMob.isEntrance(event.getRightClicked())) new PreparationGUI(event.getPlayer()).open();
    }
}