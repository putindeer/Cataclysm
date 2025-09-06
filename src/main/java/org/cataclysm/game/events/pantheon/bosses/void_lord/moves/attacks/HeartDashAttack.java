package org.cataclysm.game.events.pantheon.bosses.void_lord.moves.attacks;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAttack;

import java.util.UUID;

public class HeartDashAttack extends HeartAttack {
    public HeartDashAttack(VoidLord lord) {
        super(lord, Material.SUGAR, "Heart Dash", 1, 1.5);
    }

    @Override
    public void channel() {
        Player controller = super.lord.getController();

        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0));
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 9));

        controller.playSound(controller.getLocation(), Sound.ITEM_TRIDENT_RETURN, 4F, 1.5F);
        controller.playSound(controller.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 4F, 1.5F);

        this.lord.updateModel(EntityType.CREEPER, this.lord.getModelPrefix() + "-dash");
    }

    @Override
    public void cast() {
        Player controller = this.lord.getController();
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 30, 0));
        this.castDash(9);
    }

    public void castDash(double power) {
        Player controller = this.lord.getController();
        World world = controller.getWorld();

        Vector direction = controller.getLocation().getDirection();
        direction.normalize().multiply(power);

        Vector velocity = controller.getVelocity();
        velocity.setX(direction.getX());
        velocity.setZ(direction.getZ());
        velocity.setY(0.3);

        controller.setVelocity(velocity);

        UUID uuid = UUID.randomUUID();
        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
            Location location = controller.getLocation().clone().add(0, 1, 0);

            location.getNearbyLivingEntities(2).forEach(livingEntity -> {
                if (livingEntity.equals(this.lord.getController())) return;
                this.lord.damage(livingEntity, 40);
            });

            world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1F, 1.2F);
            world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1F, 1.5F);
            world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, .51F, 1.5F);
            world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, .51F, 1.8F);

            world.spawnParticle(Particle.SWEEP_ATTACK, location, 5, .5, .5, .5, 0, null, true);
            world.spawnParticle(Particle.END_ROD, location, 2, 0, 0, 0, 0, null, true);

            if (controller.getLocation().clone().add(0, -.01, 0).getBlock().isSolid()) {
                Bukkit.getScheduler().cancelTask(Cataclysm.getTasks().get(uuid));
                this.lord.updateModel(EntityType.CREEPER, this.lord.getModelPrefix() + "-normal");
            }
        }, 2, 1);
        Cataclysm.getTasks().put(uuid, task);
    }
}
