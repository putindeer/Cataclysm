package org.cataclysm.game.events.pantheon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.boss.PantheonBosses;
import org.cataclysm.game.events.pantheon.utils.PantheonWarper;
import org.jetbrains.annotations.NotNull;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCMD extends BaseCommand {

    @Subcommand("create")
    private void create(CommandSender sender) {
        Cataclysm.setPantheon(new PantheonOfCataclysm());
    }

    @Subcommand("change")
    private void change(PantheonLevels level) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        pantheon.changeLevel(level);
    }

    @Subcommand("warp")
    @CommandCompletion(" @players")
    private void warp(CommandSender sender, PantheonLevels zones, @Optional Player player) {
        if (player == null) PantheonWarper.warp(zones);
        else PantheonWarper.teleport(player, zones);
    }

    @Subcommand("boss cast")
    private void bossCast(CommandSender sender, PantheonBosses boss) {
        if (!(sender instanceof Player player)) return;
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        PantheonBoss instance = boss.getInstance();
        instance.setPantheon(pantheon);
        instance.setController(player);
        instance.startPantheonFight();
    }

    @Subcommand("boss stop")
    private void bossStop() {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        PantheonBoss instance = pantheon.getBoss();
        instance.stopPantheonFight();
    }

    private @NotNull PantheonOfCataclysm getOrCreatePantheon() {
        var pantheon = Cataclysm.getPantheon();
        if (pantheon == null) pantheon = new PantheonOfCataclysm();
        return pantheon;
    }
}