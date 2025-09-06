package org.cataclysm.game.events.pantheon.config.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.config.player.data.ProfileStorer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ProfileRegistry {
    private final Map<UUID, PantheonProfile> activeProfiles;

    private final ProfileStorer loader;

    public ProfileRegistry() {
        try {
            this.loader = new ProfileStorer();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar ProfileLoader", e);
        }
        this.activeProfiles = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new ProfileLoader(this), Cataclysm.getInstance());
    }

    /**
     * Obtiene el perfil de un jugador. Si no está cargado, lo carga desde disco o crea uno nuevo.
     */
    public void loadProfile(Player player) {
        UUID uuid = player.getUniqueId();
        activeProfiles.computeIfAbsent(uuid, this.loader::load);
    }

    /**
     * Guarda y descarga un perfil cuando el jugador sale.
     */
    public void unloadProfile(Player player) {
        UUID uuid = player.getUniqueId();
        PantheonProfile profile = activeProfiles.remove(uuid);
        if (profile != null) {
            loader.save(profile, uuid);
        }
    }

    /**
     * Guarda todos los perfiles en memoria (por ejemplo, al apagar el servidor).
     */
    public void saveAll() {
        for (Map.Entry<UUID, PantheonProfile> entry : activeProfiles.entrySet()) {
            loader.save(entry.getValue(), entry.getKey());
        }
    }
}
