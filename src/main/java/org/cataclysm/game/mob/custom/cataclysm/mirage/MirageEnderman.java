package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class MirageEnderman extends CataclysmMob {
    public MirageEnderman(Level level) {
        super(new MirageEndermanEntity(level), "Mirage Enderman", CataclysmColor.MIRAGE, level);
        super.setHealth(120);
        super.setAttribute(Attributes.SCALE, 1.5);
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 6);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.MIRAGE_PEARL.build(), 1, 1, .5))));

    }

    @Override
    protected CataclysmMob createInstance() {
        return new MirageEnderman(super.getLevel());
    }

    static class MirageEndermanEntity extends EnderMan {
        public MirageEndermanEntity(Level level) {
            super(EntityType.ENDERMAN, level);
            this.setPathfindingMalus(PathType.OPEN, 0.0F);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean isSensitiveToWater() {
            return false;
        }

        @Override
        public void registerGoals() {
            super.registerGoals();
            this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 1));
            for (var availableGoal : this.goalSelector.getAvailableGoals()) {
                if (availableGoal == null) continue;
                var className = availableGoal.getGoal().getClass().getName();
                if (className.contains("WaterAvoiding")) this.goalSelector.removeGoal(availableGoal.getGoal());
                if (className.contains("BlockGoal")) this.goalSelector.removeGoal(availableGoal.getGoal());

            }
        }

    }

}
