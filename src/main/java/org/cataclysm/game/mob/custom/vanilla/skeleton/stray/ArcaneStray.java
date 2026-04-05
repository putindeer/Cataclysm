package org.cataclysm.game.mob.custom.vanilla.skeleton.stray;

import net.minecraft.world.entity.EquipmentSlot;
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
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.SkeletonArmors;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class ArcaneStray extends CataclysmMob {

    public ArcaneStray(Level level) {
        super(new ArcaneStrayEntity(level), "Arcane Stray", "#ba8f49", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        SkeletonArmors.setArcaneArmor(this);
        super.setListener(new ArcaneStrayListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArcaneStray(super.getLevel());
    }

    static class ArcaneStrayEntity extends AbstractCustomStray {
        public ArcaneStrayEntity(Level level) {
            super(level);
        }
    }

    static class ArcaneStrayListener implements Listener {
        private final @NotNull ArcaneStray arcaneStray;

        public ArcaneStrayListener(@NotNull ArcaneStray arcaneStray) {
            this.arcaneStray = arcaneStray;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity entity = event.getDamager();
            if (!(entity instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.arcaneStray.getMobToken().key())) return;

            if (!(event.getEntity() instanceof Player player)) return;

            CataclystStray.CataclystStrayListener.applyHealthDebuffs(player);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.arcaneStray.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
