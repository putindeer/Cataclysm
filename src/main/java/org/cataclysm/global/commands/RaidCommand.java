package org.cataclysm.global.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.mob.custom.cataclysm.mirage.MirageShulker;
import org.cataclysm.game.events.raids.bosses.RaidBosses;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.CalamityHydra;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.HydraPhase;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.entity.SleepingHydra;
import org.cataclysm.game.events.raids.structures.RaidStructures;
import org.cataclysm.global.utils.chat.ChatMessenger;

import java.util.List;

@CommandAlias("raid")
@CommandPermission("admin.perms")
public class RaidCommand extends BaseCommand {

    @Subcommand("restore")
    @Description("Restores the raid location where this command is executed")
    private void restore(CommandSender commandSender, RaidStructures structure) {
        RaidStructure manager = structure.getStructure();
        switch (structure) {
            case MOTHER -> {
                List<Location> locations = manager.getArea().getBlockLocations(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
                World world = manager.getArea().center().getWorld();
                for (Location location : locations) {
                    world.getBlockAt(location).setType(Material.AIR);
                    CataclysmMob shulkerMob = new MirageShulker(((CraftWorld) world).getHandle());
                    shulkerMob.addFreshEntity(location.add(0.5, 0, 0.5), CreatureSpawnEvent.SpawnReason.CUSTOM);
                }
            }
        }
    }

    @Subcommand("boss start")
    private void start(CommandSender commandSender, RaidBosses boss) {
        if (!(commandSender instanceof Player player)) return;

        CataclysmBoss manager = boss.getManager();
        manager.setController(player);
        manager.startFight();
    }

    @Subcommand("boss stop")
    private void stop(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;

        CataclysmBoss manager = Cataclysm.getBoss();
        if (manager == null) {
            ChatMessenger.sendStaffMessage(player, "No hay un jefe activo.");
            return;
        }

        manager.stopFight();
        ChatMessenger.sendStaffMessage(player, "Jefe detenido.");
    }

    @Subcommand("boss manage setHealth")
    private void setHealth(CommandSender commandSender, int health) {
        if (!(commandSender instanceof Player player)) return;

        CataclysmBoss manager = Cataclysm.getBoss();
        if (manager == null) {
            ChatMessenger.sendStaffMessage(player, "No hay un jefe activo.");
            return;
        }

        manager.health = health;
        manager.updateBar();
    }

    @Subcommand("boss test hydra setModel")
    private void testHydraSetModel(CommandSender commandSender, int ordinal) {
        if (!(commandSender instanceof Player player)) return;

        CataclysmBoss manager = Cataclysm.getBoss();
        if (manager == null) {
            ChatMessenger.sendStaffMessage(player, "No hay un jefe activo.");
            return;
        }

        if (!(manager instanceof CalamityHydra hydra)) {
            ChatMessenger.sendStaffMessage(player, "El jefe no es la Calamity Hydra.");
            return;
        }

        HydraPhase phaseManager = hydra.phase;
        phaseManager.updateModel(ordinal);
    }

    @Subcommand("boss test hydra setPhase")
    private void testHydraSetPhase(CommandSender commandSender, int phase) {
        if (!(commandSender instanceof Player player)) return;

        CataclysmBoss manager = Cataclysm.getBoss();
        if (manager == null) {
            ChatMessenger.sendStaffMessage(player, "No hay un jefe activo.");
            return;
        }

        if (!(manager instanceof CalamityHydra hydra)) {
            ChatMessenger.sendStaffMessage(player, "El jefe no es la Calamity Hydra.");
            return;
        }

        HydraPhase phaseManager = hydra.phase;
        phaseManager.start(phase);
    }

    @Subcommand("boss test hydra setFury")
    private void testHydraSetFury(CommandSender commandSender, double fury) {
        if (!(commandSender instanceof Player player)) return;

        CataclysmBoss manager = Cataclysm.getBoss();
        if (manager == null) {
            ChatMessenger.sendStaffMessage(player, "No hay un jefe activo.");
            return;
        }

        if (!(manager instanceof CalamityHydra hydra)) {
            ChatMessenger.sendStaffMessage(player, "El jefe no es la Calamity Hydra.");
            return;
        }

        hydra.rage.setCurrent(fury);
    }

    @Subcommand("boss test hydra summonSleepingDragon")
    private void testHydraSetFury(CommandSender commandSender) {
        RaidStructure structure = RaidStructures.MOTHER.getStructure();

        CataclysmArea arena = structure.getBossArena();
        Location center = arena.center();

        World world = center.getWorld();
        SleepingHydra sleepingHydra = new SleepingHydra(center);
        ((CraftWorld) world).getHandle().addFreshEntity(sleepingHydra, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Subcommand("boss test hydra restorerage")
    private void testHydraRestoreRage(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;

        CataclysmBoss manager = Cataclysm.getBoss();
        if (manager == null) {
            ChatMessenger.sendStaffMessage(player, "No hay un jefe activo.");
            return;
        }

        if (!(manager instanceof CalamityHydra hydra)) {
            ChatMessenger.sendStaffMessage(player, "El jefe no es la Calamity Hydra.");
            return;
        }

        hydra.rage.reset();
    }
}