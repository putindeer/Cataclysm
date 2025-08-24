package org.cataclysm.api.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.global.utils.text.TextUtils;

import java.util.HashMap;

public class ItemRestorer {
    private final PlayerInventory playerInventory;

    public ItemRestorer(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    public void check() {
        HashMap<Integer, ItemStack> restorableItems = this.getRestorableItems();
        for (var entrySet : restorableItems.entrySet()) {
            Integer slot = entrySet.getKey();
            ItemStack item = entrySet.getValue();
            this.playerInventory.setItem(slot, item);
        }
    }

    public HashMap<Integer, ItemStack> getRestorableItems() {
        HashMap<Integer, ItemStack> restorableItems = new HashMap<>();

        HashMap<String, ItemStack> itemMap = this.getItemsMap();
        ItemStack[] contents = this.playerInventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            ItemBuilder builder = new ItemBuilder(itemStack);

            if (itemStack == null || itemStack.getType().isAir()) continue;

            String id = builder.getID();
            if (id == null || !itemMap.containsKey(id)) continue;

            ItemStack template = itemMap.get(id);
            ItemStack restoredItem = this.restore(id, template, itemStack);
            restorableItems.put(i, restoredItem.clone());
        }


        return restorableItems;
    }

    public ItemStack restore(String id, ItemStack template, ItemStack toRestore) {
        ItemMeta meta = template.getItemMeta();

        Component effectiveName = toRestore.effectiveName();
        String formattedId = TextUtils.formatKey(id);
        if (!PlainTextComponentSerializer.plainText().serialize(effectiveName).equalsIgnoreCase(formattedId)) {
            meta.displayName(effectiveName);
        }

        toRestore.getEnchantments().forEach((enchantment, integer) -> {
            if (enchantment == Enchantment.UNBREAKING && integer == 1) return;
            meta.addEnchant(enchantment, integer, true);
        });

        template.setItemMeta(meta);
        template.setAmount(toRestore.getAmount());
        return template;
    }

    public HashMap<String, ItemStack> getItemsMap() {
        HashMap<String, ItemStack> map = new HashMap<>();
        CataclysmItems[] cataclysmItems = CataclysmItems.values();

        for (CataclysmItems item : cataclysmItems) {
            var itemStack = item.build().clone();
            String id = new ItemBuilder(itemStack).getID();
            if (id == null) continue;
            map.put(id, itemStack);
        }

        return map;
    }
}
