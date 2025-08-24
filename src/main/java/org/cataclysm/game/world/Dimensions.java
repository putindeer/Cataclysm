package org.cataclysm.game.world;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

@Getter
public enum Dimensions {
    BETA(new WorldCreator("world_beta").type(WorldType.FLAT)),

    OVERWORLD(new WorldCreator("world")),
    NETHER(new WorldCreator("world_nether")),
    PALE_VOID(new WorldCreator("pale_void")),
    THE_END(new WorldCreator("custom_end")),
    ;

    private final WorldCreator worldCreator;

    Dimensions(WorldCreator worldCreator) {
        this.worldCreator = worldCreator;
    }

    public World getWorld() {
        return this.worldCreator.createWorld();
    }
}
