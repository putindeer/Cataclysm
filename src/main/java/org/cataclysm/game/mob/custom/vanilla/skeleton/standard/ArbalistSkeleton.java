package org.cataclysm.game.mob.custom.vanilla.skeleton.standard;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
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

public class ArbalistSkeleton extends CataclysmMob {

    public ArbalistSkeleton(Level level) {
        super(new ArbalistSkeletonEntity(level), "Arbalist Skeleton", "#662d2d", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        SkeletonArmors.setArbalistArmor(this);
        super.setListener(new ArbalistSkeletonListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArbalistSkeleton(super.getLevel());
    }

    static class ArbalistSkeletonEntity extends AbstractCustomSkeleton {
        public ArbalistSkeletonEntity(Level level) {
            super(level);
        }
    }

    static class ArbalistSkeletonListener implements Listener {
        private final @NotNull ArbalistSkeleton arbalistSkeleton;

        public ArbalistSkeletonListener(@NotNull ArbalistSkeleton arbalistSkeleton) {
            this.arbalistSkeleton = arbalistSkeleton;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.arbalistSkeleton.getMobToken().key())) return;

            event.setDamage(Cataclysm.getDay() < 21 ? 45 : 90);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.arbalistSkeleton.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
