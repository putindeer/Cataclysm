package org.cataclysm.game.world.generator.the_end;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Random;

public class EndGenerator extends ChunkGenerator {

    boolean block00 = false;
    int Y = 65;

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        if (!block00) {
            if (chunkData.getType(0, 83, 0) == Material.AIR) {
                chunkData.setBlock(0, 83, 0, Material.END_STONE);
            }

            block00 = true;
        }

        long seed = worldInfo.getSeed();
        SimplexOctaveGenerator baseGen = new SimplexOctaveGenerator(new Random(seed), 8);
        baseGen.setScale(0.0082);

        SimplexOctaveGenerator detailGen = new SimplexOctaveGenerator(new Random(seed + 1), 8);
        detailGen.setScale(0.03);

        for (int X = 0; X < 16; X++) {
            for (int Z = 0; Z < 16; Z++) {
                int worldX = chunkX * 16 + X;
                int worldZ = chunkZ * 16 + Z;
                double baseHeight = baseGen.noise(worldX, worldZ, 0.425D, 0.375D) * 47.5D;
                double detailHeight = detailGen.noise(worldX, worldZ, 0.315D, 0.265D) * 3.5D;
                int Height = (int) (baseHeight + detailHeight);

                for (int i = 0; i < Height; ++i) {
                    Random blockRand = new Random((worldX * 341873128712L) ^ (worldZ * 132897987541L) ^ (i * 8371L));
                    int randomGen = blockRand.nextInt(10);
                    int topLayer = Y + i;
                    if (topLayer >= 75) {
                        topLayer = 75;
                        Y--;
                    }

                    if (Y < 65) Y++;

                    if (Y - i >= 0) {
                        switch (randomGen) {
                            case 7, 8, 9 -> chunkData.setBlock(X, Y - i, Z, Material.END_STONE_BRICKS);
                            default -> chunkData.setBlock(X, Y - i, Z, Material.END_STONE);
                        }

                        switch (randomGen) {
                            case 7, 8, 9 -> chunkData.setBlock(X, topLayer, Z, Material.END_STONE_BRICKS);
                            default -> chunkData.setBlock(X, topLayer, Z, Material.END_STONE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return List.of(new EndTreePopulator());
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }


}
