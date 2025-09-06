package org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAbility;
import org.cataclysm.game.player.PlayerUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TerracismaAbility extends HeartAbility {
    private final VoidLord lord;

    private static final int BASE_DAMAGE_SLAM = 180;
    private static final int BASE_DAMAGE_WAVE = 35;
    private static final double BASE_FORCE = 3.5;
    private static final int BASE_RADIUS_EARTHQUAKE = 20;

    public TerracismaAbility(VoidLord lord) {
        super(Material.ELYTRA, "Terracisma", 2);
        this.lord = lord;
    }

    // ---------- Escalado ----------

    private int scaleValue(int base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return (int) Math.round(base * factor);
    }

    private double scaleDouble(double base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return base * factor;
    }

    private long scaleDelay(long baseMillis) {
        double factor = isVoidLord() ? 0.5 : 0.8;
        return Math.max(40, (long) (baseMillis * factor));
    }

    // ---------- Canalización ----------

    @Override
    public void channel() {
        ScheduledExecutorService service = lord.getThread().getService();
        CataclysmArea arena = lord.getArena();
        Collection<Player> players = arena.getPlayersInArena();

        int iterations = (channelTime * 4) + 2;
        for (int i = 0; i < iterations; i++) {
            long delay = scaleDelay(250L * i);
            service.schedule(() -> runSync(() -> players.forEach(player -> {
                PlayerInventory inventory = player.getInventory();
                ItemStack stack = inventory.getChestplate();

                if (stack == null || !stack.getType().equals(Material.ELYTRA)) return;
                if (player.getY() < (arena.center().getY() + 6)) return;

                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 3, 0, false, false));
                if (isVoidLord()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 1, false, false));
                }
            })), delay, TimeUnit.MILLISECONDS);
        }
    }

    // ---------- Ejecución ----------

    @Override
    public void cast() {
        CataclysmArea arena = lord.getArena();
        Collection<Player> players = arena.getPlayersInArena();

        for (Player player : players) {
            if (player.getGameMode() == GameMode.SPECTATOR) continue;

            PlayerUtils.breakElytras(player, isVoidLord() ? 600 : 400);

            if (player.getY() < (arena.center().getY() + 6)) continue;
            this.slamDown(player, scaleDouble(BASE_FORCE));
        }
    }

    private void slamDown(LivingEntity entity, double force) {
        if (entity.equals(lord.getController())) return;

        World world = entity.getWorld();
        Vector velocity = entity.getVelocity();
        velocity.setY(-Math.abs(force));
        entity.setVelocity(velocity);

        UUID uuid = UUID.randomUUID();
        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
            Location loc = entity.getLocation();

            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1F, 1.2F);
            world.playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1F, 1.5F);
            if (isVoidLord()) {
                world.playSound(loc, Sound.ENTITY_WITHER_SHOOT, 1.5F, .75F);
            }

            world.spawnParticle(Particle.EXPLOSION, loc, scaleValue(5), 0.25, 0.25, 0.25, 0, null, true);

            if (entity.getLocation().clone().add(0, -.01, 0).getBlock().isSolid()) {
                Bukkit.getScheduler().cancelTask(Cataclysm.getTasks().get(uuid));
                this.castEarthquake(loc, scaleValue(BASE_RADIUS_EARTHQUAKE));
                this.lord.damage(entity, scaleValue(BASE_DAMAGE_SLAM));
            }
        }, 2, 1);

        Cataclysm.getTasks().put(uuid, task);
    }

    private void castEarthquake(Location loc, int radius) {
        ScheduledExecutorService service = lord.getThread().getService();
        World world = loc.getWorld();

        Block blockBelow = loc.getWorld().getBlockAt(loc.clone().add(0, -1, 0));
        if (!blockBelow.isSolid()) return;

        runSync(() -> {
            loc.getWorld().strikeLightning(loc);
            world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 7F, 0.5F);
            world.playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 5F, 0.5F);
            world.playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 5F, 0.65F);
            if (isVoidLord()) {
                world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 8F, .65F);
            }
        });

        float power = 3.0F;
        long interval = 70L;
        for (int i = 0; i < radius; i++) {
            final int step = i;
            service.schedule(() -> this.castWave(loc, step, power),
                    scaleDelay(i * interval), TimeUnit.MILLISECONDS);
        }
    }

    private void castWave(@NotNull Location center, float radius, float power) {
        int points = Math.max(8, (int) radius);
        float y = (float) (center.getY() + 1);

        for (int angle = 0; angle < 360; angle += 360 / points) {
            double rad = Math.toRadians(angle);
            double x = center.getX() + Math.cos(rad) * radius;
            double z = center.getZ() + Math.sin(rad) * radius;

            Location point = new Location(center.getWorld(), x, y, z);
            runSync(() -> summonExplosion(point, power));
        }
    }

    private void summonExplosion(Location loc, double radius) {
        World world = loc.getWorld();
        Collection<LivingEntity> entities = loc.getNearbyLivingEntities(radius + 1.5);

        entities.forEach(entity -> {
            if (entity.equals(lord.getController())) return;
            lord.damage(entity, scaleValue(BASE_DAMAGE_WAVE));
            entity.setNoDamageTicks(20);
        });

        ParticleHandler handler = new ParticleHandler(loc);
        handler.sphere(Particle.EXPLOSION, scaleDouble(radius), scaleDouble(radius * 2));

        world.playSound(loc, Sound.ITEM_TRIDENT_RETURN, (float) (radius * 2), .6F);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, (float) radius, 1.2F);
    }

    // ---------- Util ----------

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task);
    }
}