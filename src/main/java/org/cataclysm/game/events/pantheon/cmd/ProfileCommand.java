package org.cataclysm.game.events.pantheon.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;
import org.cataclysm.game.events.pantheon.orchestrator.PantheonOrchestrator;
import org.cataclysm.game.events.pantheon.utils.PantheonTimer;

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