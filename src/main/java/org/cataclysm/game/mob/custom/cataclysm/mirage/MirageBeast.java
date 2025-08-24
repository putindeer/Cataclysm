package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class MirageBeast extends CataclysmMob {
    public MirageBeast(Level level) {
        super(new MirageBeastEntity(level), "Mirage Beast", CataclysmColor.MIRAGE, level);
        super.setHealth(110);
        super.setAttribute(Attributes.SCALE, 1.5);
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 9.75);
        super.amplifyAttribute(Attributes.MOVEMENT_SPEED, 1.5);
        super.setItem(EquipmentSlot.OFFHAND, new ItemBuilder(Material.AIR).buildAsNMS());
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.AIR).buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.MIRAGE_FLESH.build(), 1, 1, .5))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new MirageBeast(super.getLevel());
    }

    static class MirageBeastEntity extends Zombie {
        public MirageBeastEntity(Level level) {
            super(EntityType.ZOMBIE, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity source) {
            var entity = source.getBukkitEntity();
            var damager = this.getBukkitLivingEntity();
            var direction = damager.getLocation().getDirection();
            if (entity instanceof Player player && !player.isBlocking()) {
                player.setJumping(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1));
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.knockback(3.25, direction.multiply(-1).getX(), direction.multiply(-1).getZ()), 1L);

            }

            return super.doHurtTarget(level, source);
        }
    }
}
