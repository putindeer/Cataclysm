package org.cataclysm.game.pantheon.level.entrance;

import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.pantheon.level.PantheonLevels;

import java.util.Collection;

public class EntranceManager {
    private static final PantheonLevels entrance = PantheonLevels.PANTHEON_ENTRANCE;

    public static void setUp(boolean toggle) {
        handleEntity(toggle);
    }

    private static void handleEntity(boolean spawn) {
        Location location = entrance.getCoreLocation();
        World world = location.getWorld();

        if (spawn) {
            Level level = ((CraftWorld) world).getHandle();
            EntranceMob entranceMob = new EntranceMob(level);
            entranceMob.addFreshEntity(location);
        } else {
            Collection<Entity> ne = location.getNearbyEntities(5, 5, 5);
            for (Entity entity : ne) if (isEntrance(entity)) entity.remove();
        }
    }

    private static boolean isEntrance(Entity entity) {
        String data = PersistentData.get(entity, "CUSTOM", PersistentDataType.STRING);
        return data != null && data.equalsIgnoreCase("pantheon_entrance");
    }
}
