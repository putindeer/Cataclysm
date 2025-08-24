package org.cataclysm.api.inventory;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class BasicMenu implements Listener, Cloneable {
    protected final Inventory inventory;
    protected final Player player;
    protected final String owner;

    public BasicMenu(int size, String name, Player player, String owner) {
        this.inventory = Bukkit.createInventory(player, size, MiniMessage.miniMessage().deserialize(name));
        this.player = player;
        this.owner = owner;

        this.initInventory();
        Bukkit.getServer().getPluginManager().registerEvents(this, Cataclysm.getInstance());
    }

    public void open() {this.open(player);}
    public void open(@NotNull Player player) {player.openInventory(inventory);}

    public void close() {this.close(player);}
    public void close(@NotNull Player player) {player.closeInventory();}

    public void addItem(@NotNull ItemBuilder itemBuilder) {this.addItem(itemBuilder.build());}
    public void addItem(ItemStack itemStack) {inventory.addItem(itemStack);}

    public void setItem(@NotNull ItemBuilder itemBuilder, int slot) {this.setItem(itemBuilder.build(), slot);}
    public void setItem(ItemStack itemStack, int slot) {inventory.setItem(slot, itemStack);}

    public void setRow(@NotNull ItemBuilder itemBuilder, int from, int to) {this.setRow(itemBuilder.build(), from, to);}
    public void setRow(ItemStack itemStack, int from, int to) {for (int i = from; i <= to; ++i) inventory.setItem(i, itemStack);}
    public void setRow(Material material, int from, int to) {for (int i = from; i <= to; ++i) inventory.setItem(i, new ItemStack(material));}

    public void setItems(@NotNull ItemBuilder itemBuilder, @NotNull int... slots) {this.setItems(itemBuilder.build(), slots);}
    public void setItems(ItemStack itemStack, int @NotNull ... slots) {for (int i = 0; i <= slots.length-1; ++i) inventory.setItem(slots[i], itemStack);}

    public void setOutline(@NotNull ItemBuilder itemBuilder) {this.setOutline(itemBuilder.build());}
    public void setOutline(ItemStack itemStack) {
        setRow(itemStack, 0, 8);
        setRow(itemStack, 45, 53);
        setItems(itemStack, 9, 17, 18, 26, 27, 35, 36, 44);
    }

    public boolean isFull() {
        for (int i = 0; i < inventory.getSize(); i++) if (inventory.getItem(i) == null) return false;
        return true;
    }

    public void empty() {for (int i = 0; i < inventory.getSize(); i++) setItem(new ItemStack(Material.AIR), i);}

    public abstract void initInventory();

    public abstract void click(InventoryClickEvent event);
    public void update(MenuUpdateEvent event) {}
    public void open(InventoryOpenEvent event) {}
    public void close(InventoryCloseEvent event) {}

    @Override
    public BasicMenu clone() {
        try {
            return (BasicMenu) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
