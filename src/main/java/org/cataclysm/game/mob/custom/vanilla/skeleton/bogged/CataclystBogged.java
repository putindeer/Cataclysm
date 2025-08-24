package org.cataclysm.game.mob.custom.vanilla.skeleton.bogged;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Bogged;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.custom.vanilla.skeleton.stray.CataclystStray;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CataclystBogged extends CataclysmMob {

    public CataclystBogged(Level level) {
        super(new CataclystBoggedEntity(level), "Cataclyst Bogged", "#ad9663", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        super.setListener(new CataclystBoggedListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.CATACLYST_BONE.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CataclystBogged(super.getLevel());
    }

    static class CataclystBoggedEntity extends AbstractCustomBogged {
        public CataclystBoggedEntity(Level level) {
            super(level);
        }
    }

    static class CataclystBoggedListener implements Listener {
        private final @NotNull CataclystBogged cataclystBogged;

        public CataclystBoggedListener(@NotNull CataclystBogged cataclystBogged) {
            this.cataclystBogged = cataclystBogged;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.cataclystBogged.getMobToken().key())) return;

            event.setDamage(Cataclysm.getDay() < 21 ? 45 : event.getDamage() * 10);
            if (event.getEntity() instanceof Player player) {
                int day = Cataclysm.getDay();
                int effectDuration = day < 21 ? 200 : 400;
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, effectDuration, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, effectDuration, 2));
                if (day >= 21) player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, effectDuration, 2));

                var itemArray = player.getInventory().getStorageContents();
                var itemList = new ArrayList<>(Arrays.stream(itemArray).toList());

                Collections.shuffle(itemList);
                ItemStack[] newContents = itemList.toArray(new ItemStack[0]);
                player.getInventory().setStorageContents(newContents);
                CataclystStray.CataclystStrayListener.applyHealthDebuffs(player);

            }
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.cataclystBogged.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
