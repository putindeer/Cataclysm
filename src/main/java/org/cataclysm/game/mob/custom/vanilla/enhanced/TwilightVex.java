package org.cataclysm.game.mob.custom.vanilla.enhanced;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwilightVex extends CataclysmMob {
    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public TwilightVex(Level level) {
        super(new TwilightVexEntity(level), "Twilight Vex", "#c7c7c7", level);
        super.setHealth(16);
        super.setAttribute(Attributes.SCALE, 1.4F);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 1.3F);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 10);
        super.setItem(EquipmentSlot.MAINHAND, Items.IRON_SWORD);
        super.setListener(new TwilightVexListener(this));
    }

    static class TwilightVexEntity extends Vex {

        public TwilightVexEntity(Level level) {
            super(EntityType.VEX, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class TwilightVexListener implements Listener {
        private final @NotNull TwilightVex vex;

        public TwilightVexListener(@NotNull TwilightVex vex) {
            this.vex = vex;
        }

        @EventHandler
        public void onSpawn(EntitySpawnEvent event) {
            if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.vex.getMobToken().key())) return;

            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    if (livingEntity.isValid()) livingEntity.setHealth(0);
                });
            }, 50, TimeUnit.SECONDS);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            org.bukkit.entity.LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.vex.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new TwilightVex(super.getLevel());
    }
}
