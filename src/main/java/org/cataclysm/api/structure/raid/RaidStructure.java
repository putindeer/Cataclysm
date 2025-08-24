package org.cataclysm.api.structure.raid;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Listener;
import org.cataclysm.api.boss.CataclysmArea;

@Getter
public abstract class RaidStructure {
    private final String name;
    private @Setter Listener listener;

    public RaidStructure(String name) {
        this.name = name;
    }

    public abstract CataclysmArea getArea();

    public abstract CataclysmArea getBossArena();
}
