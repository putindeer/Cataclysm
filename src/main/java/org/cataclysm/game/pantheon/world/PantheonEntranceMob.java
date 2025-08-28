package org.cataclysm.game.pantheon.world;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class PantheonEntranceMob extends CataclysmMob {
    public PantheonEntranceMob(Level level) {
        super(new EntranceEntity(level), "Entrance Mob", CataclysmColor.PALE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setPersistentDataString("CUSTOM", "pantheon_entrance");
        super.setAttribute(Attributes.SCALE, 3);
        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
    }

    static class EntranceEntity extends ArmorStand {
        public EntranceEntity(@NotNull Level level) {
            super(EntityType.ARMOR_STAND, level);
            super.setInvulnerable(true);
            super.setSilent(true);
            super.setInvisible(true);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new PantheonEntranceMob(super.getLevel());
    }
}