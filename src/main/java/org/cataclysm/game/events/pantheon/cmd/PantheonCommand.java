package org.cataclysm.game.events.pantheon.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonBosses;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.orchestrator.PantheonOrchestrator;
import org.cataclysm.game.events.pantheon.utils.PantheonTimer;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCommand extends BaseCommand {
    private final MiniMessage mm = MiniMessage.miniMessage();

    private PantheonOfCataclysm getPantheonOrInit() {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        return pantheon != null ? pantheon : PantheonOfCataclysm.initializePantheon();
    }

    private void broadcastToStaff(CommandSender sender, String executedCommand) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("admin.perms")) {
                p.sendMessage(mm.deserialize("<#879687><italic>" + sender.getName() + ": " + executedCommand));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 3f, 1.15f);
            }
        }
        Bukkit.getConsoleSender().sendMessage(mm.deserialize("<#537054><italic>" + sender.getName() + ": " + executedCommand));
    }

    // =======================
    // COMMANDS
    // =======================

    @Subcommand("power")
    @CommandCompletion("initialize|terminate")
    private void power(CommandSender sender, String action) {
        switch (action) {
            case "initialize" -> {
                PantheonOfCataclysm.initializePantheon();
                broadcastToStaff(sender, "/pantheon power initialize");
            }
            case "terminate" -> {
                PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
                if (pantheon != null) pantheon.terminate();
                broadcastToStaff(sender, "/pantheon power terminate");
            }
        }
    }

    @Subcommand("start")
    @CommandCompletion("event|countdown")
    private void start(CommandSender sender, String action) {
        PantheonOfCataclysm pantheon = getPantheonOrInit();
        PantheonOrchestrator orchestrator = pantheon.getOrchestrator();
        switch (action) {
            case "event" -> {
                orchestrator.startLevel(true, PantheonLevels.PANTHEON_ENTRANCE);
                broadcastToStaff(sender, "/pantheon start event");
            }
            case "countdown" -> {
                orchestrator.startLevel(true, PantheonLevels.PALE_TREE);
                broadcastToStaff(sender, "/pantheon start countdown");
            }
        }
    }

    @Subcommand("level boss")
    private void levelBoss(CommandSender sender, PantheonBosses boss) {
        if (!(sender instanceof Player player)) return;

        PantheonOfCataclysm pantheon = getPantheonOrInit();
        pantheon.setController(player);
        pantheon.getOrchestrator().startBossFight(boss);

        broadcastToStaff(sender, "/pantheon level boss " + boss.name());
    }

    @Subcommand("level fountain")
    private void levelFountain(CommandSender sender, PantheonLevels level, @Optional Boolean autoElapse) {
        if (!(sender instanceof Player)) return;
        if (autoElapse == null) autoElapse = true;

        PantheonOfCataclysm pantheon = getPantheonOrInit();
        pantheon.getOrchestrator().startFountain(level);

        broadcastToStaff(sender, "/pantheon level fountain" + level.name() + " " + autoElapse);
    }

    @Subcommand("level start")
    @CommandCompletion(" true|false")
    private void level(CommandSender sender, PantheonLevels level, @Optional Boolean autoElapse) {
        if (!(sender instanceof Player player)) return;

        PantheonOfCataclysm pantheon = getPantheonOrInit();
        pantheon.setController(player);

        if (autoElapse == null) autoElapse = true;
        pantheon.getOrchestrator().startLevel(autoElapse, level);

        broadcastToStaff(sender, "/pantheon level start" + level.name() + " " + autoElapse);
    }

    @Subcommand("manage timer")
    private void manageTimer(CommandSender sender, int seconds) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        PantheonTimer timer = pantheon.getTimer();
        if (timer == null) return;

        timer.setTimeLeft(seconds);
        broadcastToStaff(sender, "/pantheon manage timer " + seconds);
    }

    @Subcommand("manage boss forceStop")
    private void manageBossForceStop(CommandSender sender) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        PantheonBoss boss = pantheon.getBoss();
        if (boss != null) boss.stopPantheonFight();

        broadcastToStaff(sender, "/pantheon manage boss forceStop");
    }

    @Subcommand("manage boss setController")
    @CommandCompletion("@players")
    private void manageBossSetController(CommandSender sender, @Optional Player player) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        pantheon.setController(player);
        broadcastToStaff(sender, "/pantheon manage boss setController "
                + (player != null ? player.getName() : "null"));
    }

    @Subcommand("manage boss setHealth")
    private void manageBossSetHealth(CommandSender sender, int health) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        PantheonBoss boss = pantheon.getBoss();
        if (boss == null) return;

        boss.setHealth(health);
        boss.updateBar();

        if (boss instanceof VoidLord lord) {
            lord.handleEvents();
        }

        broadcastToStaff(sender, "/pantheon manage boss setHealth " + health);
    }

    @Subcommand("tp")
    private void tp(CommandSender sender, PantheonLevels level) {
        if (!(sender instanceof Player player)) return;

        Location loc = level.getLocation();
        if (loc == null || loc.getWorld() == null) {
            sender.sendMessage(mm.deserialize("<red>La ubicación del nivel no está disponible.</red>"));
            return;
        }

        player.teleportAsync(loc);
        broadcastToStaff(sender, "/pantheon tp " + level.name());
    }
}