package org.cataclysm.game.mob.custom.dungeon.monolith;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TricksterKillerBunny extends CataclysmMob {
    private final @Nullable Trickster trickster;

    public TricksterKillerBunny(Level level, @Nullable Trickster trickster) {
        super(new KillerBunnyEntity(level), "The Killer Bunny", level);
        this.trickster = trickster;
        super.setListener(new KillerBunnyListener(this));
        if (Cataclysm.getDay() >= 21) super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 3);
    }

    static class KillerBunnyEntity extends Rabbit {

        public KillerBunnyEntity(Level level) {
            super(EntityType.RABBIT, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class KillerBunnyListener implements Listener {
        private final TricksterKillerBunny bunny;

        public KillerBunnyListener(TricksterKillerBunny bunny) {
            this.bunny = bunny;
        }

        @EventHandler
        public void onSpawn(EntitySpawnEvent event) {
            if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.bunny.getMobToken().key())) return;

            ((org.bukkit.entity.Rabbit) livingEntity).setRabbitType(org.bukkit.entity.Rabbit.Type.THE_KILLER_BUNNY);

            var trickster = this.bunny.trickster;
            if (trickster != null) trickster.setBunniesGenerated(trickster.getBunniesGenerated() + 1);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            org.bukkit.entity.LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.bunny.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return null;
    }
}
