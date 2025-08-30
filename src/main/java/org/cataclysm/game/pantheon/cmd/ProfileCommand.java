package org.cataclysm.game.pantheon.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.audience.AssistanceVerifier;
import org.cataclysm.game.pantheon.level.audience.PantheonSurvivor;
import org.cataclysm.game.pantheon.level.timer.PantheonTimer;

@CommandAlias("profile")
public class ProfileCommand extends BaseCommand {

    @Subcommand("assistance")
    @CommandCompletion("confirm|refute")
    private void assistance(CommandSender sender, String action) {
        if (!(sender instanceof Player player)) return;

        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        AssistanceVerifier verifier = pantheon.getAudience().getAssistanceVerifier();

        switch (action) {
            case "confirm" -> verifier.check(player);
            case "refute" -> verifier.refuse(player);
        }
    }

}
