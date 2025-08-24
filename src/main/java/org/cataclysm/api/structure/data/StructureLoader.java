package org.cataclysm.api.structure.data;

import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.api.mob.MobLoader;
import org.cataclysm.api.structure.CataclysmStructure;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class StructureLoader {
    public final JsonConfig jsonConfig;

    public StructureLoader(String id, @NotNull UUID uuid) throws Exception {
        var path = StructureConfig.getStructuresPath() + "/" + id + "/structures/" + uuid + "/level.json";
        this.jsonConfig = JsonConfig.cfg(path, Cataclysm.getInstance());
    }

    public static void saveAll() throws Exception {
        var structures = CataclysmStructure.getStructures().values();
        for (var structure : structures) {
            var loader = new StructureLoader(structure.getConfig().getId(), structure.getUuid());
            loader.save(structure);
            MobLoader.saveAll(structure);
        }
    }

    public static void loadAll() {
        var dungeonsFolder = new File(Cataclysm.getInstance().getDataFolder(), StructureConfig.getStructuresPath());
        var dungeons = dungeonsFolder.listFiles();

        if (dungeons == null) return;

        for (var dungeon : dungeons) {
            var dungeonName = dungeon.getName().toUpperCase();

            var path = Cataclysm.getInstance().getDataFolder().getAbsolutePath() + "/" + StructureConfig.getStructuresPath() + "/" + dungeonName + "/structures";
            var structuresFolder = new File(path);
            var structures = structuresFolder.list();

            if (structures == null) continue;

            for (var structure : structures) {
                var uuid = UUID.fromString(structure);
                try {
                    new StructureLoader(dungeonName, uuid).restore();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void save(@NotNull CataclysmStructure structure) {
        structure.save(this.jsonConfig);
    }

    public void restore() {
        CataclysmStructure.restore(this.jsonConfig);
    }
}