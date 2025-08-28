package org.cataclysm.game.pantheon.level;

import org.bukkit.*;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.entrance.EntranceMob;
import org.cataclysm.game.world.generator.VoidGenerator;

public class LevelHandler {
    private static final String worldID = "world_pantheon";
    private final PantheonOfCataclysm pantheon;

    public LevelHandler(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    public void setUpEntrance(Location location) {
        SchematicLoader loader = new SchematicLoader("pantheon/schematics/entrance.schem");
        loader.pasteSchematic(location, true);

        EntranceMob entranceMob = new EntranceMob(((CraftWorld) location.getWorld()).getHandle());
        entranceMob.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.COMMAND);
        entranceMob.startTickTask();
    }

    public void setUpWorld() {
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
        }
        this.pantheon.world = world;
    }
}
