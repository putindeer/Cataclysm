package org.cataclysm.game.mob.custom.cataclysm.mirage;

import me.libraryaddict.disguise.DisguiseAPI;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class MirageCreeper extends CataclysmMob {
    public MirageCreeper(Level level) {
        super(new MirageCreeperEntity(level), "Mirage Disguise", CataclysmColor.MIRAGE, level);
        super.setHealth(20);
        super.setAttribute(Attributes.SCALE, 1.5);
        super.amplifyAttribute(Attributes.MOVEMENT_SPEED, 1.5);
        super.setListener(new MirageCreeperListener(this));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new MirageCreeper(super.getLevel());
    }

    static class MirageCreeperEntity extends Creeper {
        public MirageCreeperEntity(Level level) {
            super(EntityType.CREEPER, level);
            var creeper = ((CraftCreeper) this.getBukkitLivingEntity());
            creeper.setExplosionRadius(12);
            creeper.setMaxFuseTicks(25);
            creeper.setFuseTicks(25);

            MirageBeast beast = new MirageBeast(this.level());
            beast.addFreshEntity(creeper.getLocation());
            beast.getBukkitLivingEntity().customName(creeper.customName());
            DisguiseAPI.disguiseToAll(creeper, DisguiseAPI.constructDisguise(beast.getBukkitLivingEntity()));
            beast.getBukkitLivingEntity().remove();
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class MirageCreeperListener implements Listener {
        private final @NotNull MirageCreeper creeper;

        public MirageCreeperListener(@NotNull MirageCreeper creeper) {
            this.creeper = creeper;
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();
            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.creeper.getMobToken().key())) return;
            event.getDrops().clear();
            if (this.creeper.getEntity().random.nextBoolean()) livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(), CataclysmItems.MIRAGE_POWDER.build());
            HandlerList.unregisterAll(this);
        }

    }

}
