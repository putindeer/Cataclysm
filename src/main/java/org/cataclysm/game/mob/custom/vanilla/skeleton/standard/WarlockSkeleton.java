package org.cataclysm.game.mob.custom.vanilla.skeleton.standard;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class WarlockSkeleton extends CataclysmMob {

    public WarlockSkeleton(Level level) {
        super(new WarlockSkeletonEntity(level), "Warlock Skeleton", "#47663d", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        SkeletonArmors.setWarlockArmor(this);
        super.setListener(new WarlockSkeletonListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new WarlockSkeleton(super.getLevel());
    }

    static class WarlockSkeletonEntity extends AbstractCustomSkeleton {
        public WarlockSkeletonEntity(Level level) {
            super(level);
        }
    }

    static class WarlockSkeletonListener implements Listener {
        private final @NotNull WarlockSkeleton warlockSkeleton;

        public WarlockSkeletonListener(@NotNull WarlockSkeleton warlockSkeleton) {
            this.warlockSkeleton = warlockSkeleton;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.warlockSkeleton.getMobToken().key())) return;

            if (event.getEntity() instanceof Player player) player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, (Cataclysm.getDay() < 21 ? 3 : 5) * 20, 0));
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.warlockSkeleton.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
