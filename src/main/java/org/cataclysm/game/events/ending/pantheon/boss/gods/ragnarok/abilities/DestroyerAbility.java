package org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.abilities;

import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.TheRagnarok;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DestroyerAbility extends RagnarokAbility {
    public DestroyerAbility(TheRagnarok ragnarok) {
        super(ragnarok, Material.WIND_CHARGE, "Destroyer", 0);
    }

    @Override
    public void cast() {
        Location start = ragnarok.getController().getEyeLocation();
        Vector direction = start.getDirection().normalize();

        ScheduledExecutorService service = ragnarok.getThread().getService();
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    Vector offset = direction.clone().multiply(((finalI + 3) * 1.25F));
                    Location location = start.clone().add(offset);
                    this.castDamageSphere(location, 3, 100);
                });
            }, 150 * i, TimeUnit.MILLISECONDS);
        }
    }

    private void castDamageSphere(Location location, int radius, double damage) {
        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.SWEEP_ATTACK, radius, radius * 4);
        handler.sphere(Particle.END_ROD, radius - 1, radius * 3);

        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1F, 1.15F);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2F, 1.05F);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.65F);

        location.getNearbyLivingEntities(radius, radius, radius).forEach(livingEntity -> {
            if (livingEntity.equals(ragnarok.getController())) return;

            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2F, 0.95F);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));

            ragnarok.damage(livingEntity, damage);
        });
    }
}
