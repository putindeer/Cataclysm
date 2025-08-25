package org.cataclysm.game.mob.custom.dungeon.temple;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.MobUtils;
import org.cataclysm.game.effect.MortemEffect;
import org.cataclysm.game.effect.PaleCorrosionEffect;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class Sentinel extends CataclysmMob {

    public Sentinel(@NotNull Level level) {
        super(new SentinelEntity(level), "Sentinel", level);
        super.setListener(new SentinelListener(this));

        super.setHealth(20);
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).addEnchant(Enchantment.POWER, 9).buildAsNMS());
        super.setItem(EquipmentSlot.OFFHAND, new ItemBuilder(Material.TIPPED_ARROW).setPotion(PotionType.HARMING).buildAsNMS());
    }

    static class SentinelEntity extends Skeleton {
        public SentinelEntity(Level level) {
            super(EntityType.SKELETON, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(2, new RestrictSunGoal(this));
            this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
            this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0, 1.2));
            this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
            this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
            this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
        }
    }

    static class SentinelListener implements Listener {
        private final @NotNull Sentinel sentinel;

        public SentinelListener(@NotNull Sentinel sentinel) {
            this.sentinel = sentinel;
        }

        @EventHandler
        private void onChangeTarget(EntityTargetLivingEntityEvent event) {
            if (!(event.getEntity() instanceof org.bukkit.entity.LivingEntity livingEntity)) return;

            var token = CataclysmMob.getToken(livingEntity);
            if (token == null) return;

            if (!token.key().equals(this.sentinel.getMobToken().key())) return;

            if (!(event.getTarget() instanceof org.bukkit.entity.Player)) return;

            if (!MobUtils.hasNearbyPlayer(livingEntity, 20, 3, 20)) return;

            PersistentData.set(livingEntity, "hasTracked", PersistentDataType.BOOLEAN, true);
        }

        @EventHandler
        public void onAttack(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Arrow damager)) return;
            if (!(damager.getShooter() instanceof org.bukkit.entity.LivingEntity shooter)) return;

            int day = Cataclysm.getDay();
            if (day < 7) return;
            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.sentinel.getMobToken().key())) return;

            if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) return;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
            if (player.getWorld() == Dimensions.PALE_VOID.getWorld()) {
                player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 100, 0));
                player.addPotionEffect(new PotionEffect(PaleCorrosionEffect.EFFECT_TYPE, 100, 0));
            }
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            if (!(event.getEntity() instanceof LivingEntity entity)) return;

            CataclysmToken token = CataclysmMob.getToken(entity);
            if (token == null || !token.key().equals(this.sentinel.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Sentinel(super.getLevel());
    }
}