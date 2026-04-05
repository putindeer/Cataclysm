package org.cataclysm.game.events.pantheon.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;

@CommandAlias("profile")
public class ProfileCommand extends BaseCommand {

    @Default
    private void profile(CommandSender commandSender) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(commandSender instanceof Player player)) return;

        PantheonProfile profile = PantheonProfile.fromPlayer(pantheon, player);
        profile.log();
    }

}