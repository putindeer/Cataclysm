package org.cataclysm.game.mob.custom.dungeon.temple;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.mob.utils.MobUtils;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.global.utils.security.CataclysmToken;

public class Fishmoth extends CataclysmMob {

    public Fishmoth(Level level) {
        super(new Silverfish(EntityType.SILVERFISH, level), "Fishmoth", level);
        super.setListener(new SilverfishListener(this));
        super.setHealth(16);
        super.amplifyAttribute(Attributes.SCALE, 1.5);
        super.amplifyAttribute(Attributes.MOVEMENT_SPEED, 2);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 7.5);
        if (Cataclysm.getDay() >= 7) MobUtils.letalBoost(this.getBukkitLivingEntity(), 3);
    }

    static class SilverfishListener implements Listener {
        private final Fishmoth fishmoth;

        public SilverfishListener(Fishmoth silverfish) {
            this.fishmoth = silverfish;
        }

        @EventHandler
        private void onChangeTarget(EntityTargetLivingEntityEvent event) {
            if (!(event.getEntity() instanceof org.bukkit.entity.LivingEntity livingEntity)) return;

            var token = CataclysmMob.getToken(livingEntity);
            if (token == null) return;

            if (!token.key().equals(this.fishmoth.getMobToken().key())) return;

            if (!(event.getTarget() instanceof org.bukkit.entity.Player)) return;

            if (!org.cataclysm.api.mob.MobUtils.hasNearbyPlayer(livingEntity, 20, 3, 20)) return;

            PersistentData.set(livingEntity, "hasTracked", PersistentDataType.BOOLEAN, true);
        }

        @EventHandler
        public void onAttack(EntityDamageByEntityEvent event) {
            if (Cataclysm.getDay() < 28) return;
            if (!(event.getDamager() instanceof LivingEntity damager)) return;

            CataclysmToken token = CataclysmMob.getToken(damager);
            if (token == null || !token.key().equals(this.fishmoth.getMobToken().key())) return;

            if (damager.getWorld() != Dimensions.PALE_VOID.createWorld()) return;
            if (!(event.getEntity() instanceof Player player)) return;

            player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 100, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.LivingEntity entity)) return;

            CataclysmToken token = CataclysmMob.getToken(entity);
            if (token == null || !token.key().equals(this.fishmoth.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Fishmoth(super.getLevel());
    }
}
