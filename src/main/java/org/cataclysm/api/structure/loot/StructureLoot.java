package org.cataclysm.api.structure.loot;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class StructureLoot {
    private LootContainer chestLoot;
    private LootContainer trappedChestLoot;
    private LootContainer barrelLoot;
    private LootContainer vaultLoot;

    public void setLootContainer(@NotNull Material material, LootContainer lootContainer) {
        switch (material) {
            case CHEST -> this.chestLoot = lootContainer;
            case TRAPPED_CHEST -> this.trappedChestLoot = lootContainer;
            case BARREL -> this.barrelLoot = lootContainer;
            case VAULT -> this.vaultLoot = lootContainer;
        }
    }

    public void apply(@NotNull BlockInventoryHolder blockInventoryHolder) {
        LootContainer lootContainer = null;

        switch (blockInventoryHolder.getBlock().getType()) {
            case CHEST -> lootContainer = this.chestLoot;
            case TRAPPED_CHEST -> lootContainer = this.trappedChestLoot;
            case BARREL -> lootContainer = this.barrelLoot;
        }

        if (lootContainer != null) this.apply(blockInventoryHolder, lootContainer);
    }

    public void apply(@NotNull BlockInventoryHolder blockInventoryHolder, @NotNull LootContainer lootContainer) {
        Inventory inventory = blockInventoryHolder.getInventory();
        inventory.clear();

        List<ItemStack> list = new ArrayList<>();

        int quantity = (int) (inventory.getSize() / 1.5);
        for (int i = 0; i < quantity; i++) {
            for (LootHolder lootHolder : lootContainer.getList()) {
                ItemStack itemStack = lootHolder.build();
                if (itemStack == null) continue;
                list.add(itemStack);
                break;
            }
        }

        int remainingSlots = inventory.getSize() - list.size();
        for (int i = 0; i < remainingSlots; i++) list.add(new ItemStack(Material.AIR));

        Collections.shuffle(list);

        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, list.get(i));
    }
}
