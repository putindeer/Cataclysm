package org.cataclysm.game.pantheon.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.TheRagnarok;
import org.cataclysm.game.pantheon.level.levels.PantheonLevel;
import org.cataclysm.game.pantheon.level.levels.entrance.PantheonEntrance;
import org.cataclysm.game.pantheon.level.levels.treehouse.TreeHouseWaiting;
import org.cataclysm.game.pantheon.level.timer.PantheonTimer;
import org.jetbrains.annotations.NotNull;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCommand extends BaseCommand {

    @Subcommand("boss")
    @CommandCompletion("cast|stop THE_RAGNAROK|VOID_LORD")
    private void boss(CommandSender sender, String action, String display) {
        if (!(sender instanceof Player player)) return;

        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) pantheon = PantheonOfCataclysm.createPantheon();

        CataclysmBoss manager;
        switch (display.toUpperCase()) {
            case "THE_RAGNAROK" -> manager = new TheRagnarok(pantheon);
            case "VOID_LORD" -> manager = new TheRagnarok(pantheon);
            default -> {
                return;
            }
        }

        manager.setController(player);
        manager.startFight();
    }

    @Subcommand("timer")
    private void toolsTimer(int seconds) {
        PantheonTimer timer = Cataclysm.getPantheon().getTimer();
        if (timer == null) return;
        timer.setTimeLeft(seconds);
    }

    @Subcommand("event")
    @CommandCompletion("START|CANCEL")
    private void event(CommandSender sender, String action) {
        switch (action) {
            case "START" -> PantheonOfCataclysm.createPantheon().startLevel(new TreeHouseWaiting(Cataclysm.getPantheon()));
            case "CANCEL" -> Cataclysm.getPantheon().cancelEvent();
        }
    }

    @Subcommand("level")
    @CommandCompletion("FINALE|BREAK|TREE_COUNTDOWN|ENTRANCE|FIGHT_WARDEN|FIGHT_HYDRA|FIGHT_KING|FIGHT_LORD|FIGHT_RAGNAROK")
    private void action(CommandSender sender, @NotNull String id) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) pantheon = PantheonOfCataclysm.createPantheon();

        PantheonLevel level;
        switch (id.toUpperCase()) {
            case "BREAK" -> level = new PantheonEntrance(pantheon);
            case "TREE_COUNTDOWN" -> level = new TreeHouseWaiting(pantheon);
            case "ENTRANCE" -> level = new PantheonEntrance(pantheon);
            case "FINALE" -> level = new PantheonEntrance(pantheon);

            case "FIGHT_WARDEN" -> level = new PantheonEntrance(pantheon);
            case "FIGHT_HYDRA" -> level = new PantheonEntrance(pantheon);
            case "FIGHT_KING" -> level = new PantheonEntrance(pantheon);
            case "FIGHT_LORD" -> level = new PantheonEntrance(pantheon);
            case "FIGHT_RAGNAROK" -> level = new PantheonEntrance(pantheon);
            default -> {return;}
        }
        pantheon.startLevel(level);
    }
}
