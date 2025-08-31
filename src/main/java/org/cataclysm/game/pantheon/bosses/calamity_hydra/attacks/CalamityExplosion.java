package org.cataclysm.game.pantheon.bosses.calamity_hydra.attacks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Ravager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.mob.utils.EffectUtils;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.PantheonHydra;

public class CalamityExplosion {
    public enum Type {SMALL, MACRO, MAGIC}

    private final PantheonHydra hydra;

    public CalamityExplosion(PantheonHydra hydra) {
        this.hydra = hydra;
    }

    public void create(Location location, double power, Type type) {
        World world = this.hydra.getLocation().getWorld();

        ParticleHandler handler = new ParticleHandler(location);
        int steps = (int) (power/2);

        switch (type) {
            case SMALL -> {
                world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 0, 0, 0, 0, 1);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, (float) (0.1 * power), 1.25F);
            }
            case MACRO -> {
                handler.sphere(Particle.EXPLOSION_EMITTER, power, steps);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, (float) power / 2, 1.15F);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, (float) power / 2, 0.75F);
            }
            case MAGIC -> {
                handler.sphere(Particle.EXPLOSION_EMITTER, power, steps);
                handler.sphere(Particle.DRIPPING_LAVA, power + 1.5, steps * 5);
                handler.sphere(Particle.FLAME, power + 1.5, steps * 5);

                location.getNearbyLivingEntities(power).forEach(livingEntity -> {
                    if (livingEntity.equals(this.hydra.getController()) || livingEntity instanceof Ravager) return;
                    EffectUtils.removePossitiveEffects(livingEntity);
                    world.playSound(livingEntity, Sound.ITEM_SHIELD_BREAK, 2F, 0.55F);
                    world.playSound(livingEntity, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 2F, 1.5F);
                });

                world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 3F, 0.55F);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 3F, 0.85F);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 3F, 1.45F);
            }
        }

        location.getNearbyLivingEntities(power).forEach(livingEntity -> {
            if (livingEntity.equals(this.hydra.getController()) || livingEntity instanceof Ravager) return;
            livingEntity.damage(power * (7 + this.hydra.heads), this.hydra.getController());
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
            world.playSound(livingEntity, Sound.ENTITY_RAVAGER_STUNNED, 1F, 0.95F);
        });
    }
}
