package org.cataclysm.game.events.pantheon.bosses.the_cataclysm.abilities;

import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Final boss ability: Cataclysmic Rite that summons powerful entities and chaos around the arena.
 */
public class CataclysmAbility extends PantheonAbility {

    private static final String[] CALAMITY_MOBS = {
            "TwistedBrute", "Headsman", "CataclystSkeleton", "CataclystStray",
            "MirageEye", "PaleBlaze", "PaleWhale", "PaleEnderman",
            "TwistedZombie", "ArcaneSpider", "AggressiveLlama"
    };

    private static final int SPAWN_COUNT = 40;
    private static final int PARTICLE_HEIGHT = 3; // menos partículas -> más rendimiento
    private static final double PARTICLE_RADIUS = 1.5; // radio reducido
    private static final long MOB_SPAWN_DELAY_TICKS = 40L; // 2 segundos

    private final TheCataclysm cataclysm;

    public CataclysmAbility(TheCataclysm cataclysm) {
        super(Material.SKELETON_SKULL, "Cataclysm", 3);
        this.cataclysm = cataclysm;
    }

    @Override
    public void channel() {
        var controller = cataclysm.getController();
        var location = controller.getLocation();
        cataclysm.getArena().getPlayersInArena().forEach(player -> {;
            playSound(player, location, Sound.ENTITY_WARDEN_ROAR, 8F, 0.5F);
            playSound(player, location, Sound.ITEM_GOAT_HORN_SOUND_6, 8F, 0.6F);
        });

        // Programar invocación de calamidades
        scheduleSync(this::spawnCalamities, 20L);
    }

    @Override
    public void cast() {
        // No se usa en este caso
    }

    private void spawnCalamities() {
        var arena = cataclysm.getArena();
        var world = arena.center().getWorld();
        var level = ((CraftWorld) world).getHandle();

        List<Location> locations = arena.getRandomLocations(SPAWN_COUNT);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (Location loc : locations) {
            String mobType = CALAMITY_MOBS[random.nextInt(CALAMITY_MOBS.length)];
            CataclysmMob calamityMob = CataclysmMob.instantiateMob(mobType, level);
            summonCalamity(loc, calamityMob);
        }
    }

    private void summonCalamity(Location location, CataclysmMob calamityMob) {
        var world = location.getWorld();

        // Sonido y partículas iniciales optimizadas
        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 2.0F, 0.7F);
        for (int i = 0; i < PARTICLE_HEIGHT; i++) {
            new ParticleHandler(location.clone().add(0, i, 0))
                    .circle((float) PARTICLE_RADIUS, Particle.SOUL_FIRE_FLAME);
        }

        // Invocación con dramatización
        scheduleSync(() -> {
            world.strikeLightningEffect(location); // solo efecto, no daño extra
            world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1.2F, 0.8F);
            calamityMob.addFreshEntity(location);
        }, MOB_SPAWN_DELAY_TICKS);
    }

    /**
     * Ejecuta un Runnable en el hilo principal de Bukkit
     */
    private void scheduleSync(Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), task, delayTicks);
    }

    private void playSound(org.bukkit.entity.Player player, Location loc, Sound sound, float volume, float pitch) {
        player.getWorld().playSound(loc, sound, volume, pitch);
    }
}
