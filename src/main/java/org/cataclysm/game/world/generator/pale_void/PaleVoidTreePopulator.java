package org.cataclysm.game.world.generator.pale_void;

import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.data.type.CreakingHeart;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.cataclysm.game.world.Dimensions;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

            for (int i = 1; i < 10; i++) {
                int offsetX = random.nextInt(17) - 4;
                int offsetZ = random.nextInt(17) - 4;

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
                if (!groundMaterial.isAir()) this.generatePaleTrees(limitedRegion, newTreeLocation, random);
            }
        }
    }

    public void generatePaleTrees(@NotNull LimitedRegion region, Location location, @NotNull Random random) {
        TreeType type;

        List<TreeType> treeTypes = new ArrayList<>(List.of(
                TreeType.JUNGLE,
                TreeType.BIG_TREE,
                TreeType.CHERRY,
                TreeType.TALL_MANGROVE,
                TreeType.MANGROVE,
                TreeType.TALL_BIRCH,
                TreeType.TALL_REDWOOD,
                TreeType.PALE_OAK,
                TreeType.DARK_OAK
        ));

        Collections.shuffle(treeTypes);
        type = treeTypes.getFirst();

        region.generateTree(location, random, type, blockState -> {
            Material blockType = blockState.getType();
            String id = blockType.name().toUpperCase();

            if (id.contains("MANGROVE_PROPAGULE")) blockState.setType(Material.PALE_OAK_WOOD);
            if (id.contains("MANGROVE_ROOTS")) blockState.setType(Material.PALE_OAK_WOOD);
            if (id.contains("MOSS_CARPET")) blockState.setType(Material.PALE_MOSS_CARPET);
            if (id.contains("PODZOL")) blockState.setType(Material.PALE_MOSS_BLOCK);
            if (id.contains("LEAVE")) blockState.setType(Material.PALE_OAK_LEAVES);
            if (id.contains("LOG")) blockState.setType(Material.PALE_OAK_LOG);
        });
    }

}