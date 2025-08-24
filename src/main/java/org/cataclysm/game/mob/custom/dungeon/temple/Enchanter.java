package org.cataclysm.game.mob.custom.dungeon.temple;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class Enchanter extends CataclysmMob {
    public static final Color BOOST_CLOUD_COLOR = Color.fromRGB(202, 99, 85);
    public static final float BOOST_RADIUS = 4F;

    public Enchanter(Level level) {
        super(new EnchanterEntity(level), "Enchanter", level);
        super.setSpawnTag(SpawnTag.PERSISTENT);

        super.setHealth(40);
        super.setDrops(new CataclysmDrops(
                new LootContainer(
                        new LootHolder(new ItemStack(Material.DIAMOND), 1, 3, 1),
                        new LootHolder(new ItemStack(Material.GOLDEN_APPLE), 1, 3, 1),
                        new LootHolder(new ItemStack(Material.TOTEM_OF_UNDYING), 1, 1, 0.5)
                )
        ));

        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.getBukkitLivingEntity().setPersistent(true);
        super.setPregenerationMaterial(Material.PURPLE_GLAZED_TERRACOTTA);
    }

    static class EnchanterEntity extends Evoker {
        public EnchanterEntity(Level level) {
            super(EntityType.EVOKER, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(1, new FloatGoal(this));
            this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6, 1.0));
            this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Creaking.class, 8.0F, 0.6, 1.0));
            this.goalSelector.addGoal(3, new PathfindToRaidGoal<>(this));
            this.goalSelector.addGoal(4, new EnchanterBoostSpellGoal(this));
            this.goalSelector.addGoal(6, new PatrollingMonster.LongDistancePatrolGoal<>(this, 0.7, 0.595));
            this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
            this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
            this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        }

        class EnchanterBoostSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
            private final EnchanterEntity enchanterEntity;

            public EnchanterBoostSpellGoal(EnchanterEntity enchanterEntity) {
                this.enchanterEntity = enchanterEntity;
            }

            @Override
            public void start() {
                super.start();
                this.enchanterEntity.level().playSound(null, this.enchanterEntity.blockPosition(), SoundEvents.EVOKER_PREPARE_WOLOLO, this.enchanterEntity.getSoundSource(), 2F, 0.75F);
                this.enchanterEntity.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 2));
            }

            @Override
            public void tick() {
                if (!MobUtils.isEntityInCloudWithColor(this.enchanterEntity.getBukkitLivingEntity(), BOOST_RADIUS, BOOST_CLOUD_COLOR)) {
                    MobUtils.spawnColoredCloud(BOOST_RADIUS, 150, BOOST_CLOUD_COLOR, this.enchanterEntity.getBukkitLivingEntity().getLocation());
                }
            }

            @Override
            public void stop() {
                super.stop();
                this.enchanterEntity.setIsCastingSpell(IllagerSpell.NONE);
            }

            @Override
            protected void performSpellCasting() {}

            @Override
            protected int getCastingTime() {
                return 100;
            }

            @Override
            protected int getCastingInterval() {
                return 250;
            }

            @Override
            protected SoundEvent getSpellPrepareSound() {
                return SoundEvents.EVOKER_PREPARE_SUMMON;
            }

            @Override
            protected @NotNull IllagerSpell getSpell() {
                return IllagerSpell.WOLOLO;
            }
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Enchanter(super.getLevel());
    }
}
