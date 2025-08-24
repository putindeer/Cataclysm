package org.cataclysm.global.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.cataclysm.server.chat.ChatMode;

@CommandAlias("cataclysm|cata|ct")
public class CataclysmCommand extends BaseCommand {

    @Subcommand("ragnarok")
    private void ragnarok(CommandSender commandSender) {
        if (!(commandSender instanceof Player sender)) return;

        var message = "No hay Ragnarök activa.";
        if (Cataclysm.getRagnarok() != null) message = "<#478db6>Ragnarök <#B0B0B0>de nivel <#478db6>" + Cataclysm.getRagnarok().getData().getLevel() + " <#B0B0B0>activa.";

        ChatMessenger.sendMessage(sender, message);
    }

    @Subcommand("mortality")
    private void mortality(CommandSender commandSender) {
        if (!(commandSender instanceof Player sender)) return;
        var cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(sender);
        var manager = cataclysmPlayer.getMortalityManager();
        ChatMessenger.sendMessage(sender, "Tu porcentaje de mortalidad es de " +  ChatMessenger.getCataclysmColor() + manager.getPercentage());
    }

    @Subcommand("day")
    private void day(CommandSender commandSender) {
        if (!(commandSender instanceof Player sender)) return;
        ChatMessenger.sendMessage(sender, "Nos encontramos en el día " + ChatMessenger.getCataclysmColor() + Cataclysm.getDay() + ChatMessenger.getTextColor() + ".");
    }

    @Subcommand("chat")
    private void chat(CommandSender commandSender, @Optional ChatMode chatMode) {
        if (!(commandSender instanceof Player sender)) return;
        var cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(sender);
        if (chatMode == null) chatMode = cataclysmPlayer.getData().getChatMode() == ChatMode.GLOBAL ? ChatMode.TEAM : ChatMode.GLOBAL;
        if (chatMode == ChatMode.TEAM && cataclysmPlayer.getData().getTeam().equals("NONE")) {
            ChatMessenger.sendMessage(sender, "No puedes usar el modo de chat de team si no tienes un equipo.");
            return;
        }
        cataclysmPlayer.getData().setChatMode(chatMode);
        ChatMessenger.sendMessage(sender, "Has puesto tu modo de chat en " + ChatMessenger.getCataclysmColor() + chatMode.name().toLowerCase() + ChatMessenger.getTextColor() + ".");
    }

}
