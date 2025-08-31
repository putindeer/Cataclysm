package org.cataclysm.game.pantheon.bosses.calamity_hydra.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.attacks.CalamityExplosion;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HellquakeAbility extends HydraAbility {
    private ScheduledFuture<?> future;
    private double initJumpValue;

    public HellquakeAbility(PantheonHydra hydra) {
        super(hydra, Material.PITCHER_PLANT, "Terremoto", 2, 20);
    }

    @Override
    public void channel() {
        super.hydra.playSound(Sound.ENTITY_RAVAGER_STUNNED, 12F, 0.66F);
        super.hydra.playSound(Sound.ENTITY_RAVAGER_STUNNED, 12F, 0.86F);
        super.hydra.playSound(Sound.ENTITY_BLAZE_DEATH, 12F, 0.86F);
    }

    @Override
    public void cast() {
        super.hydra.playSound(Sound.ENTITY_BLAZE_DEATH, 12F, 0.5F);
        super.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 12F, 0.66F);
        super.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 12F, 0.86F);
        this.toggleJumpMode(true);
    }

    private void castEarthquake(ScheduledExecutorService service) {
        Location location = this.hydra.getLocation();

        Block blockBelow = location.getWorld().getBlockAt(location.clone().add(0, -1, 0));
        if (!blockBelow.isSolid()) return;

        double scale = super.hydra.getAttribute(Attribute.SCALE);
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            location.getWorld().strikeLightning(location);
            this.hydra.playSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 7F, 0.5F);
            this.hydra.playSound(Sound.ITEM_TRIDENT_THUNDER, 5F, 0.5F);
            this.hydra.playSound(Sound.ITEM_TRIDENT_THUNDER, 5F, 0.65F);
            this.hydra.createHydraExplosion(location, scale, CalamityExplosion.Type.MACRO);
        });

        double radius = (scale * 15);
        float power = 5.0F;
        long interval = 70L;

        for (int i = 0; i < radius; i++) {
            final int finalI = i;
            service.schedule(() -> this.castWave(location, finalI, power), (i * interval), TimeUnit.MILLISECONDS);
        }

        this.toggleJumpMode(false);
    }

    private void toggleJumpMode(boolean activate) {
        if (activate) {
            this.initJumpValue = super.hydra.getAttribute(Attribute.JUMP_STRENGTH);
            super.hydra.setAttribute(Attribute.JUMP_STRENGTH, this.initJumpValue * 3);

            ScheduledExecutorService service = super.hydra.getThread().getService();
            this.future = service.scheduleAtFixedRate(() -> this.castEarthquake(service), 1500, 100, TimeUnit.MILLISECONDS);
        } else {
            super.hydra.setAttribute(Attribute.JUMP_STRENGTH, this.initJumpValue);
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
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.hydra.createHydraExplosion(pointLocation, power, CalamityExplosion.Type.SMALL));
        }
    }
}
