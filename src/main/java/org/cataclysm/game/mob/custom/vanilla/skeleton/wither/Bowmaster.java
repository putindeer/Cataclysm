package org.cataclysm.game.mob.custom.vanilla.skeleton.wither;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class Bowmaster extends CataclysmMob {
    public Bowmaster(Level level) {
        super(new BowmasterEntity(level), "Bowmaster", level);
        super.setHealth(60);
        super.setListener(new BowmasterListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).addEnchant(Enchantment.POWER, 20).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Bowmaster(super.getLevel());
    }

    static class BowmasterEntity extends WitherSkeleton {
        public BowmasterEntity(Level level) {
            super(EntityType.WITHER_SKELETON, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class BowmasterListener implements Listener {
        private final @NotNull Bowmaster bowmaster;

        public BowmasterListener(@NotNull Bowmaster bowmaster) {
            this.bowmaster = bowmaster;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.bowmaster.getMobToken().key())) return;

            if (Cataclysm.getDay() >= 21) event.setDamage(event.getDamage() * 2);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.bowmaster.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
