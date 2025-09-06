package org.cataclysm.game.events.pantheon.config.player.data;

import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;

import java.util.UUID;

/**
 * Snapshot inmutable del estado de un PantheonProfile.
 */
public record ProfileSnapshot(
        UUID uuid,
        boolean ready,
        boolean alive
) {
    /**
     * Construye un snapshot desde un PantheonProfile activo.
     */
    public static ProfileSnapshot fromProfile(PantheonProfile profile) {
        return new ProfileSnapshot(
                profile.getUuid(),
                profile.isReady(),
                profile.isAlive()
        );
    }
}
