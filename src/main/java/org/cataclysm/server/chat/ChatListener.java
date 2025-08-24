package org.cataclysm.server.chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.data.PlayerData;
import org.cataclysm.game.player.tag.role.RoleManager;
import org.cataclysm.game.player.tag.team.TeamManager;
import org.cataclysm.game.raids.bosses.pale_king.PaleKing;
import org.cataclysm.global.utils.chat.ChatMessenger;

@Registrable
public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (Cataclysm.getBoss() != null
                && Cataclysm.getBoss() instanceof PaleKing paleKing
                && paleKing.phase.getCurrent() > 1
                && !event.getPlayer().isOp()) event.setCancelled(true);
    }

    @EventHandler
    private void onAsync(AsyncChatEvent event) {
        if (Cataclysm.getBoss() != null && Cataclysm.getBoss() instanceof PaleKing paleKing && paleKing.phase.getCurrent() > 1) {
            event.setCancelled(true);
            return;
        }

        var player = event.getPlayer();
        var data = CataclysmPlayer.getCataclysmPlayer(player).getData();
        var role = new RoleManager(data);
        var team = new TeamManager(data);

        var chatMode = data.getChatMode();

        if (chatMode == ChatMode.TEAM) {
            handleTeamChat(event, player, data, role, team);
            return;
        }

        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> {
            var prefix = role.build()
                    .append(MiniMessage.miniMessage().deserialize(" " + role.getRole().getHex() + player.getName() + " "))
                    .append(team.build());

            var chatColor = "<#ffffff>";
            var serializer = PlainTextComponentSerializer.plainText();
            var messageText = serializer.serialize(message);
            var formattedMessage = getFormattedMessage(player, chatColor, messageText);
            return prefix.append(MiniMessage.miniMessage().deserialize(ChatMessenger.getTextColor() + " » ")).append(formattedMessage);
        }));
    }

    private void handleTeamChat(AsyncChatEvent event, Player player, PlayerData data, RoleManager role, TeamManager team) {
        event.setCancelled(true);

        var serializer = PlainTextComponentSerializer.plainText();
        var message = serializer.serialize(event.message());

        var prefix = MiniMessage.miniMessage().deserialize(ChatMessenger.getTextColor() + "[")
                .append(team.build())
                .append(MiniMessage.miniMessage().deserialize(ChatMessenger.getTextColor() + "]"))
                .append(MiniMessage.miniMessage().deserialize(role.getRole().getHex() + " " + player.getName()));

        var chatColor = "<#89c5cc>";
        var formattedMessage = getFormattedMessage(player, chatColor, message);

        for (var recipient : Bukkit.getOnlinePlayers()) {
            var recipientData = CataclysmPlayer.getCataclysmPlayer(recipient).getData();
            boolean sameTeam = data.getTeam().equals(recipientData.getTeam());
            if (sameTeam) {
                recipient.sendMessage(prefix.append(MiniMessage.miniMessage().deserialize(ChatMessenger.getTextColor() + " » ")).append(formattedMessage));
                recipient.playSound(recipient, Sound.BLOCK_NOTE_BLOCK_CHIME, 0.3f, 1.0f);
            }
        }
    }

    private Component getFormattedMessage(Player player, String chatColor, String messageText) {
        Component formattedMessage = MiniMessage.miniMessage().deserialize(chatColor + messageText);

        if (!player.isOp()) {
            chatColor = chatColor.replace("<", "").replace(">", "");
            formattedMessage = formattedMessage.style(Style.style(TextColor.fromHexString(chatColor))
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, false));
        }
        return formattedMessage;
    }

}
