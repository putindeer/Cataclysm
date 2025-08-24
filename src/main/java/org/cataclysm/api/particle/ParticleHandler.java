package org.cataclysm.api.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;

public class ParticleHandler {
    private final Location location;
    private final double x;
    private final double z;

    public ParticleHandler(@NotNull Location location) {
        this.location = location;
        this.x = location.getX();
        this.z = location.getZ();
    }

    public void sphere(Particle particle, double radius, double steps) {
        for (int i = 0; i <= steps; i++) {
            double phi = Math.PI * i / steps;
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);

            for (int j = 0; j < steps; j++) {
                double theta = 2 * Math.PI * j / steps;
                double sinTheta = Math.sin(theta);
                double cosTheta = Math.cos(theta);

                double x = radius * sinPhi * cosTheta;
                double y = radius * cosPhi;
                double z = radius * sinPhi * sinTheta;

                var particleLoc = this.location.clone().add(x, y, z);
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () ->
                        this.location.getWorld().spawnParticle(particle, particleLoc, 0, 0, 0, 0, 0, null, true)
                );
            }
        }
    }

    public void circle(float radius, Particle... particles) {this.circle(radius, ((int) radius) * 12, 1, (float) (this.location.getY() + 1), particles);}

    public void circle(float radius, int points, int amountPerPoint, float y, Particle... particles) {
        for (var angleDegrees = 0; angleDegrees < 360; angleDegrees += 360 / points) {
            final var angleRadians = Math.toRadians(angleDegrees);
            final var x = this.x + Math.cos(angleRadians) * radius;
            final var z = this.z + Math.sin(angleRadians) * radius;

            final var pointLocation = new Location(this.location.getWorld(), x, y, z);
            for (var particle : particles) {
                this.location.getWorld().spawnParticle(particle, pointLocation, 0, 0, 0, 0, amountPerPoint);
            }
        }
    }
}
