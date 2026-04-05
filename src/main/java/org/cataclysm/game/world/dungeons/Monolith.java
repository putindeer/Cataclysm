package org.cataclysm.game.world.dungeons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.StructureUtils;
import org.cataclysm.api.structure.data.StructureLevel;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.player.survival.advancement.AdvancementChecker;

import java.util.Random;

public class Monolith extends CataclysmStructure {
    public static final int CHALLENGE_GOAL = 300;

    public Monolith(StructureLevel level) {
        super(level);
        super.listener = new MonolithListener(this);
        super.setUp();
    }

    public Monolith() {
        super("MONOLITH");
        super.listener = new MonolithListener(this);
    }

    @Override
    public String getAdvancement() {
        return "the_twisted/all_wise_men_fear";
    }

    static class MonolithListener implements Listener {
        private final Monolith monolith;

        public MonolithListener(Monolith monolith) {
            this.monolith = monolith;
        }

        @EventHandler
        private void onDeath(EntityDeathEvent event) {
            var livingEntity = event.getEntity();
            var id = CataclysmMob.getID(livingEntity);

            var location = livingEntity.getLocation();
            if (!StructureUtils.isLocationInStructure(location, this.monolith)) return;

            if (id == null || !id.equalsIgnoreCase("ArcaneSculpture")) return;

            var killer = livingEntity.getKiller();
            new AdvancementChecker(killer).addChallengeProgress(this.monolith);
        }

        @EventHandler
        public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
            var bucket = event.getBucket();
            var location = event.getBlock().getLocation();

            if (!StructureUtils.isLocationInStructure(location, this.monolith)) return;
            if (bucket == Material.LAVA_BUCKET) event.setCancelled(true);
        }

        @EventHandler
        public void onBlockPlaceEvent(BlockPlaceEvent event) {
            var block = event.getBlock();
            var location = block.getLocation();

            if (!StructureUtils.isLocationInStructure(location, this.monolith)) return;

            var type = block.getType();

            if (type == Material.CHISELED_RESIN_BRICKS) event.setCancelled(true);
        }

        @EventHandler
        public void onBlockBreakEvent(BlockBreakEvent event) {
            var block = event.getBlock();
            var location = block.getLocation();
            var player = event.getPlayer();
            var mainHand = player.getInventory().getItemInMainHand();
            var handMeta = mainHand.getItemMeta();
            var type = block.getType();

            short totalNuggets = 1;

            if (!StructureUtils.isLocationInStructure(location, this.monolith)) return;

            if (type == Material.SPAWNER
                    || type == Material.CREAKING_HEART
                    || type == Material.CHISELED_RESIN_BRICKS
            ) new AdvancementChecker(player).addChallengeProgress(this.monolith);

            if (type != Material.CHISELED_RESIN_BRICKS || player.getPotionEffect(DisperEffect.EFFECT_TYPE) != null) return;

            if (handMeta != null) {
                if (handMeta.isUnbreakable()) {
                    if (mainHand.getType().equals(Material.NETHERITE_PICKAXE)) totalNuggets = (short) (1 + new Random().nextInt(0, 2));
                }
            }

            var arcaneNuggets = CataclysmItems.ARCANE_NUGGET.build().clone();
            arcaneNuggets.setAmount(totalNuggets);

            var world = location.getWorld();
            world.playSound(location, Sound.BLOCK_BASALT_BREAK, SoundCategory.BLOCKS, 1, 0.65F);
            world.dropItemNaturally(location, arcaneNuggets);
            event.setDropItems(false);
        }
    }
}
