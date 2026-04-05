package org.cataclysm.game.mob.custom.vanilla.skeleton.bogged;

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

public class WarlockBogged extends CataclysmMob {

    public WarlockBogged(Level level) {
        super(new WarlockBoggedEntity(level), "Warlock Bogged", "#47663d", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        SkeletonArmors.setWarlockArmor(this);
        super.setListener(new WarlockBoggedListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new WarlockBogged(super.getLevel());
    }

    static class WarlockBoggedEntity extends AbstractCustomBogged {
        public WarlockBoggedEntity(Level level) {
            super(level);
        }
    }

    static class WarlockBoggedListener implements Listener {
        private final @NotNull WarlockBogged warlockBogged;

        public WarlockBoggedListener(@NotNull WarlockBogged warlockBogged) {
            this.warlockBogged = warlockBogged;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.warlockBogged.getMobToken().key())) return;

            if (event.getEntity() instanceof Player player) {
                int day = Cataclysm.getDay();
                int effectDuration = day < 21 ? 200 : 400;
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, effectDuration, 2));
                if (day >= 21) player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, effectDuration, 2));
            }
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.warlockBogged.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
