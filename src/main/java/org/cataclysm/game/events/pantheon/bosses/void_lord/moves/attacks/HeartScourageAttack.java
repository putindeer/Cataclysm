package org.cataclysm.game.events.pantheon.bosses.void_lord.moves.attacks;

import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAttack;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartScourageAttack extends HeartAttack {
    public HeartScourageAttack(VoidLord lord) {
        super(lord, Material.NETHERITE_SWORD, "Heart Scourage", 1, 3);
    }

    @Override
    public void channel() {
        this.lord.getController().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (this.channelTime * 20) + 20,  3));
        this.lord.playSound(Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 4F, 0.5F);
        this.lord.playSound(Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 4F, 1.5F);
        this.lord.playSound(Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 4F, 1.25F);
    }

    @Override
    public void cast() {
        Location start = this.lord.getController().getEyeLocation();
        Vector direction = start.getDirection().normalize();

        ScheduledExecutorService service = this.lord.getThread().getService();
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    Vector offset = direction.clone().multiply(((finalI + 3) * 1.25F));
                    Location location = start.clone().add(offset);
                    this.castDamageSphere(location, 3, 40);
                    this.lord.playSound(Sound.ITEM_TRIDENT_THUNDER, 3F, (float) (1.15F + (finalI * .1)));
                });
            }, 150 * i, TimeUnit.MILLISECONDS);
        }
    }

    private void castDamageSphere(Location location, int radius, double damage) {
        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.SWEEP_ATTACK, radius, radius * 4);
        handler.sphere(Particle.END_ROD, radius - 1, radius * 3);

        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1F, 1.15F);
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 2F, 1.05F);
        location.getWorld().playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 2F, 0.65F);

        location.getNearbyLivingEntities(radius, radius, radius).forEach(livingEntity -> {
            if (livingEntity.equals(this.lord.getController())) return;

            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2F, 0.95F);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));

            livingEntity.damage(damage);
        });
    }
}
