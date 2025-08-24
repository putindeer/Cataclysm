package org.cataclysm.game.mob.custom.vanilla.ghast;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

public class ElderGhast extends CataclysmMob {

    public ElderGhast(Level level) {
        super(new ElderGhastEntity(level), "Elder Ghast", level);
        super.setHealth(50);
        super.setListener(new ElderGhastListener(this));
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(ItemStack.of(Material.GHAST_TEAR), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ElderGhast(super.getLevel());
    }

    static class ElderGhastEntity extends Ghast {
        public ElderGhastEntity(Level level) {
            super(EntityType.GHAST, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class ElderGhastListener implements Listener {

        private final ElderGhast elderGhast;

        public ElderGhastListener(ElderGhast elderGhast) {
            this.elderGhast = elderGhast;
        }

        @EventHandler
        public void fireBallDamagePlayer(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (!(event.getDamager() instanceof Fireball fireball)) return;
            if (!(fireball.getShooter() instanceof org.bukkit.entity.LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.elderGhast.getMobToken().key())) return;

            PotionEffectType[] types = {PotionEffectType.WITHER, PotionEffectType.BLINDNESS, PotionEffectType.SLOWNESS, PotionEffectType.MINING_FATIGUE, PotionEffectType.NAUSEA};
            for (PotionEffectType type : types) {
                player.addPotionEffect(new PotionEffect(type, 600, ((type == PotionEffectType.WITHER || type == PotionEffectType.SLOWNESS) ? 2 : 0)));
            }
            if (Cataclysm.getDay() >= 21) player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 600, 0));
        }
    }
}
