package org.cataclysm.game.world.generator.pale_void;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class PaleVoidGenerator extends ChunkGenerator {

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(worldInfo.getSeed(), 11);
        generator.setScale(0.016725);

        int y = 40;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double noise = (generator.noise((chunkX * 16 + x), (chunkZ * 16 + z), .777D, .000423D) * 45.75D);
                for (int i = 0; i < (noise/2.5); i++) if ((15 + i) <= 45) chunkData.setBlock(x, (y + i), z, this.getMaterial(i));
                for (int i = 0; i < (noise/0.55); i++) {
                    if (i == 0) chunkData.setBlock(x, (y - i), z, this.getMaterial(0));
                    chunkData.setBlock(x, (y - 1 - i), z, this.getMaterial(-i));
                }
            }
        }
    }

    public Material getMaterial(int y) {
        Material material;

        double random = new Random().nextDouble(100);
        if (y > 0) {
            if (random > 30) material = Material.PALE_MOSS_BLOCK;
            else if (random > .5) material = Material.PALE_OAK_WOOD;
            else material = Material.PALE_OAK_LOG;
        }
        else material = Material.PALE_OAK_WOOD;

        return material;
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return List.of(new PaleVoidTreePopulator());
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new BiomeProvider() {
            @Override
            public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                return Biome.PALE_GARDEN;
            }

            @Override
            public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                return List.of(Biome.PALE_GARDEN);
            }
        };
    }

}
