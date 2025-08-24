package org.cataclysm.game.player.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.player.CataclysmPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerLoader {
    private final String nickname;
    private final JsonConfig jsonConfig;

    public PlayerLoader(@NotNull String nickname) throws Exception {
        this.nickname = nickname;
        this.jsonConfig = JsonConfig.cfg("/Data/Player/" + nickname.toUpperCase() + ".json", Cataclysm.getInstance());
    }

    public static void saveAll() throws Exception {
        for (CataclysmPlayer cataclysmPlayer : Cataclysm.getPlayerHashMap().values()) {
            new PlayerLoader(cataclysmPlayer.getData().getNickname()).save(cataclysmPlayer);
        }
    }

    public static void loadAll() throws Exception {
        for (Player player : Bukkit.getOnlinePlayers()) {
            new PlayerLoader(player.getName()).restore();
        }
    }

    public void save(@NotNull CataclysmPlayer cataclysmPlayer) {
        cataclysmPlayer.save(this.jsonConfig);
    }

    public void restore() {
        CataclysmPlayer.restore(this.jsonConfig, this.nickname);
    }
}