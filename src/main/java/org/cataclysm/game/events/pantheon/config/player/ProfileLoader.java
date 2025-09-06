package org.cataclysm.game.events.pantheon.config.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileLoader implements Listener {
    private final ProfileRegistry registry;

    public ProfileLoader(ProfileRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        this.registry.loadProfile(event.getPlayer());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        this.registry.unloadProfile(event.getPlayer());
    }
}
