package org.cataclysm.game.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.pantheon.level.LevelHandler;
import org.cataclysm.game.pantheon.level.PantheonAreas;
import org.cataclysm.game.pantheon.level.entrance.EntranceMob;
import org.cataclysm.game.pantheon.listeners.PantheonPlayerListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PantheonHandler {
    private static final List<Listener> listeners = List.of(
            new PantheonPlayerListener()
    );

    public static void setUp(boolean setUp) {
        Location entranceLoc = PantheonAreas.PANTHEON_ENTRANCE.getCoreLocation();
        double radius = PantheonAreas.PANTHEON_ENTRANCE.getArea().radius();

        if (setUp) setUpEntrance(entranceLoc);
        else removeEntranceEntity(entranceLoc, radius);
    }
    public static void registerAll() {
        registerListeners();
        registerTasks();
    }
    public static void unregisterAll() {
        Cataclysm.getPantheon().getService().shutdownNow();
        listeners.forEach(HandlerList::unregisterAll);
    }

    public static void registerTasks() {
        ScheduledExecutorService service = Cataclysm.getPantheon().getService();
        service.scheduleAtFixedRate(PantheonTasks::tickPlayerTask, 0, 1, TimeUnit.SECONDS);
        service.scheduleAtFixedRate(PantheonTasks::tickEntranceParticles, 0, 1, TimeUnit.SECONDS);
    }
    public static void registerListeners() {
        listeners.forEach(listener ->
                Cataclysm.getInstance().getServer().getPluginManager().registerEvents(listener, Cataclysm.getInstance()));
    }

    private static void setUpEntrance(Location location) {
        SchematicLoader loader = new SchematicLoader("pantheon/schematics/entrance.schem");
        loader.pasteSchematic(location, true);

        EntranceMob entranceMob = new EntranceMob(((CraftWorld) location.getWorld()).getHandle());
        entranceMob.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.COMMAND);
    }
    private static void removeEntranceEntity(@NotNull Location location, double radius) {
        location.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            String data = PersistentData.get(entity, "CUSTOM", PersistentDataType.STRING);
            if (!(entity instanceof ArmorStand) || data == null || !data.equals("pantheon_entrance")) return;
            entity.remove();
        });
    }
}
