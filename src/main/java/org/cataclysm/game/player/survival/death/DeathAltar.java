package org.cataclysm.game.player.survival.death;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.raids.bosses.pale_king.PaleKing;
import org.jetbrains.annotations.NotNull;

public class DeathAltar {
    private final SchematicLoader schematicLoader;
    private final Location location;
    private final Player player;

    public DeathAltar(@NotNull Player player) {
        this.schematicLoader = new SchematicLoader("altar/schematics/death_altar.schem");
        this.location = player.getLocation();
        this.player = player;
    }

    public void placeSimple() {
        if (Cataclysm.getBoss() != null && Cataclysm.getBoss() instanceof PaleKing paleKing && paleKing.phase.getCurrent() > 1) return;

        this.location.clone().getBlock().setType(Material.NETHER_BRICK_FENCE);
        this.location.clone().add(0, -1, 0).getBlock().setType(Material.BEDROCK);

        var block = this.location.clone().add(0, 1, 0).getBlock();
        block.setType(Material.PLAYER_HEAD, false);
        var state = block.getState();
        if (!(state instanceof Skull skull)) return;

        skull.setOwningPlayer(Bukkit.getOfflinePlayer(this.player.getName()));
        skull.update(true);

        if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))) return;
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            this.generateDoubleChestWithPlayerInventory(this.player, this.location.clone().add(1, 0, 0));
            this.player.getInventory().clear();
        }, 40L);
    }

    public void placeComplex() {
        if (this.schematicLoader.getFile() == null) return;

        this.schematicLoader.pasteSchematic(this.location, true);

        var block = this.location.clone().add(0, 1, 0).getBlock();
        block.setType(Material.PLAYER_HEAD, false);
        var state = block.getState();
        if (!(state instanceof Skull skull)) return;

        skull.setOwningPlayer(Bukkit.getOfflinePlayer(this.player.getName()));
        skull.update(true);

        if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))) return;
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            this.generateDoubleChestWithPlayerInventory(this.player, this.location.clone().add(1, 0, 0));
            this.player.getInventory().clear();
        }, 40L);
    }

    private void generateDoubleChestWithPlayerInventory(Player player, Location location) {
        World world = location.getWorld();
        if (world == null) return;

        Block leftChest = world.getBlockAt(location);
        Block rightChest = world.getBlockAt(location.clone().add(0, 0, -1));

        try {
            leftChest.setType(Material.CHEST);
            rightChest.setType(Material.CHEST);

            Chest leftChestData = (Chest) leftChest.getBlockData();
            Chest rightChestData = (Chest) rightChest.getBlockData();

            leftChestData.setType(Chest.Type.LEFT);
            rightChestData.setType(Chest.Type.RIGHT);

            leftChestData.setFacing(BlockFace.WEST);
            rightChestData.setFacing(BlockFace.WEST);

            leftChest.setBlockData(leftChestData);
            rightChest.setBlockData(rightChestData);

            leftChest.getState().update(true);
            rightChest.getState().update(true);

            if (leftChest.getState() instanceof org.bukkit.block.Chest leftChestState) {
                Inventory doubleChestInventory = leftChestState.getInventory();
                copyPlayerInventoryToChest(player, doubleChestInventory);
            }

        } catch (Exception ignored) {}
    }

    private void copyPlayerInventoryToChest(Player player, org.bukkit.inventory.Inventory chestInventory) {
        ItemStack[] playerItems = player.getInventory().getContents();
        int chestSize = chestInventory.getSize(); // Double chest has 54 slots

        // Copy items from player inventory to chest
        for (int i = 0; i < Math.min(playerItems.length, chestSize); i++) {
            if (playerItems[i] != null) {
                chestInventory.setItem(i, playerItems[i]);
            }
        }
    }
}
