package org.cataclysm.game.events.pantheon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.boss.PantheonBosses;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCMD extends BaseCommand {

    @Subcommand("boss stop")
    private void bossStop() {
        CataclysmBoss boss = Cataclysm.getBoss();
        if (boss != null) boss.stopFight();
    }

    @Subcommand("boss cast")
    private void bossCast(CommandSender sender, PantheonBosses boss) {
        if (!(sender instanceof Player player)) return;

        PantheonBoss instance = boss.getInstance();
        instance.setController(player);
        instance.startFight();
    }

}