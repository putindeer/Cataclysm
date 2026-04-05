package org.cataclysm.game.events.pantheon.config.player.data;

import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Loader para guardar y restaurar perfiles de jugadores en archivos separados.
 * Cada jugador tiene su archivo en: /pantheon/profiles/<uuid>.json
 */
public class ProfileStorer {
    private final Path baseDir;

    public ProfileStorer() throws IOException {
        this.baseDir = Path.of("pantheon", "profiles");
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }
    }

    private Path getFile(UUID uuid) {
        return baseDir.resolve(uuid.toString() + ".json");
    }

    /**
     * Guarda el perfil de un jugador en su archivo dedicado.
     */
    public void save(PantheonProfile profile, UUID uuid) {
        Bukkit.getConsoleSender().sendMessage("profile_uuid: " + profile.getUuid());
        Bukkit.getConsoleSender().sendMessage("inserted_uuid: " + uuid);
        try {
            Path file = getFile(uuid);
            JsonConfig cfg = JsonConfig.cfg(file.toString(), Cataclysm.getInstance());

            ProfileSnapshot snapshot = ProfileSnapshot.fromProfile(profile);
            cfg.setJsonObject(Cataclysm.getGson().toJsonTree(snapshot).getAsJsonObject());

            cfg.save();
            Bukkit.getConsoleSender().sendMessage(uuid + " ha sido GUARDADO correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el perfil de " + profile.getUuid(), e);
        }
    }

    /**
     * Restaura el perfil de un jugador desde su archivo y elimina el archivo después de cargarlo.
     */
    public PantheonProfile load(UUID uuid) {
        Path path = getFile(uuid);
        try {
            JsonConfig cfg = JsonConfig.cfg(path.toString(), Cataclysm.getInstance());

            ProfileSnapshot snapshot = Cataclysm.getGson().fromJson(cfg.getJsonObject(), ProfileSnapshot.class);

            if (snapshot.uuid() == null) {
                Bukkit.getConsoleSender().sendMessage("No se pudo cargar el perfil de " + uuid + ", se crea uno nuevo.");
                return new PantheonProfile(uuid);
            }

            Bukkit.getConsoleSender().sendMessage(uuid + " ha sido CARGADO correctamente.");
            return new PantheonProfile(snapshot);
        } catch (Exception e) {
            throw new RuntimeException("Error al CARGA el perfil de " + uuid, e);
        }
    }
}
