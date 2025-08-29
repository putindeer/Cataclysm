package org.cataclysm.game.pantheon.utils;

import org.cataclysm.game.pantheon.handlers.PlayerHandler;
import org.jetbrains.annotations.NotNull;

public class PantheonGlobalUtils {
    private static @NotNull String getActionBarDisplay() {
        int ready = PlayerHandler.getReadyCount();
        int size = PlayerHandler.getParticipants().size();
        return  "estás listo para entrar al panteón [" + ready + "/" + size + "]";
    }
}
