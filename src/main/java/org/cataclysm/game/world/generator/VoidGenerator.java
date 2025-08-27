package org.cataclysm.game.world.generator;

import org.bukkit.HeightMap;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {}

    @Override
    public int getBaseHeight(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull HeightMap heightMap) {
        return 320;
    }
}
