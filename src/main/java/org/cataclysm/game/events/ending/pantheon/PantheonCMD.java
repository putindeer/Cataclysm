package org.cataclysm.game.events.ending.pantheon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.Soundtrack;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.events.ending.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.ending.pantheon.boss.PantheonBosses;
import org.cataclysm.game.events.ending.pantheon.utils.PantheonSoundtrack;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCMD extends BaseCommand {

    @Subcommand("sfk")
    @CommandCompletion("loop|stopAll RAGNAROK|CATACLYSM")
    private void sfkLoop(String... commands) {
        PantheonSoundtrack soundtrack = getOrCreatePantheon().getSoundtrack();
        if (commands.length >= 1) {
            switch (commands[0]) {
                case "loop" -> {
                    if (commands.length >= 2) {
                        switch (commands[1]) {
                            case "RAGNAROK" -> soundtrack.loopRagnarokTracks();
                            case "CATACLYSM" -> soundtrack.loopCataclysmTracks(1);
                        }
                    }
                }
                case "stopAll" -> soundtrack.stopAll();
            }
        }
    }

    @Subcommand("boss stop")
    private void bossStop() {
        CataclysmBoss boss = getOrCreatePantheon().getBoss();
        if (boss != null) boss.stopFight();
    }

    @Subcommand("boss cast")
    private void bossCast(CommandSender sender, PantheonBosses boss) {
        if (!(sender instanceof Player player)) return;

        PantheonBoss instance = boss.getInstance();
        instance.setController(player);
        instance.startFight();
    }

    private PantheonOfCataclysm getOrCreatePantheon() {
        var pantheon = Cataclysm.getPantheon();
        if (pantheon == null) pantheon = new PantheonOfCataclysm();
        return pantheon;
    }
}