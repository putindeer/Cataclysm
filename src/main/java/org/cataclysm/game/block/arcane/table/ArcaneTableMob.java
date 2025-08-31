package org.cataclysm.game.block.arcane.table;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class ArcaneTableMob extends CataclysmMob {
    public ArcaneTableMob(Level level) {
        super(new ArcaneTableEntity(level), "Arcane Table", CataclysmColor.ARCANE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);

        super.setPersistentDataString("CUSTOM", "arcane_table");

        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setPregenerationMaterial(Material.BEDROCK);
    }

    static class ArcaneTableEntity extends ArmorStand {
        public ArcaneTableEntity(@NotNull Level level) {
            super(EntityType.ARMOR_STAND, level);
            super.setInvulnerable(true);
            super.setSilent(true);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArcaneTableMob(super.getLevel());
    }
}
