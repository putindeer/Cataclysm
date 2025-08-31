package org.cataclysm.game.pantheon.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.pantheon.bosses.pale_king.PaleKing;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.TheRagnarok;
import org.cataclysm.game.pantheon.bosses.twisted_warden.PantheonWarden;
import org.cataclysm.game.pantheon.helpers.PantheonTeleport;
import org.cataclysm.game.pantheon.level.levels.LevelBuilder;
import org.cataclysm.game.pantheon.level.levels.PantheonZones;
import org.cataclysm.game.pantheon.level.levels.treehouse.TreeHouseWaiting;
import org.cataclysm.game.pantheon.level.timer.PantheonTimer;

import java.util.stream.Collectors;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCommand extends BaseCommand {

    @Subcommand("update PALE_KING phase")
    private void updatePaleKing() {
        CataclysmBoss boss = Cataclysm.getBoss();
        if (boss instanceof PaleKing king) {
            king.getPhase().start(3);
        }
    }

    @Subcommand("update PALE_KING amplifier")
    private void updatePaleKingAmplifier(int amplifier) {
        CataclysmBoss boss = Cataclysm.getBoss();
        if (boss instanceof PaleKing king) {
            king.amplifier = amplifier;
        }
    }

    @Subcommand("update THE_RAGNAROK phase")
    private void update() {
        CataclysmBoss boss = Cataclysm.getBoss();
        if (boss instanceof TheRagnarok ragnarok) {
            ragnarok.event.changePhase();
        }
    }

    @Subcommand("level warpAll")
    public void teleport(PantheonZones zones) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonTeleport.teleport(player, zones.getLocation());
        }
    }

    @Subcommand("boss stop")
    private void bossStop() {
        CataclysmBoss boss = Cataclysm.getBoss();
        if (boss != null) boss.stopFight();
    }

    @Subcommand("boss cast")
    @CommandCompletion("THE_RAGNAROK|PALE_KING|HYDRA|WARDEN")
    private void boss(CommandSender sender, String display) {
        if (!(sender instanceof Player player)) return;

        LevelBuilder.buildWorld();

        CataclysmBoss manager;
        switch (display.toUpperCase()) {
            case "THE_RAGNAROK" -> manager = new TheRagnarok();
            case "PALE_KING" -> manager = new PaleKing();
            case "HYDRA" -> manager = new PantheonHydra();
            case "WARDEN" -> manager = new PantheonWarden();
            default -> {return;}
        }

        manager.setController(player);
        manager.startFight();
    }

    @Subcommand("zone teleport")
    private void zoneTeleport(CommandSender sender, PantheonZones zones) {
        if (!(sender instanceof Player player)) return;
        player.teleport(zones.getLocation());
    }

    @Subcommand("zone paste")
    private void pasteScheme(PantheonZones zones) {
        zones.getSchemLoader().pasteSchematic(zones.getLocation());
    }

    @Subcommand("zone pasteAll")
    private void pasteSchemeAll() {
        LevelBuilder.handleStructures();
    }

    @Subcommand("event")
    @CommandCompletion("START|CANCEL|CREATE")
    private void event(CommandSender sender, String action) {
        switch (action) {
            case "CREATE" -> PantheonOfCataclysm.createPantheon();
            case "START" -> Cataclysm.getPantheon().startLevel(new TreeHouseWaiting(Cataclysm.getPantheon()));
            case "CANCEL" -> Cataclysm.getPantheon().cancelEvent();
        }
    }

    @Subcommand("system timer")
    private void systemTimer(int seconds) {
        PantheonTimer timer = Cataclysm.getPantheon().getTimer();
        if (timer == null) return;
        timer.setTimeLeft(seconds);
    }

    @Subcommand("system audience")
    private void systemAudience(CommandSender commandSender, int seconds) {
        if (!(commandSender instanceof Player player)) return;
        player.sendMessage("Survivors: " + getSurvivorsList());
    }

    private static String getSurvivorsList() {
        return Cataclysm.getPantheon()
                .getAudience()
                .getSurvivors()
                .keySet() // si los nombres están en la clave
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}
