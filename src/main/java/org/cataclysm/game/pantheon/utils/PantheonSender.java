package org.cataclysm.game.pantheon.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PantheonSender {
    private static final ScheduledExecutorService service = Cataclysm.getPantheon().getService();

    public static void sendAnimatedActionBar(Player player, String message, int totalDurationMs, int delayMs) {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> sendAnimatedActionBar(player, message, totalDurationMs), delayMs / 50);
    }
    public static void sendAnimatedActionBar(Player player, String message, int totalDurationMs) {
        List<Character> characters = message.toLowerCase().chars().mapToObj(c -> (char) c).toList();
        ScheduledExecutorService service = Cataclysm.getPantheon().getService();

        double interval = (double) totalDurationMs / characters.size();
        for (int i = 0; i < characters.size(); i++) {
            String display = TinyCaps.tinyCaps(message.substring(0, i + 1));
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(display));
                    player.getWorld().playSound(player, Sound.UI_BUTTON_CLICK, 0.35F, 1.92F);
                });
            }, (i * (long) interval), TimeUnit.MILLISECONDS);
        }
    }

    public static void sendPantheonMessage(Player player, String message) {
        String prefix = wrapPrefix("<GRADIENT:#FC8C03:#B5A16B>ᴘᴀɴᴛʜᴇᴏɴ ᴏꜰ ᴄᴀᴛᴀᴄʟʏꜱᴍ</GRADIENT>", "<#B3AB9B>");
        player.sendMessage(MiniMessage.miniMessage().deserialize(prefix)
                .append(MiniMessage.miniMessage().deserialize(message)));

        World world = player.getWorld();
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 2.0F, 0.75F);
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 2.0F, 1.25F);
    }

    private static @NotNull String wrapPrefix(String prefix, String textColor) {
        return "<#8c8c8c><b>[<reset>" + prefix + "<#8c8c8c><b>]<reset> <#727272>» " + textColor;
    }
}
