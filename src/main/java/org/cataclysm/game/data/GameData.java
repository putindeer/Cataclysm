package org.cataclysm.game.data;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameData {
    private int deathCount;

    public GameData() {
        this.deathCount = 0;
    }

    public GameData(int deathCount) {
        this.deathCount = deathCount;
    }
}
