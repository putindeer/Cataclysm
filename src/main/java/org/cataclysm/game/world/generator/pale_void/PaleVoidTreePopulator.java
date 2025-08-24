package org.cataclysm.game.world.generator.pale_void;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.cataclysm.game.world.Dimensions;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PaleVoidTreePopulator extends BlockPopulator {

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        int X = random.nextInt(16) + (chunkX * 16);
        int Z = random.nextInt(16) + (chunkZ * 16);
        int Y = 16;

        if (!limitedRegion.isInRegion(new Location(Dimensions.PALE_VOID.getWorld(), X, Y, Z))) return;

        while (limitedRegion.getType(X, Y, Z) != Material.AIR) Y++;

        Location firstTreeLocation = new Location(Dimensions.PALE_VOID.getWorld(), X, Y, Z);
        Material startMaterial = limitedRegion.getType(firstTreeLocation.clone().add(0.0D, -1.0D, 0.0D));

        if (!limitedRegion.isInRegion(firstTreeLocation)) return;

        if (startMaterial == Material.PALE_MOSS_BLOCK) {
            this.generatePaleTrees(limitedRegion, firstTreeLocation, random);

            for (int i = 1; i < 6; i++) {
                int offsetX = random.nextInt(9) - 4;
                int offsetZ = random.nextInt(9) - 4;

                int newX = X + offsetX;
                int newZ = Z + offsetZ;
                int newY = Y;

                Location newTreeLocation = new Location(Dimensions.PALE_VOID.getWorld(), newX, newY, newZ);
                if (!limitedRegion.isInRegion(newTreeLocation)) continue;

                while (newY > 0 && limitedRegion.getType(newX, newY, newZ) == Material.AIR) newY--;
                newY++;

                newTreeLocation.setY(newY);

                if (!limitedRegion.isInRegion(newTreeLocation)) continue;

                Material groundMaterial = limitedRegion.getType(newTreeLocation.clone().add(0.0D, -1.0D, 0.0D));
                if (groundMaterial == Material.PALE_OAK_WOOD || groundMaterial == Material.PALE_MOSS_BLOCK) this.generatePaleTrees(limitedRegion, newTreeLocation, random);
            }
        }
    }

    public void generatePaleTrees(@NotNull LimitedRegion region, Location location, @NotNull Random random) {
        TreeType type;

        switch (new Random().nextInt(0, 20)) {
            case 0, 1, 2, 3 -> type = TreeType.JUNGLE;
            case 4, 5 -> type = TreeType.BIG_TREE;
            case 6, 7 -> type = TreeType.CHERRY;
            case 8 -> type = TreeType.BIRCH;
            case 9 -> type = TreeType.DARK_OAK;
            case 10 -> type = TreeType.ACACIA;
            case 11, 12 -> type = TreeType.TALL_MANGROVE;
            case 13 -> type = TreeType.MANGROVE;
            case 14 -> type = TreeType.MEGA_PINE;
            default -> type = TreeType.PALE_OAK_CREAKING;
        }

        region.generateTree(location, random, type, blockState -> {
            var blockType = blockState.getType();
            if (blockType.name().toUpperCase().contains("MANGROVE_PROPAGULE")) blockState.setType(Material.PALE_OAK_WOOD);
            if (blockType.name().toUpperCase().contains("MANGROVE_ROOTS")) blockState.setType(Material.PALE_OAK_WOOD);
            if (blockType.name().toUpperCase().contains("MOSS_CARPET")) blockState.setType(Material.PALE_MOSS_CARPET);
            if (blockType.name().toUpperCase().contains("PODZOL")) blockState.setType(Material.PALE_OAK_WOOD);
            if (blockType.name().toUpperCase().contains("LOG")) blockState.setType(Material.PALE_OAK_LOG);
            if (blockType.name().toUpperCase().contains("LEAVE")) blockState.setType(Material.PALE_OAK_LEAVES);
        });
    }

}