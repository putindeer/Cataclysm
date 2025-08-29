package org.cataclysm.game.pantheon.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerStatusHandler {
    public enum Status{IDDLE, PREPARED, PLAYING}

    private static final String DATA_KEY = "PANTHEON-STATUS";

    public static void setStatus(Player player, @NotNull Status status) {
        PersistentData.set(player, DATA_KEY, PersistentDataType.STRING, status.name());
    }

    public static @Nullable Status getStatus(Player player) {
        String value = PersistentData.get(player, DATA_KEY, PersistentDataType.STRING);
        if (value == null) return null;
        return Status.valueOf(value.toUpperCase());
    }

    public static List<? extends Player> getPlayers(Status status) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> getStatus(player) == status)
                .toList();
    }
}
