package org.cataclysm.game.mob.custom.vanilla.ghast;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class WraithGhast extends CataclysmMob {

    public WraithGhast(Level level) {
        super(new WraithGhastEntity(level), "Wraith Ghast", level);
        super.setHealth(Cataclysm.getDay() < 14 ? 50 : 100);
        super.setListener(new WraithGhastListener(this));
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(ItemStack.of(Material.GHAST_TEAR), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new WraithGhast(super.getLevel());
    }

    static class WraithGhastEntity extends Ghast {
        public WraithGhastEntity(Level level) {
            super(EntityType.GHAST, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
    
    static class WraithGhastListener implements Listener {

        private final WraithGhast wraithGhast;

        public WraithGhastListener(WraithGhast wraithGhast) {
            this.wraithGhast = wraithGhast;
        }

        @EventHandler
        public void onHitFireball(ProjectileHitEvent event) {
            if (!(event.getEntity().getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.wraithGhast.getMobToken().key())) return;

            Location hitLocation = event.getEntity().getLocation();
            AreaEffectCloud effectCloud = (AreaEffectCloud) hitLocation.getWorld().spawnEntity(hitLocation, org.bukkit.entity.EntityType.AREA_EFFECT_CLOUD);
            effectCloud.setSource(shooter);
            effectCloud.setRadius(effectCloud.getRadius() * 2);

            int day = Cataclysm.getDay();
            int damageAmplifier = day < 21 ? day < 14 ? 2 : 3 : 6;
            effectCloud.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 200, damageAmplifier), true);
            effectCloud.setDuration(200);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.wraithGhast.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
