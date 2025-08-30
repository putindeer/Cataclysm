package org.cataclysm.game.pantheon.level.audience;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.player.CataclysmPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PantheonSurvivor {
    private final PantheonAudience audience;

    private final @Getter UUID uuid;
    private final @Getter CataclysmPlayer manager;

    private @Getter @Setter PlayerStatus status;
    private @Getter int place;
    private @Getter int points;

    public PantheonSurvivor(PantheonAudience audience, Player player) {
        this.audience = audience;
        this.manager = CataclysmPlayer.getCataclysmPlayer(player.getName());
        this.uuid = player.getUniqueId();
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void register() {
        this.status = PlayerStatus.IDDLE;
        this.points = 0;
        this.audience.addSurvivor(this);
    }

    public void unregister() {
        this.place = audience.currentPlace();
        this.audience.removeSurvivor(this);
    }

    public @Nullable Player getPlayer() {return Bukkit.getPlayer(this.uuid);}
}
