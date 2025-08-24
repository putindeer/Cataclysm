package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class  MirageEye extends CataclysmMob {
    public MirageEye(Level level) {
        super(new MirageEyeEntity(level), "Mirage Eye", CataclysmColor.MIRAGE, level);
        this.setHealth(5);
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 10);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.MIRAGE_EYEBALL.build(), 1, 1, .5))));

    }

    @Override
    public CataclysmMob createInstance() {
        return new MirageEye(super.getLevel());
    }

    static class MirageEyeEntity extends Phantom {
        public MirageEyeEntity(Level level) {
            super(EntityType.PHANTOM, level);
        }

        @Override
        public void tick() {
            super.tick();
            updatePhantomTarget();
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity source) {
            var entity = source.getBukkitEntity();
            var damager = this.getBukkitLivingEntity();
            var direction = damager.getLocation().getDirection();
            if (entity instanceof Player player && !player.isBlocking()) {
                player.setJumping(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1));

                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    if (POSITIVE_EFFECTS.contains(effect.getType())) {
                        player.removePotionEffect(effect.getType());
                    }
                }

                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.knockback(3.25, direction.multiply(-1).getX(), direction.multiply(-1).getZ()), 1L);
            }

            return super.doHurtTarget(level, source);
        }

        public void updatePhantomTarget() {
            net.minecraft.world.entity.player.Player nearestPlayer = this.level().getNearestPlayer(
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    64.0D,
                    false
            );

            if (nearestPlayer != null) {
                this.setTarget(nearestPlayer, EntityTargetEvent.TargetReason.CUSTOM);
            }
        }

        private static final Set<PotionEffectType> POSITIVE_EFFECTS = Set.of(
                PotionEffectType.ABSORPTION,
                PotionEffectType.CONDUIT_POWER,
                PotionEffectType.DOLPHINS_GRACE,
                PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.HERO_OF_THE_VILLAGE,
                PotionEffectType.INVISIBILITY,
                PotionEffectType.JUMP_BOOST,
                PotionEffectType.NIGHT_VISION,
                PotionEffectType.REGENERATION,
                PotionEffectType.RESISTANCE,
                PotionEffectType.SATURATION,
                PotionEffectType.SLOW_FALLING,
                PotionEffectType.SPEED,
                PotionEffectType.STRENGTH,
                PotionEffectType.WATER_BREATHING
        );
    }

}

