package org.cataclysm.game.mob.custom.vanilla.ghast;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.jetbrains.annotations.NotNull;

public class AtomicGhast extends CataclysmMob {

    public AtomicGhast(Level level) {
        super(new ObsidianGhastEntity(level), "Atomic Ghast", level);
        super.setHealth(50);
        super.setAttribute(Attributes.SCALE, 1.8);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(ItemStack.of(Material.GHAST_TEAR), 1, 1, 1))));
    }

    static class ObsidianGhastEntity extends Ghast {
        public ObsidianGhastEntity(Level level) {
            super(EntityType.GHAST, level);

            int day = Cataclysm.getDay();
            int explosionPower = day < 14 ? 7 : 12;
            super.setExplosionPower(explosionPower);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new AtomicGhast(super.getLevel());
    }
}
