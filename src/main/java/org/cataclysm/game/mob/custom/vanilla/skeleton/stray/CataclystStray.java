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
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class CataclystStray extends CataclysmMob {

    public CataclystStray(Level level) {
        super(new CataclystStrayEntity(level), "Cataclyst Stray", "#ad9663", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        super.setListener(new CataclystStrayListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.CATACLYST_BONE.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CataclystStray(super.getLevel());
    }

    static class CataclystStrayEntity extends AbstractCustomStray {
        public CataclystStrayEntity(Level level) {
            super(level);
        }
    }

    public static class CataclystStrayListener implements Listener {
        private final @NotNull CataclystStray cataclystStray;

        public CataclystStrayListener(@NotNull CataclystStray cataclystStray) {
            this.cataclystStray = cataclystStray;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.cataclystStray.getMobToken().key())) return;

            int day = Cataclysm.getDay();
            event.setDamage(day < 21 ? 45 : 90);
            if (!(event.getEntity() instanceof Player player)) return;
            applyHealthDebuffs(player);
        }

        public static void applyHealthDebuffs(Player player) {
            if (!PersistentData.has(player, "ARCANE_STRAY_HEALTH_DEBUFF", PersistentDataType.DOUBLE)) {
                PersistentData.set(player, "ARCANE_STRAY_HEALTH_DEBUFF", PersistentDataType.DOUBLE, 2.0d);
            } else {
                var healthDebuff = PersistentData.get(player, "ARCANE_STRAY_HEALTH_DEBUFF", PersistentDataType.DOUBLE);
                if (healthDebuff != null) PersistentData.set(player, "ARCANE_STRAY_HEALTH_DEBUFF", PersistentDataType.DOUBLE, healthDebuff + 2.0d);
            }

            PersistentData.set(player, "ARCANE_STRAY_HEALTH_DEBUFF_TIMER", PersistentDataType.INTEGER, 10);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.cataclystStray.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
