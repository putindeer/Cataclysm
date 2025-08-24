package org.cataclysm.game.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.player.data.PlayerData;
import org.cataclysm.game.player.mechanics.upgrade.UpgradeManager;
import org.cataclysm.game.player.survival.death.message.DeathMessageManager;
import org.cataclysm.game.player.survival.resurrect.mortality.MortalityManager;
import org.cataclysm.game.player.survival.resurrect.totems.TotemManager;
import org.cataclysm.game.player.systems.cooldown.CooldownManager;
import org.jetbrains.annotations.NotNull;

@Getter
public class CataclysmPlayer {
    private final PlayerData data;
    private final DeathMessageManager deathMessageManager;
    private final TotemManager totemManager;
    private final MortalityManager mortalityManager;
    private final UpgradeManager upgradeManager;
    private final CooldownManager cooldownManager;

    public CataclysmPlayer(@NotNull PlayerData data) {
        this.data = data;
        this.deathMessageManager = new DeathMessageManager(data);
        this.totemManager = new TotemManager(data);
        this.mortalityManager = new MortalityManager(data);
        this.upgradeManager = new UpgradeManager(data);
        this.cooldownManager = new CooldownManager(data);
    }

    public void save(@NotNull JsonConfig jsonConfig) {
        jsonConfig.setJsonObject(Cataclysm.getGson().toJsonTree(this.data).getAsJsonObject());
        try {
            jsonConfig.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void restore(@NotNull JsonConfig jsonConfig, String nickname) {
        CataclysmPlayer cataclysmPlayer;

        if (!jsonConfig.getJsonObject().entrySet().isEmpty()) {
            cataclysmPlayer = new CataclysmPlayer(new PlayerData(Cataclysm.getGson().fromJson(jsonConfig.getJsonObject(), PlayerData.class)));
        } else {
            cataclysmPlayer = new CataclysmPlayer(new PlayerData(nickname));
        }

        Cataclysm.getCataclysmPlayers().put(cataclysmPlayer.getData().getNickname().toUpperCase(), cataclysmPlayer);
    }

    public static CataclysmPlayer getCataclysmPlayer(@NotNull Player player) {
        return getCataclysmPlayer(player.getName());
    }

    public static CataclysmPlayer getCataclysmPlayer(@NotNull String nickname) {
        return Cataclysm.getCataclysmPlayers().get(nickname.toUpperCase());
    }
}