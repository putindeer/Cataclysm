package org.cataclysm.game.pantheon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.world.PantheonLocations;
import org.cataclysm.game.pantheon.handlers.PantheonPlayerHandler;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.jetbrains.annotations.NotNull;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCommand extends BaseCommand {

    @Subcommand("teleport")
    @CommandCompletion("PANTHEON_ENTRANCE|WARDEN_ARENA")
    private void teleport(CommandSender commandSender, String area) {
        if (!(commandSender instanceof Player player)) return;

        if (Cataclysm.getPantheon() == null) {
            ChatMessenger.sendMessage(player, "There is no active Pantheon.");
            return;
        }

        Location location = null;
        switch (area) {
            case "PANTHEON_ENTRANCE" -> location = PantheonLocations.PANTHEON_ENTRANCE.getCoreLocation();
            case "WARDEN_ARENA" -> location = PantheonLocations.WARDEN_ARENA.getCoreLocation();
            default -> ChatMessenger.sendMessage(player, "Unknown area: " + area);
        }

        PantheonPlayerHandler.teleport(player, location);
    }

    @Subcommand("create")
    private void create() {
        if (Cataclysm.getPantheon() != null) return;
        PantheonOfCataclysm.create();
    }

    @Subcommand("action")
    @CommandCompletion("open|close|start|stop")
    private void action(@NotNull String action) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        switch (action) {
            case "open" -> pantheon.openPantheon();
            case "close" -> pantheon.closePantheon();
            case "start" -> pantheon.startPantheon();
            case "stop" -> pantheon.stopPantheon();
        }
    }

    @Subcommand("info")
    private void info(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();

        ChatMessenger.sendMessage(player, "Current Pantheon information");
        player.sendMessage("- Current Phase: " + pantheon.getPhase().name());
        player.sendMessage("- World: " + pantheon.getWorld().getName());
    }
}
