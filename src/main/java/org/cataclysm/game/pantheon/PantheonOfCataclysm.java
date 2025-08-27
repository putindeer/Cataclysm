package org.cataclysm.game.pantheon;

import org.bukkit.World;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.level.LevelManager;

public class PantheonOfCataclysm {
    public World world;

    public PantheonOfCataclysm(World world) {
        this.world = world;
    }

    public static PantheonOfCataclysm createInstance() {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm(LevelManager.createWorld());
        Cataclysm.setPantheon(pantheon);
        return pantheon;
    }
}
