package org.cataclysm.global.utils.chat;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class ChatMessenger {
    private static final @Getter String textColor = "<#B0B0B0>";

    private static final @Getter String cataclysmColor = "<#A03E3E>";

    private static final @Getter String staffColor = "<#B64E4E>";

    private static final String cataclysmPrefix = wrapPrefix(cataclysmColor + "Cataclysm");

    private static final String staffPrefix = wrapPrefix(staffColor + "Staff");

    public static void broadcastMessage(String message) {
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            sendMessage(onlinePlayers, message);
        }
    }

    public static void sendStaffMessage(@NotNull Player player, Component message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(staffPrefix).append(message));
    }
    public static void sendStaffMessage(@NotNull Player player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(staffPrefix + message));
    }

    public static void sendMessage(@NotNull Player player, Component message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(cataclysmPrefix).append(message));
    }
    public static void sendMessage(@NotNull Player player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(cataclysmPrefix + message));
    }

    @NotNull
    private static String wrapPrefix(String prefix) {
        return "<#8c8c8c><b>[<reset>" + TinyCaps.tinyCaps(prefix) + "<#8c8c8c><b>]<reset> <#727272>» " + textColor;
    }
}
