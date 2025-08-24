package org.cataclysm.game.mob.custom.cataclysm;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.cataclysm.game.mob.utils.TeleportUtils;
import org.jetbrains.annotations.NotNull;

public class QuantumReactor extends CataclysmMob {

    public QuantumReactor(Level level) {
        super(new QuantumReactorEntity(level), "Quantum Reactor", "#87ffe3", level);
        super.setHealth(5);
        super.setAttribute(Attributes.SCALE, 1.25);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.38);
    }

    static class QuantumReactorEntity extends Creeper {
        private static int TELEPORT_COOLDOWN_SECONDS = 300; // 5 minutes in seconds
        private final BukkitTask teleportTask;

        public QuantumReactorEntity(Level level) {
            super(EntityType.CREEPER, level);
            CraftCreeper creeper = ((CraftCreeper) this.getBukkitLivingEntity());
            creeper.setExplosionRadius(25);
            creeper.setMaxFuseTicks(15);
            creeper.setFuseTicks(15);
            creeper.setPowered(true);
            creeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false));
            MobUtils.setGlowingColor(creeper, NamedTextColor.AQUA);
            if (Cataclysm.getRagnarok() != null && Cataclysm.getRagnarok().getData().getLevel() >= 9) TELEPORT_COOLDOWN_SECONDS = 150;

            // Schedule repeating teleport task
            this.teleportTask = Bukkit.getScheduler().runTaskTimer(
                    Cataclysm.getInstance(), this::teleportCreeper,
                    TELEPORT_COOLDOWN_SECONDS * 20L, // Initial delay (5 minutes)
                    TELEPORT_COOLDOWN_SECONDS * 20L  // Period (5 minutes)
            );
        }

        @Override
        protected void registerGoals() {
            if (Cataclysm.getDay() >= 21) {
                this.goalSelector.addGoal(1, new FloatGoal(this));
                this.goalSelector.addGoal(2, new SwellGoal(this));
                this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
                this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
                this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
                this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
                this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
                this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
            } else super.registerGoals();
        }

        public void teleportCreeper() {
            if (this.isDeadOrDying()) {
                if (teleportTask != null && !teleportTask.isCancelled()) {
                    teleportTask.cancel();
                }
                return;
            }

            int teleportRadius = 100;

            if (this.getBukkitLivingEntity().getTicksLived() > 20 * (60 * 17)) this.remove(RemovalReason.DISCARDED);

            var ragnarok = Cataclysm.getRagnarok();
            if (ragnarok != null) {
                var level = ragnarok.getData().getLevel();
                if (level >= 9) {
                    teleportRadius = 128;
                    this.persist = true;
                    this.setPersistenceRequired(true);
                    this.getBukkitLivingEntity().setRemoveWhenFarAway(false);
                    this.getBukkitLivingEntity().setPersistent(true);
                }
            }

            TeleportUtils.teleportEntityNearPlayer(this.getBukkitLivingEntity(), teleportRadius, 4, 8);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_FALL)) return false;

            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
                this.getBukkitLivingEntity().removePotionEffect(PotionEffectType.GLOWING);
                this.getBukkitLivingEntity().removePotionEffect(PotionEffectType.SLOWNESS);
                teleportCreeper();
                return false;
            }

            if (this.random.nextBoolean()) teleportCreeper();
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void remove(@NotNull RemovalReason reason) {
            if (teleportTask != null && !teleportTask.isCancelled()) {
                teleportTask.cancel();
            }
            super.remove(reason);
        }

    }

    @Override
    protected CataclysmMob createInstance() {
        return new QuantumReactor(super.getLevel());
    }
}
