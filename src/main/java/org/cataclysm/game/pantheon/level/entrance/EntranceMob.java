package org.cataclysm.game.pantheon.level.entrance;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class EntranceMob extends CataclysmMob {
    public EntranceMob(Level level) {
        super(new EntranceEntity(level), "Entrance Mob", CataclysmColor.PALE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setPersistentDataString("CUSTOM", "pantheon_entrance");
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
        return new EntranceMob(super.getLevel());
    }
}