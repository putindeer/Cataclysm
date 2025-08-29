package org.cataclysm.game.pantheon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.utils.PantheonBuilder;
import org.cataclysm.game.pantheon.level.PantheonLevels;
import org.cataclysm.game.pantheon.handlers.PlayerHandler;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.jetbrains.annotations.NotNull;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCMD extends BaseCommand {

    @Subcommand("teleport")
    @CommandCompletion("PANTHEON_ENTRANCE|WARDEN_ARENA")
    private void teleport(Player player, String area) {
        if (Cataclysm.getPantheon() == null) return;

        Location location = switch (area) {
            case "PANTHEON_ENTRANCE" -> PantheonLevels.PANTHEON_ENTRANCE.getCoreLocation();
            case "WARDEN_ARENA" -> PantheonLevels.WARDEN_ARENA.getCoreLocation();
            default -> player.getLocation();
        };
        PlayerHandler.teleport(player, location);
    }

    @Subcommand("create")
    @CommandCompletion("true|false")
    private void create(CommandSender sender, @Optional Boolean pasteStructure) {
        if (!(sender instanceof Player player)) return;

        if (Cataclysm.getPantheon() != null) {
            sendStaffMessage(player, "The Pantheon of Cataclysm is already created.");
            return;
        }

        PantheonBuilder.create();
        if (pasteStructure != null && pasteStructure) {
            PantheonBuilder.pastePantheonEntrance(player.getLocation());
        }

        sendStaffMessage(player, "Pantheon of Cataclysm has been created.");
    }

    @Subcommand("action")
    @CommandCompletion("open|close|start|stop")
    private void action(@NotNull String action, CommandSender sender) {
        if (!(sender instanceof Player player)) return;

        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) {
            sendStaffMessage(player, "The Pantheon of Cataclysm is not created. Use /pantheon create to create it.");
            return;
        }

        switch (action.toLowerCase()) {
            case "open" -> pantheon.openPantheon();
            case "close" -> pantheon.closePantheon();
            case "start" -> pantheon.startPantheon();
            case "stop" -> pantheon.stopPantheon();
            default -> sendStaffMessage(player, "Invalid action. Use open, close, start, or stop.");
        }
    }

    private void sendStaffMessage(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            ChatMessenger.sendStaffMessage(player, message);
        }
    }
}
