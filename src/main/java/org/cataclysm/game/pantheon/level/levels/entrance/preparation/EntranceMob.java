package org.cataclysm.game.pantheon.level.levels.entrance.preparation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.pantheon.level.levels.entrance.PantheonEntrance;

import java.util.Collection;

public class EntranceMob extends CataclysmMob {
    public EntranceMob(Level level) {
        super(new EntranceEntity(level), "Entrance Mob", CataclysmColor.PALE, level);
        super.setPersistentDataString("CUSTOM", "pantheon_entrance");
        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setAttribute(Attributes.SCALE, 3);
    }

    static class EntranceEntity extends ArmorStand {
        public EntranceEntity(Level level) {
            super(EntityType.ARMOR_STAND, level);
            super.setInvulnerable(true);
            super.setSilent(true);
            super.setInvisible(true);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new EntranceMob(super.getLevel());
    }

    public static void handle(boolean spawn) {
        Location location = PantheonEntrance.getLocation();
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

    public static boolean isEntrance(Entity entity) {
        String data = PersistentData.get(entity, "CUSTOM", PersistentDataType.STRING);
        return data != null && data.equalsIgnoreCase("pantheon_entrance");
    }
}
