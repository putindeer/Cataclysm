package org.cataclysm.game.mob.custom.vanilla.skeleton.standard;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class CataclystSkeleton extends CataclysmMob {

    public CataclystSkeleton(Level level) {
        super(new CataclystSkeletonEntity(level), "Cataclyst Skeleton", "#ad9663", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        super.setListener(new CataclystSkeletonListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.CATACLYST_BONE.build(), 1, 1, 1))));

    }

    @Override
    protected CataclysmMob createInstance() {
        return new CataclystSkeleton(super.getLevel());
    }

    static class CataclystSkeletonEntity extends AbstractCustomSkeleton {
        public CataclystSkeletonEntity(Level level) {
            super(level);
        }
    }

    static class CataclystSkeletonListener implements Listener {
        private final @NotNull CataclystSkeleton cataclystSkeleton;

        public CataclystSkeletonListener(@NotNull CataclystSkeleton cataclystSkeleton) {
            this.cataclystSkeleton = cataclystSkeleton;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.cataclystSkeleton.getMobToken().key())) return;

            int day = Cataclysm.getDay();
            event.setDamage(day < 21 ? 45 : 90);
            if (event.getEntity() instanceof Player player) player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, (day < 21 ? 3 : 5) * 20, 0));
        }

        @EventHandler
        public void onProjectileHit(ProjectileHitEvent event) {
            Projectile projectile = event.getEntity();
            if (!(projectile instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.cataclystSkeleton.getMobToken().key())) return;

            Location location = projectile.getLocation();
            location.createExplosion(shooter, Cataclysm.getDay() < 21 ? 3f : 4f);
            if (!arrow.isDead()) arrow.remove();
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.cataclystSkeleton.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
