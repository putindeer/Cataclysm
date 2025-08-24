package org.cataclysm.api.item.loot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public record LootHolder(ItemStack itemStack, int minAmount, int maxAmount, double rarity) {
    public @Nullable ItemStack build() {
        ItemStack item = this.itemStack.clone();

        double chance = new Random().nextDouble(0, 1);
        if (chance >= this.rarity) return null;

        for (int amount = this.minAmount; amount <= this.maxAmount; amount++) {
            int addChance = new Random().nextInt(0, 100);
            if (addChance < (250 / this.maxAmount)) {
                item.setAmount(amount);
                break;
            }
        }

        return item;
    }
}
