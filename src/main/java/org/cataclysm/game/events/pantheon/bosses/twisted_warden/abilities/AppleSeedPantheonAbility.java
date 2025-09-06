package org.cataclysm.game.events.pantheon.bosses.twisted_warden.abilities;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.twisted_warden.PantheonWarden;

import java.util.concurrent.TimeUnit;

public class AppleSeedPantheonAbility extends PantheonAbility {
    @Getter private static final double EXPLOSION_RADIUS = 40.0;
    private static final int CAST_DAMAGE = 200;
    private static final int CAST_EFFECT_DURATION = 200;

    private final PantheonWarden warden;

    public AppleSeedPantheonAbility(PantheonWarden warden) {
        super(Material.TNT_MINECART, "Apple Seed", 3);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = warden.getController();
        int durationTicks = channelTime * 20;

        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 4, true, true, true));
        controller.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durationTicks, 0, true, true, true));

        for (int i = 0; i < channelTime; i++) {
            warden.getThread().getService().schedule(() -> {
                var location = controller.getLocation();
                warden.playAlarmSound(location, 10F);

                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    var handler = new ParticleHandler(location);
                    handler.sphere(Particle.END_ROD, EXPLOSION_RADIUS, EXPLOSION_RADIUS * 3);
                    location.getWorld().strikeLightningEffect(location);

                    warden.getNearbyLivingEntities(location, EXPLOSION_RADIUS).forEach(entity ->
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 0, true, true))
                    );
                });
            }, i, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cast() {
        Location location = warden.getController().getLocation();
        World world = location.getWorld();

        // efecto principal
        world.playSound(location, "cataclysm.pantheon.radiance", 10F, 0.78F);
        world.spawnParticle(
                Particle.EXPLOSION_EMITTER,
                location,
                (int) (EXPLOSION_RADIUS * 5),
                EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS,
                0, null, true
        );

        // aplicar efectos a entidades
        warden.getNearbyLivingEntities(location, EXPLOSION_RADIUS).forEach(entity -> {
            warden.damage(entity, CAST_DAMAGE);

            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, CAST_EFFECT_DURATION, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, CAST_EFFECT_DURATION, 2));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, CAST_EFFECT_DURATION, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, CAST_EFFECT_DURATION, 0));

            Location entityLoc = entity.getLocation();
            world.playSound(entityLoc, Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.65F);
            world.playSound(entityLoc, Sound.ENTITY_WARDEN_SONIC_BOOM, 2F, 0.65F);
            world.playSound(entityLoc, Sound.ENTITY_WARDEN_SONIC_BOOM, 2F, 0.55F);
            world.playSound(entityLoc, Sound.ENTITY_GHAST_SCREAM, 1F, 0.55F);
        });
    }
}
