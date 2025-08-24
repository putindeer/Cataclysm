package org.cataclysm.api.item.crafting.util;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.cataclysm.api.listener.registrable.Registrable;

@Registrable
public class CraftingEvent implements Listener {

    @EventHandler
    private static void removeCustomRecipeItems(CraftItemEvent event){
        CraftingInventory inventory = event.getInventory();
        if (inventory.getResult() == null) return;

        if (event.isShiftClick()) CraftingUtils.removeCustomItemWithShiftClick(inventory, (Player) event.getWhoClicked());
        else CraftingUtils.removeCustomItemsNoShiftClick(inventory);
    }

    @EventHandler
    private static void onPlayerCraftItem(PrepareItemCraftEvent event){
        if (event.getInventory().getResult() != null) {
            CraftingUtils.checkCrafts(event.getInventory());
        }
    }

}
