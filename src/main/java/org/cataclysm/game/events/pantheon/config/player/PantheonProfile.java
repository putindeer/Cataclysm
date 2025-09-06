package org.cataclysm.game.events.pantheon.config.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.config.player.data.ProfileSnapshot;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter @Setter
public class PantheonProfile {
    private boolean ready = false;
    private boolean alive = true;

    private final UUID uuid;

    public PantheonProfile(UUID uuid) {
        this.uuid = uuid;
    }

    public PantheonProfile(ProfileSnapshot snapshot) {
        this.uuid = snapshot.uuid();
        this.ready = snapshot.ready();
        this.alive = snapshot.alive();
    }

    public void log() {
        this.getPlayer().sendMessage("Ready: " + this.ready);
        this.getPlayer().sendMessage("Alive: " + this.alive);
    }

    /**
     * Obtiene el jugador propietario de este perfil.
     * Devuelve null si el jugador no está en línea.
     */
    public @NotNull Player getPlayer() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) throw new IllegalStateException("Player with UUID " + uuid + " is not online.");
        return player;
    }

    public static PantheonProfile fromPlayer(PantheonOfCataclysm pantheon, Player player) {
        ProfileRegistry registry = pantheon.getConfigurator().getRegistry();
        return registry.getActiveProfiles().get(player.getUniqueId());
    }
}
