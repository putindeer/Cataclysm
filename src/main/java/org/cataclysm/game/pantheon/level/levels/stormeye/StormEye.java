package org.cataclysm.game.pantheon.level.levels.stormeye;

import lombok.Getter;
import org.bukkit.Location;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.levels.PantheonLevel;
import org.cataclysm.game.world.Dimensions;

public class StormEye extends PantheonLevel {
    public StormEye(PantheonOfCataclysm pantheon) {
        super(pantheon);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public Location location() {return location;}

    private static @Getter Location location = new Location(PantheonOfCataclysm.getWorld(), 750.5, 100, 750.5);
}
