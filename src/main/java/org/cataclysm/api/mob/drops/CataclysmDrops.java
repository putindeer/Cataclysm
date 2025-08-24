package org.cataclysm.api.mob.drops;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;

public record CataclysmDrops(LootContainer lootContainer) {
    public void drop(Location location) {
        for (LootHolder dropHolder : lootContainer.lootHolders()) {
            ItemStack itemStack = dropHolder.build();
            if (itemStack != null) {
                location.getWorld().dropItem(location, itemStack);
            }
        }
    }
}
