package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.attacks.PantheonExplosion;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HellquakePantheonAbility extends PantheonAbility {
    private ScheduledFuture<?> future;
    private double initJumpValue;

    private final PantheonHydra hydra;

    public HellquakePantheonAbility(PantheonHydra hydra) {
        super(Material.PITCHER_PLANT, "Earthquake", 2);
        this.hydra = hydra;
    }

    @Override
    public void channel() {
        Location location = this.hydra.getController().getLocation();
        World world = location.getWorld();

        world.playSound(location, Sound.ENTITY_RAVAGER_STUNNED, 12F, 0.66F);
        world.playSound(location, Sound.ENTITY_RAVAGER_STUNNED, 12F, 0.86F);
        world.playSound(location, Sound.ENTITY_BLAZE_DEATH, 12F, 0.86F);
    }

    @Override
    public void cast() {
        Location location = this.hydra.getController().getLocation();
        World world = location.getWorld();

        world.playSound(location, Sound.ENTITY_BLAZE_DEATH, 12F, 0.5F);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 12F, 0.66F);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 12F, 0.86F);

        this.toggleJumpMode(true);
    }

    private void castEarthquake(ScheduledExecutorService service) {
        Location location = this.hydra.getController().getLocation();
        World world = location.getWorld();

        Block blockBelow = location.getWorld().getBlockAt(location.clone().add(0, -1, 0));
        if (!blockBelow.isSolid()) return;

        double scale = this.hydra.getAttribute(Attribute.SCALE);
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            world.strikeLightning(location);
            world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 7F, 0.5F);
            world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 5F, 0.5F);
            world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 5F, 0.65F);
            new PantheonExplosion(this.hydra).create(location, scale, PantheonExplosion.Type.MACRO);
        });

        double radius = (scale * 25);
        float power = 8.0F;
        long interval = 70L;
        for (int i = 0; i < radius; i++) {
            final int finalI = i;
            service.schedule(() -> this.castWave(location, finalI, power), (i * interval), TimeUnit.MILLISECONDS);
        }
        this.toggleJumpMode(false);
    }

    private void toggleJumpMode(boolean activate) {
        if (activate) {
            ScheduledExecutorService service = this.hydra.getThread().getService();
            this.future = service.scheduleAtFixedRate(() -> this.castEarthquake(service), 1500, 100, TimeUnit.MILLISECONDS);
            this.initJumpValue = this.hydra.getAttribute(Attribute.JUMP_STRENGTH);
            this.hydra.setAttribute(Attribute.JUMP_STRENGTH, this.initJumpValue * 3);
        } else {
            this.hydra.setAttribute(Attribute.JUMP_STRENGTH, this.initJumpValue);
            if (this.future != null) this.future.cancel(true);
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
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () ->
                    new PantheonExplosion(this.hydra).create(pointLocation, power, PantheonExplosion.Type.SMALL));
        }
    }
}
