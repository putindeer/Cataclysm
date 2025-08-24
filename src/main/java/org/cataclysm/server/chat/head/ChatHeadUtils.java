package org.cataclysm.server.chat.head;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minso.chathead.API.ChatHeadAPI;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.global.utils.text.TextUtils;

import java.util.HashMap;

public class ChatHeadUtils {

    private static final HashMap<Player, Component> headCache = new HashMap<>();

    public static Component getHeadFont(Player player) {
        if (headCache.containsKey(player)) return headCache.get(player);
        Component headFont = Component.text("");
        try {
            var head = new ChatHeadAPI(Cataclysm.getInstance()).getHeadAsString(player);
            head = head.replace("§", "").replace("x", "#");

            String formatedHex = TextUtils.wrapHexCodes(head);
            headFont = headFont.append(MiniMessage.miniMessage().deserialize(" <!shadow>" + formatedHex));
            headCache.put(player, headFont);
        } catch (IllegalArgumentException exception) {
            Cataclysm.getInstance().getLogger().warning("Failed to get head font for player " + player.getName());
        }
        return headFont;
    }
}
