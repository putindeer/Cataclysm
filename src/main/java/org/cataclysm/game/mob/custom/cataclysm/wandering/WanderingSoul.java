package org.cataclysm.game.mob.custom.cataclysm.wandering;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class WanderingSoul extends CataclysmMob {

    public WanderingSoul(Level level) {
        super(new WanderingSoulEntity(level), "Wandering Soul", level);
        super.setHealth(750);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.WANDERING_SOUL.build(), 1, 1, 0.2))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new WanderingSoul(super.getLevel());
    }

    static class WanderingSoulEntity extends WitherBoss {

        public WanderingSoulEntity(Level level) {
            super(EntityType.WITHER, level);
            var livingEntity = this.getBukkitLivingEntity();
            MobUtils.scaleBoost(livingEntity, 2);
        }

        @Override
        protected void registerGoals() {
            super.registerGoals();
            this.goalSelector.addGoal(0, new WanderingSoulAttacksGoal(this));
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_MACE_SMASH)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class WanderingSoulAttacksGoal extends Goal {
        private final WanderingSoulEntity wanderingSoul;
        private int cooldown = 20 * 5;

        public WanderingSoulAttacksGoal(WanderingSoulEntity wanderingSoul) {
            this.wanderingSoul = wanderingSoul;
        }

        @Override
        public boolean canUse() {
            return this.wanderingSoul.getBukkitLivingEntity().isValid() && this.wanderingSoul.getTarget() != null;
        }

        @Override
        public void tick() {
            Bukkit.getConsoleSender().sendMessage("Cooldown: " + cooldown);
            cooldown--;

            var bukkitEntity = this.wanderingSoul.getBukkitLivingEntity();
            if (cooldown == 0) {
                cooldown = 20 * 10;
                this.wanderingSoul.setNoAi(true);
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    bukkitEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 110, 0, true, true));
                    bukkitEntity.getNearbyEntities(15, 15, 15).forEach(entity -> {
                        if (entity instanceof Player player) {
                            bukkitEntity.setGlowing(true);
                            Sound[] sounds = {Sound.ENTITY_ELDER_GUARDIAN_DEATH, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, Sound.ITEM_MACE_SMASH_GROUND_HEAVY};
                            for (var sound : sounds) player.playSound(bukkitEntity.getLocation(), sound, 1.5F, 0.55F);

                            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                                this.wanderingSoul.setNoAi(false);

                                bukkitEntity.setGlowing(false);
                            }, 100);
                        }
                    });
                }, 1L);


            } else if (cooldown % 200 == 0) {
                var target = this.wanderingSoul.getTarget();
                if (target == null) return;

                var projectile = bukkitEntity.launchProjectile(org.bukkit.entity.WitherSkull.class);
                var direction = target.getBukkitLivingEntity().getLocation().toVector().subtract(bukkitEntity.getLocation().toVector()).normalize().multiply(0.5);
                projectile.setVelocity(direction);
                projectile.setGlowing(true);
            }
        }
    }

}
