package org.cataclysm.game.pantheon.utils;

import org.bukkit.Location;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.handlers.PantheonHandler;
import org.cataclysm.game.pantheon.handlers.PhaseHandler;

public class PantheonBuilder {
    public static void create() {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        pantheon.getPhaseHandler().changePhase(PhaseHandler.PantheonPhase.IDDLE);

        Cataclysm.setPantheon(pantheon);

        PantheonHandler.registerAll();
        PantheonHandler.setUp(false);
        PantheonHandler.setUp(true);
    }

    public static void pastePantheonEntrance(Location location) {
        SchematicLoader schemLoader = getSchemLoader("entrance");
        schemLoader.pasteSchematic(location, true);
    }

    private static SchematicLoader getSchemLoader(String path) {
        return new SchematicLoader("pantheon/schematics/" + path + ".schem");
    }
}