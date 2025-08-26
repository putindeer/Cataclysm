package org.cataclysm.game.mob.custom.cataclysm.wandering;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class WanderingFaith extends CataclysmMob {
    public WanderingFaith(Level level) {
        super(new WanderingFaithEntity(level), "Wandering Faith", level);
        super.setHealth(1000);
        super.amplifyAttribute(Attributes.SCALE, 3);
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 100);
        super.amplifyAttribute(Attributes.MOVEMENT_SPEED, 1.5);
        super.amplifyAttribute(Attributes.FOLLOW_RANGE, 10);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.WANDERING_HEART.build(), 1, 1, 0.1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new WanderingFaith(super.getLevel());
    }

    static class WanderingFaithEntity extends Ravager {
        public WanderingFaithEntity(Level level) {
            super(EntityType.RAVAGER, level);

            var bukkitEntity = this.getBukkitLivingEntity();
            bukkitEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 2, false, false));

            DisguiseConfig.setPlayerNameType(DisguiseConfig.PlayerNameType.VANILLA);
            var sniffer = bukkitEntity.getWorld().spawnEntity(bukkitEntity.getLocation(), org.bukkit.entity.EntityType.SNIFFER, CreatureSpawnEvent.SpawnReason.CUSTOM);
            sniffer.customName(MiniMessage.miniMessage().deserialize("Wandering Faith"));
            sniffer.setCustomNameVisible(false);
            DisguiseAPI.disguiseToAll(bukkitEntity, DisguiseAPI.constructDisguise(sniffer));
            sniffer.remove();
        }

        @Override
        protected void registerGoals() {
            super.registerGoals();
            this.goalSelector.addGoal(0, new WanderingFaithCurseGoal(this));
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_MACE_SMASH)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    static class WanderingFaithCurseGoal extends Goal {

        private final WanderingFaithEntity wanderingFaith;
        public WanderingFaithCurseGoal(@NotNull WanderingFaithEntity wanderingFaith) {
            this.wanderingFaith = wanderingFaith;
        }

        @Override
        public boolean canUse() {
            return !this.wanderingFaith.getBukkitLivingEntity().getNearbyEntities(25, 25, 25).isEmpty();
        }

        @Override
        public void tick() {
            var bukkitEntity = this.wanderingFaith.getBukkitLivingEntity();
            var nearbyEntities = bukkitEntity.getNearbyEntities(25, 25, 25);
            for (var entity : nearbyEntities) {
                if (entity instanceof org.bukkit.entity.Player player) {
                    player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 20, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 1));
                }
            }
        }
    }
}
