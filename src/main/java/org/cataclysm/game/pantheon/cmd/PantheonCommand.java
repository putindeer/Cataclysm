package org.cataclysm.game.pantheon.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.LevelHandler;
import org.cataclysm.game.pantheon.level.PantheonAreas;
import org.cataclysm.global.utils.chat.ChatMessenger;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCommand extends BaseCommand {

    @Subcommand("action")
    @CommandCompletion("open|close|start|stop")
    private void action(String action) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();

        switch (action) {
            case "open" -> pantheon.openPantheon();
            case "close" -> pantheon.closePantheon();
            //case "start" -> pantheon.startPantheon();
            case "stop" -> pantheon.stopPantheon();
        }
    }

    @Subcommand("info")
    private void info(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();

        ChatMessenger.sendMessage(player, "Current Pantheon information");
        player.sendMessage("- Current Phase: " + pantheon.phase.name());
        player.sendMessage("- World: " + pantheon.world.getName());
    }
}
