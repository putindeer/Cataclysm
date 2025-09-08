package org.cataclysm.game.events.pantheon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.world.Dimensions;

public class PersistentTeleportListener implements Listener {
    private final Location teleportLocation;
    private final NamespacedKey teleportedKey;

    public PersistentTeleportListener(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
        this.teleportedKey = new NamespacedKey(Cataclysm.getInstance(), "initial_teleport_done");
    }

    public static void register() {
        Bukkit.getPluginManager().registerEvents(new PersistentTeleportListener(
                Dimensions.PALE_VOID.getWorld().getSpawnLocation()),
                Cataclysm.getInstance()
        );
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Revisar si el jugador ya ha sido teletransportado
        if (player.getPersistentDataContainer().has(teleportedKey, PersistentDataType.BYTE)) {
            return; // Ya teletransportado
        }

        // Teletransportar al jugador con un tick de retraso para que el jugador esté completamente cargado
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            player.teleport(teleportLocation);

            // Marcar al jugador como teletransportado
            player.getPersistentDataContainer().set(teleportedKey, PersistentDataType.BYTE, (byte) 1);
        }, 1L);
    }
}
