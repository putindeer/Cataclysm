package org.cataclysm.game.mob.custom.vanilla.phantom;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class ToxicTerror extends CataclysmMob {
    public ToxicTerror(Level level) {
        super(new PhantomTerrorEntity(EntityType.PHANTOM, level), "Toxic Terror", "#c4c4c4", level);
        super.setHealth(80);
        super.setAttribute(Attributes.SCALE, 7);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 20);

        if (this.getBukkitLivingEntity().getWorld().getEnvironment() == World.Environment.NETHER) {
            super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TOXIC_MEMBRANE.build(), 1, 1, 1))));
        }
    }

    public static class PhantomTerrorEntity extends Phantom {
        private final HashMap<UUID, Integer> ticks;
        public PhantomTerrorEntity(EntityType<? extends Phantom> entityType, Level level) {
            super(entityType, level);
            this.ticks = new HashMap<>();
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity source) {
            var livingEntity = this.getTarget();
            if (livingEntity == null) return false;

            var cle = livingEntity.getBukkitLivingEntity();
            cle.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0));
            this.startGnomificate(cle);
            return super.doHurtTarget(level, source);
        }

        public void startGnomificate(LivingEntity livingEntity) {
            var target = this.getTarget();
            Bukkit.getConsoleSender().sendMessage("Start Gnomificate Target: " + (target == null));
            if (target == null) return;
            this.gnomificate(target.getBukkitLivingEntity(), true);
            this.tick(livingEntity.getUniqueId());
        }

        public void gnomificate(@NotNull LivingEntity livingEntity, boolean gnomificate) {
            var scale = livingEntity.getAttribute(Attribute.SCALE);
            Bukkit.getConsoleSender().sendMessage("Start Gnomificate Scale: " + (scale == null));
            if (scale == null) return;
            if (gnomificate) {
                setTimeLeft(livingEntity, 10 + getTimeLeft(livingEntity));
                scale.setBaseValue(scale.getValue() / 2);
                livingEntity.playSound(Sound.sound(Key.key("block.vault.open_shutter"), Sound.Source.HOSTILE, 0.75F, 1.25F));
                livingEntity.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.HOSTILE, 0.65F, 1.55F));
            } else {
                scale.setBaseValue(1);
                livingEntity.playSound(Sound.sound(Key.key("block.vault.open_shutter"), Sound.Source.HOSTILE, 0.75F, 1.75F));
            }
        }

        public void tick(UUID uuid) {
            if (!this.ticks.containsKey(uuid)) {
                Bukkit.getConsoleSender().sendMessage("Started ticking");
                var tickId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
                    var entity = Bukkit.getEntity(uuid);

                    if (!(entity instanceof LivingEntity livingEntity)) return;

                    if (entity instanceof org.bukkit.entity.Player player && !player.isOnline()) return;

                    livingEntity.sendActionBar(Component.text("" + getTimeLeft(livingEntity)));

                    if (getTimeLeft(livingEntity) == 0) {
                        this.gnomificate(livingEntity, false);
                        Bukkit.getScheduler().cancelTask(this.ticks.get(uuid));
                        this.ticks.remove(uuid);
                    } else {
                        setTimeLeft(livingEntity, getTimeLeft(livingEntity) - 1);
                    }
                }, 0, 20);

                this.ticks.put(uuid, tickId);
            }
        }

        public static void setTimeLeft(LivingEntity target, int time) {
            PersistentData.set(target, "gnomification", PersistentDataType.INTEGER, time);
        }

        public static int getTimeLeft(LivingEntity target) {
            var timeLeft = PersistentData.get(target, "gnomification", PersistentDataType.INTEGER);
            return timeLeft == null ? 0 : timeLeft;
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ToxicTerror(super.getLevel());
    }

}
