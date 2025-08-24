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
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.SkeletonArmors;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ArcaneBogged extends CataclysmMob {

    public ArcaneBogged(Level level) {
        super(new ArcaneBoggedEntity(level), "Arcane Bogged", "#ba8f49", level);
        super.setHealth(Cataclysm.getDay() < 21 ? 60 : 120);
        SkeletonArmors.setArcaneArmor(this);
        super.setListener(new ArcaneBoggedListener(this));
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).buildAsNMS());
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArcaneBogged(super.getLevel());
    }

    static class ArcaneBoggedEntity extends AbstractCustomBogged {
        public ArcaneBoggedEntity(Level level) {
            super(level);
        }
    }

    static class ArcaneBoggedListener implements Listener {
        private final @NotNull ArcaneBogged arcaneBogged;

        public ArcaneBoggedListener(@NotNull ArcaneBogged arcaneBogged) {
            this.arcaneBogged = arcaneBogged;
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (!(damager instanceof Arrow arrow && arrow.getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.arcaneBogged.getMobToken().key())) return;

            if (Cataclysm.getDay() >= 21) event.setDamage(event.getDamage() * 5);
            if (event.getEntity() instanceof Player player) {
                var itemArray = player.getInventory().getStorageContents();
                var itemList = new ArrayList<>(Arrays.stream(itemArray).toList());

                Collections.shuffle(itemList);
                ItemStack[] newContents = itemList.toArray(new ItemStack[0]);
                player.getInventory().setStorageContents(newContents);
            }
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.arcaneBogged.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
