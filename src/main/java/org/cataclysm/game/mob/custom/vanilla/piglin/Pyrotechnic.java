package org.cataclysm.game.mob.custom.vanilla.piglin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class Pyrotechnic extends CataclysmMob {

    public Pyrotechnic(Level level) {
        super(new PyrotechnicEntity(level), "Piglin Pyrotechnic", "#767b82", level);
        super.setListener(new PyrotechnicListener(this));
        super.setItem(EquipmentSlot.MAINHAND,
                new ItemBuilder(Material.CROSSBOW)
                        .addEnchant(Enchantment.MULTISHOT, 2)
                        .addEnchant(Enchantment.POWER, 30)
                        .setUnbreakable(true)
                        .buildAsNMS());
        super.setDrops(null);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Pyrotechnic(super.getLevel());
    }

    static class PyrotechnicEntity extends Piglin {
        public PyrotechnicEntity(@NotNull Level level) {
            super(EntityType.PIGLIN, level);
            cannotHunt = true;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean canPickUpLoot() {
            return false;
        }

        @Override
        public boolean wantsToPickUp(@NotNull ServerLevel level, @NotNull ItemStack stack) {
            return false;
        }

        @Override
        public void readAdditionalSaveData(@NotNull CompoundTag compound) {
            super.readAdditionalSaveData(compound);
            this.allowedBarterItems = new java.util.HashSet<>();
            this.interestItems = new java.util.HashSet<>();
        }
    }

    static class PyrotechnicListener implements Listener {
        private final @NotNull Pyrotechnic pyrotechnic;

        public PyrotechnicListener(@NotNull Pyrotechnic pyrotechnic) {
            this.pyrotechnic = pyrotechnic;
        }

        @EventHandler
        public void onShoot(ProjectileLaunchEvent event) {
            if (!(event.getEntity() instanceof Arrow arrow)) return;
            if (!(arrow.getShooter() instanceof org.bukkit.entity.Piglin shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.pyrotechnic.getMobToken().key())) return;

            shooter.getWorld().spawn(shooter.getLocation(), Firework.class, fireworkEntity -> {
                var meta = fireworkEntity.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.RED, Color.ORANGE, Color.YELLOW)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .build());
                fireworkEntity.setFireworkMeta(meta);
                fireworkEntity.setShooter(shooter);
                fireworkEntity.setVelocity(arrow.getVelocity());
            });

            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), arrow::remove, 1L);
        }

        @EventHandler
        public void onFireworkExplode(FireworkExplodeEvent event) {
            if (!(event.getEntity().getShooter() instanceof Piglin)) return;
            var firework = event.getEntity();
            firework.getLocation().getNearbyPlayers(4).forEach(firework::hitEntity);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.pyrotechnic.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
