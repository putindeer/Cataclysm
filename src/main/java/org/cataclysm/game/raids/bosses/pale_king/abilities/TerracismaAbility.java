package org.cataclysm.game.raids.bosses.pale_king.abilities;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.raids.bosses.calamity_hydra.attacks.CalamityExplosion;
import org.cataclysm.game.raids.bosses.pale_king.PaleKing;
import org.cataclysm.game.raids.bosses.pale_king.PaleKingUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TerracismaAbility extends PaleAbility {
    public TerracismaAbility(PaleKing king) {
        super(king, Material.ELYTRA, "Terracisma", 2, 10);
    }

    @Override
    public void channel() {
        ScheduledExecutorService service = super.king.getThread().getService();

        CataclysmArea arena = super.king.getArena();
        Collection<Player> players = arena.getPlayersInArena();
        for (int i = 0; i < (this.channelTime * 4) + 2; i++) {
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> players.forEach(player -> {
                    PlayerInventory inventory = player.getInventory();

                    ItemStack stack = inventory.getChestplate();
                    if (stack == null || !stack.getType().equals(Material.ELYTRA) || player.getY() < (arena.center().getY() + 6)) return;

                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 3, 0, false, false));
                }));
            }, 250L * i, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void cast() {
        CataclysmArea arena = super.king.getArena();
        Collection<Player> players = arena.getPlayersInArena();
        for (Player player : players) {
            if (player.getGameMode() == GameMode.SPECTATOR) return;

            PaleKingUtils.breakElytras(player, 400);

            if (player.getY() < (arena.center().getY() + 6)) continue;
            this.slamDown(player, 3.5);
        }
    }

    private void slamDown(LivingEntity livingEntity, double force) {
        World world = livingEntity.getWorld();

        if (livingEntity.equals(super.king.getController())) return;
        Vector velocity = livingEntity.getVelocity();
        velocity.setY(-Math.abs(force));
        livingEntity.setVelocity(velocity);

        UUID uuid = UUID.randomUUID();
        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
            Location location = livingEntity.getLocation();

            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1F, 1.2F);
            world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1F, 1.5F);
            world.spawnParticle(Particle.EXPLOSION, location, 5, 0.25, 0.25, 0.25, 0, null, true);

            if (livingEntity.getLocation().clone().add(0, -.01, 0).getBlock().isSolid()) {
                Bukkit.getScheduler().cancelTask(Cataclysm.getBukkitTasks().get(uuid));
                this.castEarthquake(location, 20);
                super.king.damage(livingEntity, 180);
            };
        }, 2, 1);
        Cataclysm.getBukkitTasks().put(uuid, task);
    }

    private void castEarthquake(Location location, int radius) {
        ScheduledExecutorService service = super.king.getThread().getService();
        World world = location.getWorld();

        Block blockBelow = location.getWorld().getBlockAt(location.clone().add(0, -1, 0));
        if (!blockBelow.isSolid()) return;

        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            location.getWorld().strikeLightning(location);
            world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 7F, 0.5F);
            world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 5F, 0.5F);
            world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 5F, 0.65F);
        });

        float power = 3.0F;
        long interval = 70L;
        for (int i = 0; i < radius; i++) {
            final int finalI = i;
            service.schedule(() -> this.castWave(location, finalI, power), (i * interval), TimeUnit.MILLISECONDS);
        }
    }

    private void castWave(@NotNull Location center, float radius, float power) {
        int points = ((int) radius);
        float y = (float) (center.getY() + 1);

        for (var angleDegrees = 0; angleDegrees < 360; angleDegrees += 360 / points) {
            final var angleRadians = Math.toRadians(angleDegrees);
            final var x = center.getX() + Math.cos(angleRadians) * radius;
            final var z = center.getZ() + Math.sin(angleRadians) * radius;

            Location pointLocation = new Location(center.getWorld(), x, y, z);
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.summonExplosion(pointLocation, power));
        }
    }

    private void summonExplosion(Location location, double radius) {
        World world = location.getWorld();
        Collection<LivingEntity> livingEntities = location.getNearbyLivingEntities(radius + 1.5);

        livingEntities.forEach(livingEntity -> {
            if (livingEntity.equals(super.king.getController())) return;
            super.king.damage(livingEntity, 35);
            livingEntity.setNoDamageTicks(20);
        });

        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.EXPLOSION, radius, radius * 2);

        world.playSound(location, Sound.ITEM_TRIDENT_RETURN, (float) (radius * 2), .6F);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, (float) radius, 1.2F);
    }
}
