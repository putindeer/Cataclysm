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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.SkeletonArmors;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class WarlockStray extends CataclysmMob {

    public WarlockStray(Level level) {
        super(new WarlockStrayEntity(level), "Warlock Stray", "#47663d", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        SkeletonArmors.setWarlockArmor(this);
        super.setListener(new WarlockStrayListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new WarlockStray(super.getLevel());
    }

    static class WarlockStrayEntity extends AbstractCustomStray {
        public WarlockStrayEntity(Level level) {
            super(level);
        }
    }

    static class WarlockStrayListener implements Listener {
        private final @NotNull WarlockStray warlockStray;

        public WarlockStrayListener(@NotNull WarlockStray warlockStray) {
            this.warlockStray = warlockStray;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.warlockStray.getMobToken().key())) return;

            if (event.getEntity() instanceof Player player) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (Cataclysm.getDay() < 21 ? 3 : 6) * 20, 6));
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.warlockStray.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
