package org.cataclysm.api.item.loot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record LootContainer(LootHolder... lootHolders) {

    public @NotNull List<LootHolder> getList() {
        return new ArrayList<>(Arrays.asList(this.lootHolders));
    }

    public @NotNull List<LootHolder> getShuffledList() {
        var list = this.getList();
        Collections.shuffle(list);
        return list;
    }

}
