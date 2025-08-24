package org.cataclysm.game.world.generator;

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
        SimplexOctaveGenerator gen = new SimplexOctaveGenerator(worldInfo.getSeed(), 11);
        gen.setScale(0.005725);
        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double noise = (gen.noise((chunkX * 16 + x), (chunkZ * 16 + z), .777D, .000323D) * 45.75D);

                for(int i = 0; i < noise/2.5; i++) if (15+i <= 25) chunkData.setBlock(x, 15+i, z, getMaterial(i));
                for(int i = 0; i < noise/1.87; i++) chunkData.setBlock(x, 15-i, z, getMaterial(-i));
            }
        }
    }

    public Material getMaterial(int y) {
        return (y >= 3 ? (new Random().nextInt(100) > 65 ? Material.PALE_MOSS_BLOCK : Material.GRASS_BLOCK) : Material.PALE_MOSS_BLOCK);

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
