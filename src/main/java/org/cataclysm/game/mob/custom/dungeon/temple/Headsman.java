package org.cataclysm.game.mob.custom.dungeon.temple;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.MobUtils;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class Headsman extends CataclysmMob {
    public Headsman(Level level) {
        super(new HeadsmanEntity(level), "Headsman", level);
        super.setItem(EquipmentSlot.MAINHAND, Items.IRON_AXE);
        super.setHealth(20);
        super.setAttribute(Attributes.SCALE, 1.3f);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 42);
        super.amplifyAttribute(Attributes.MOVEMENT_SPEED, 1.17f);
    }

    static class HeadsmanEntity extends Vindicator {
        public HeadsmanEntity(Level level) {
            super(EntityType.VINDICATOR, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(0, new FloatGoal(this));
            this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Creaking.class, 8.0F, 1.0, 1.2));
            this.goalSelector.addGoal(3, new AbstractIllager.RaiderOpenDoorGoal(this));
            this.goalSelector.addGoal(4, new Raider.HoldGroundAttackGoal(this, 10.0F));
            this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, false));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
            this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
            this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
            this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        }
    }

    static class HeadsmanListener implements Listener {
        private final Headsman headsman;

        public HeadsmanListener(Headsman headsman) {
            this.headsman = headsman;
        }

        @EventHandler
        private void onChangeTarget(EntityTargetLivingEntityEvent event) {
            if (!(event.getEntity() instanceof org.bukkit.entity.LivingEntity livingEntity)) return;

            var token = CataclysmMob.getToken(livingEntity);
            if (token == null) return;

            if (!token.key().equals(this.headsman.getMobToken().key())) return;

            if (!(event.getTarget() instanceof org.bukkit.entity.Player)) return;

            if (!MobUtils.hasNearbyPlayer(livingEntity, 20, 3, 20)) return;

            PersistentData.set(livingEntity, "hasTracked", PersistentDataType.BOOLEAN, true);
        }

        @EventHandler
        public void onAttack(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof LivingEntity damager)) return;

            CataclysmToken token = CataclysmMob.getToken(damager);
            if (token == null || !token.key().equals(this.headsman.getMobToken().key())) return;

            if (damager.getWorld() != Dimensions.PALE_VOID.createWorld()) return;
            event.setDamage(event.getFinalDamage() * 2);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.LivingEntity entity)) return;

            CataclysmToken token = CataclysmMob.getToken(entity);
            if (token == null || !token.key().equals(this.headsman.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Headsman(super.getLevel());
    }
}
