package org.cataclysm.api.mob;

import org.bukkit.entity.LivingEntity;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.Base64;

public class MobLoader {
    private final CataclysmToken cataclysmToken;
    private final JsonConfig jsonConfig;
    private final CataclysmStructure structure;

    public MobLoader(@NotNull CataclysmMob mob, @NotNull CataclysmStructure structure) throws Exception {
        this.cataclysmToken = mob.getMobToken();
        this.structure = structure;

        var id = CataclysmMob.getID(mob.getBukkitLivingEntity());
        if (id == null) throw new IllegalArgumentException("Mob does not have a valid ID.");

        this.jsonConfig = JsonConfig.cfg(structure.getPath() + "/mobs/" + id.toUpperCase() + "/" + this.cataclysmToken.key() + ".json", Cataclysm.getInstance());
    }

    public MobLoader(@NotNull CataclysmToken token, @NotNull String mobId, @NotNull CataclysmStructure structure) throws Exception {
        this.cataclysmToken = token;
        this.structure = structure;
        this.jsonConfig = JsonConfig.cfg(structure.getPath() + "/mobs/" + mobId.toUpperCase() + "/" + this.cataclysmToken.key() + ".json", Cataclysm.getInstance());
    }

    public void save() {
        var mob = this.structure.getMobStore().getStorer().getMobWithToken(this.cataclysmToken);
        if (mob == null) return;
        try {
            mob.save(this.jsonConfig);
        } catch (Exception ignored) {
        }
    }

    public boolean restore() throws Exception {
        if (this.jsonConfig.getJsonObject().isEmpty()) {
            var file = this.jsonConfig.getFile();
            file.delete();
            return false;
        }

        var base64 = this.jsonConfig.getJsonObject().get("data").getAsString();
        var inputStream = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));

        var mob = (CataclysmMob) inputStream.readObject();
        mob.updateToken();
        this.structure.getMobStore().getStorer().store(mob);
        return true;
    }

    public static void saveAll(@NotNull CataclysmStructure structure) throws Exception {
        for (var box : structure.getMobStore().getStorer().store()) {
            var mob = box.cataclysmMob();
            if (mob.getSpawnTag() != CataclysmMob.SpawnTag.PERSISTENT) continue;

            var dungeon = mob.getStructure();
            if (dungeon == null) continue;

            new MobLoader(mob, dungeon).save();
        }
    }

    public static void loadAll(@NotNull CataclysmStructure structure) {
        var mobFolder = new File(Cataclysm.getInstance().getDataFolder(), structure.getPath() + "/mobs");
        var mobFolders = mobFolder.listFiles();

        if (mobFolders == null) return;
        int deletedFiles = 0;
        for (var folder : mobFolders.clone()) {
            var mobs = folder.listFiles();

            if (mobs == null) continue;

            for (var mob : mobs.clone()) {
                var mobId = folder.getName().toUpperCase();
                var token = new CataclysmToken(mob.getName().replace(".json", ""));
                try {
                    if (!new MobLoader(token, mobId, structure).restore()) deletedFiles++ ;
                } catch (Exception ignored) {

                }
            }
            if (deletedFiles > 0) Cataclysm.getInstance().getLogger().info("[MobLoader] Deleted " + deletedFiles + " files from " + structure.getLevel().id);
        }
    }

    public static @Nullable File getMobFile(CataclysmStructure structure, LivingEntity livingEntity) {
        var id = CataclysmMob.getID(livingEntity);
        var token = CataclysmMob.getToken(livingEntity);
        if (id == null || token == null) return null;
        return new File(Cataclysm.getInstance().getDataFolder(), structure.getPath() + "/mobs/" + id.toUpperCase() + "/" + token.key() + ".json");
    }
}
