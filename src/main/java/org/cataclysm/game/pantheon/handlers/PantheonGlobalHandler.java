package org.cataclysm.game.pantheon.handlers;

import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.pantheon.world.PantheonLocations;
import org.cataclysm.game.pantheon.world.PantheonEntranceMob;
import org.cataclysm.game.world.generator.VoidGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PantheonGlobalHandler {
    public static void setUp(boolean setUp) {
        Location entranceLoc = PantheonLocations.PANTHEON_ENTRANCE.getCoreLocation();
        double radius = PantheonLocations.PANTHEON_ENTRANCE.getArea().radius();

        if (setUp) setUpEntrance(entranceLoc, false);
        else removeEntranceEntity(entranceLoc, radius);
    }
    public static void registerAll() {
        registerTasks();
    }
    public static void unregisterAll() {
        Cataclysm.getPantheon().getService().shutdownNow();
    }

    public static void registerTasks() {
        ScheduledExecutorService service = Cataclysm.getPantheon().getService();
        service.scheduleAtFixedRate(PantheonTaskHandler::tickPlayerTask, 0, 1, TimeUnit.SECONDS);
    }

    public static World getOrCreateWorld() {
        String worldID = "world_pantheon";

        World world = Bukkit.getWorld(worldID);
        if (world == null) {
            WorldCreator wc = new WorldCreator(worldID);
            wc.generator(new VoidGenerator());
            wc.environment(World.Environment.NORMAL);
            world = wc.createWorld();
        }
        if (world != null) {
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }

        return world;
    }

    private static void setUpEntrance(Location location, boolean pasteSchematic) {
        if (pasteSchematic) {
            new SchematicLoader("pantheon/schematics/entrance.schem").pasteSchematic(location, true);
        }
        PantheonEntranceMob pantheonEntranceMob = new PantheonEntranceMob(((CraftWorld) location.getWorld()).getHandle());
        pantheonEntranceMob.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.COMMAND);
    }
    private static void removeEntranceEntity(@NotNull Location location, double radius) {
        location.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            String data = PersistentData.get(entity, "CUSTOM", PersistentDataType.STRING);
            if (!(entity instanceof ArmorStand) || data == null || !data.equals("pantheon_entrance")) return;
            entity.remove();
        });
    }
}
