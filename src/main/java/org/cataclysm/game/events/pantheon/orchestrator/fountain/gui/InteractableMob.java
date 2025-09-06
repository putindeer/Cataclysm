package org.cataclysm.game.events.pantheon.orchestrator.fountain.gui;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;

import java.util.Collection;

public class InteractableMob extends CataclysmMob {
    private static final String GENERIC_ID = "ENTRANCE";

    public InteractableMob(Level level) {
        super(new EntranceEntity(level), "Entrance Mob", CataclysmColor.PALE, level);
        super.setPersistentDataString("CUSTOM", GENERIC_ID);
        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setAttribute(Attributes.SCALE, 3);
    }

    @Override
    protected CataclysmMob createInstance() {return new InteractableMob(super.getLevel());}

    static class EntranceEntity extends ArmorStand {
        public EntranceEntity(Level level) {
            super(EntityType.ARMOR_STAND, level);
            super.setInvulnerable(true);
            super.setSilent(true);
            super.setInvisible(true);
        }
    }

    public static boolean isEntrance(Entity entity) {
        String data = PersistentData.get(entity, "CUSTOM", PersistentDataType.STRING);
        return data != null && data.equalsIgnoreCase(GENERIC_ID);
    }

    public static void handleExistance(Location location, boolean exist) {
        if (exist) spawn(location);
        else remove(location);
    }

    protected static void spawn(Location location) {
        Level level = ((CraftWorld) location.getWorld()).getHandle();
        InteractableMob entranceMob = new InteractableMob(level);
        entranceMob.addFreshEntity(location);
    }

    protected static void remove(Location location) {
        Collection<Entity> entities = location.getNearbyEntities(5, 5, 5);
        for (Entity entity : entities) if (isEntrance(entity)) entity.remove();
    }
}