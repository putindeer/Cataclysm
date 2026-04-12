package org.cataclysm.game.mob.listener.spawn;

import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;

import java.util.List;
import java.util.SplittableRandom;

@Registrable
public class CreatureSpawnHandler implements Listener {

    private final VanillaMobTransformer vanillaTransformer = new VanillaMobTransformer();
    private final CustomMobSpawner customSpawner = new CustomMobSpawner();
    private final BiomeBasedSpawner biomeSpawner = new BiomeBasedSpawner();
    private final SpecialMobTransformer mobTransformer = new SpecialMobTransformer();
    private final SplittableRandom random = new SplittableRandom();

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (Cataclysm.getDay() >= 7) {
            event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        }
        // Skip custom/spawner spawns
        if (!event.getEntity().getType().equals(EntityType.BLAZE) && getSkippableSpawnReasons().contains(event.getSpawnReason())) return;

        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT)) {
            event.getEntity().remove();
            return;
        }

        LivingEntity entity = event.getEntity();
        if (getSkippedTypes().contains(entity.getType())) return;

        Location location = entity.getLocation();
        int day = Cataclysm.getDay();
        if (entity.getWorld().getEnvironment() == World.Environment.NETHER) {
            if (day >= 21 && location.getY() >= 190) {
                entity.remove();
                return;
            }
        }


        // Early return if day < 7 (most logic doesn't apply)
        if (day < 7) return;
        SpawnContext context = new SpawnContext(entity, location, day, random);

        if (customSpawner.spawnCustomVariant(event, context)) return;
        if (mobTransformer.replace(context)) return;
        if (vanillaTransformer.transform(event, context)) return;
        biomeSpawner.handleBiomeSpawns(context);
    }

    @EventHandler
    public void onPiglinSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        switch (entity.getType()) {
            case PIGLIN -> {

                Piglin piglin = (Piglin) entity;
                piglin.setAggressive(true);
            }
            case ZOMBIFIED_PIGLIN -> {
                ZombifiedPiglin piglin = (ZombifiedPiglin) entity;
                piglin.setAggressive(true);
            }
        }
    }

    private List<EntityType> getSkippedTypes() {
        return List.of(
                EntityType.ARMOR_STAND,
                EntityType.WITHER,
                EntityType.ENDER_DRAGON
        );
    }

    private List<CreatureSpawnEvent.SpawnReason> getSkippableSpawnReasons() {
        return List.of(
                CreatureSpawnEvent.SpawnReason.COMMAND,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                CreatureSpawnEvent.SpawnReason.SPAWNER,
                CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER,
                CreatureSpawnEvent.SpawnReason.ENDER_PEARL);
    }


}
