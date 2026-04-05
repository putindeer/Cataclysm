package org.cataclysm.game.mob.custom.vanilla.skeleton.standard;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class ArcaneSkeleton extends CataclysmMob {

    public ArcaneSkeleton(Level level) {
        super(new ArcaneSkeletonEntity(level), "Arcane Skeleton", "#ba8f49", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        SkeletonArmors.setArcaneArmor(this);
        super.setListener(new ArcaneSkeletonListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).setGlint(true).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArcaneSkeleton(super.getLevel());
    }

    static class ArcaneSkeletonEntity extends AbstractCustomSkeleton {
        public ArcaneSkeletonEntity(Level level) {
            super(level);
        }
    }

    static class ArcaneSkeletonListener implements Listener {
        private final @NotNull ArcaneSkeleton arcaneSkeleton;

        public ArcaneSkeletonListener(@NotNull ArcaneSkeleton arcaneSkeleton) {
            this.arcaneSkeleton = arcaneSkeleton;
        }

        @EventHandler
        public void onProjectileHit(ProjectileHitEvent event) {
            Projectile projectile = event.getEntity();
            if (!(projectile instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.arcaneSkeleton.getMobToken().key())) return;

            Location location = projectile.getLocation();
            location.createExplosion(shooter, Cataclysm.getDay() < 21 ? 3f : 4f);
            if (!arrow.isDead()) arrow.remove();
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.arcaneSkeleton.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
