package org.cataclysm.game.mob.custom.vanilla.skeleton.bogged;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.SkeletonArmors;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class ArbalistBogged extends CataclysmMob {

    public ArbalistBogged(Level level) {
        super(new ArbalistBoggedEntity(level), "Arbalist Bogged", "#662d2d", level);
        SkeletonArmors.setArbalistArmor(this);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        super.setListener(new ArbalistBoggedListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArbalistBogged(super.getLevel());
    }

    static class ArbalistBoggedEntity extends AbstractCustomBogged {
        public ArbalistBoggedEntity(Level level) {
            super(level);
        }
    }

    static class ArbalistBoggedListener implements Listener {
        private final @NotNull ArbalistBogged arbalistBogged;

        public ArbalistBoggedListener(@NotNull ArbalistBogged arbalistBogged) {
            this.arbalistBogged = arbalistBogged;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.arbalistBogged.getMobToken().key())) return;

            event.setDamage(Cataclysm.getDay() < 21 ? 45 : 90);
            if (event.getEntity() instanceof Player player) player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.arbalistBogged.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
