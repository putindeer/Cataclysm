package org.cataclysm.game.world.generator;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

public class EndTreePopulator extends BlockPopulator {


    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if(random.nextInt(100) >= 35) {
            int X = random.nextInt(16) + (chunkX * 16);
            int Z = random.nextInt(16) + (chunkZ * 16);
            int Y = 65;
            if (!limitedRegion.isInRegion(new Location(Bukkit.getWorld("custom_end"), X,Y,Z))) {
                return;
            }

            while (limitedRegion.getType(X, Y, Z) != Material.AIR) Y++;

            Location location = new Location(Bukkit.getWorld("custom_end"), X, Y, Z);
            Location blockLoc = location.clone().add(0.0D, -1.0D, 0.0D);

            if (!limitedRegion.isInRegion(location)) {
                return;
            }

            Material startMaterial = limitedRegion.getType(location.clone().add(0.0D, -1.0D, 0.0D));

            if (startMaterial == Material.END_STONE || startMaterial == Material.END_STONE_BRICKS) {

                if (limitedRegion.isInRegion(location)) {
                    limitedRegion.setType(blockLoc, Material.END_STONE);
                    generateChorusWithFroglights(limitedRegion, location, random);
                }
            }
        }
    }

    public void generateChorusWithFroglights(@NotNull LimitedRegion region, Location location, @NotNull Random random) {
        region.generateTree(location, random, TreeType.CHORUS_PLANT, state -> {
            if (state.getType() == Material.CHORUS_FLOWER) {
                state.setType(Material.PEARLESCENT_FROGLIGHT);
            }
        });
    }

}
