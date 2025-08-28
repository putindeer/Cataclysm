package org.cataclysm.game.pantheon.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class PantheonMessenger {
    private static final String glitchedPrefix = wrapPrefix("<#5C595C><OBF>Void Lord</OBF>", "<#818182>");
    private static final String pantheonPrefix = wrapPrefix("<GRADIENT:#FC8C03:#B5A16B>" + TinyCaps.tinyCaps("pantheon of cataclysm") + "</GRADIENT>", "<#B3AB9B>");

    public static void sendPantheonMessage(Player player, String message) {
        sendPantheonMessage(player, MiniMessage.miniMessage().deserialize(TinyCaps.tinyCaps(message)));
    }
    public static void sendPantheonMessage(Player player, Component message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(pantheonPrefix).append(message));
        player.getWorld().playSound(player, Sound.ITEM_TRIDENT_RETURN, 2.0F, 0.75F);
        player.getWorld().playSound(player, Sound.ITEM_TRIDENT_RETURN, 2.0F, 1.25F);
    }

    public static void sendGlitchedMessage(Player player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(glitchedPrefix).append(MiniMessage.miniMessage().deserialize(message)));
    }

    @NotNull
    private static String wrapPrefix(String prefix, String textColor) {
        return "<#8c8c8c><b>[<reset>" + TinyCaps.tinyCaps(prefix) + "<#8c8c8c><b>]<reset> <#727272>» " + textColor;
    }
}
