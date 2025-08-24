package org.cataclysm.game.mob.custom.vanilla.enhanced;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlackSpider extends CataclysmMob {
    public BlackSpider(Level level) {
        super(new BlackSpiderEntity(level), "Black Spider", "#262626", level);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.3);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 24);
        super.setListener(new BlackSpiderListener(this));
    }

    static class BlackSpiderEntity extends Spider {
        public BlackSpiderEntity(Level level) {
            super(EntityType.SPIDER, level);
            super.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class BlackSpiderListener implements Listener {
        private final BlackSpider spider;

        public BlackSpiderListener(BlackSpider spider) {
            this.spider = spider;
        }

        @EventHandler
        public void onAttack(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof LivingEntity damager)) return;

            CataclysmToken token = CataclysmMob.getToken(damager);
            if (token == null || !token.key().equals(this.spider.getMobToken().key())) return;

            if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) return;

            var random = ThreadLocalRandom.current();
            if (random.nextInt(100) < 33) {
                var playerBlock = player.getLocation().getBlock();
                if (playerBlock.getType() != Material.COBWEB) playerBlock.setType(Material.COBWEB);
                else {
                    BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
                    playerBlock.getRelative(faces[random.nextInt(faces.length)]).setType(Material.COBWEB);
                }
            }
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            org.bukkit.entity.LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.spider.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new BlackSpider(super.getLevel());
    }
}
