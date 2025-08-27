package org.cataclysm.game.world.dungeons;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.block.Lectern;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.StructureUtils;
import org.cataclysm.api.structure.data.StructureLevel;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.player.survival.advancement.CataclysmAdvancement;

import java.util.List;

public class PaleTemple extends CataclysmStructure {
    public PaleTemple(StructureLevel level) {
        super(level);
        super.listener = new PaleTempleListener(this);
        super.setUp();
    }

    public PaleTemple() {
        super("PALE_TEMPLE");
        super.listener = new PaleTempleListener(this);
    }

    @Override
    public String getAdvancement() {
        return "the_beginning/stone_and_divinity";
    }

    static class PaleTempleListener implements Listener {
        private final PaleTemple temple;

        public PaleTempleListener(PaleTemple temple) {
            this.temple = temple;
        }

        @EventHandler(priority = EventPriority.LOWEST)
        private void onCreatureSpawn(CreatureSpawnEvent event) {
            LivingEntity entity = event.getEntity();

            var reason = event.getSpawnReason();
            List<CreatureSpawnEvent.SpawnReason> permittedReasons = List.of(
                    CreatureSpawnEvent.SpawnReason.COMMAND,
                    CreatureSpawnEvent.SpawnReason.SPAWNER,
                    CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);

            if (permittedReasons.contains(reason) || entity instanceof ArmorStand) return;
            if (StructureUtils.isEntityInStructure(entity, this.temple)) entity.remove();
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            var player = event.getPlayer();
            if (event.getClickedBlock() == null) return;

            var block = event.getClickedBlock();
            if (block.getType() != Material.LECTERN) return;

            if (!StructureUtils.isEntityInStructure(player, this.temple)) return;

            var lectern = (Lectern) block.getState();
            var book = lectern.getInventory().getItem(0);

            if (book == null || book.getType() != Material.WRITTEN_BOOK) return;

            var meta = (BookMeta) book.getItemMeta();

            var title = meta.getTitle();
            var author = meta.getAuthor();

            if (title == null || author == null || !title.equalsIgnoreCase("cataclysm_tome") || !author.equalsIgnoreCase("pepen3012")) return;

            var item = player.getWorld().dropItem(player.getLocation(), CataclysmItems.LEMEGETON.build());
            item.setPickupDelay(0);

            player.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.BLOCK, 1.0F, 0.5F));
            player.playSound(Sound.sound(Key.key("entity.elder_guardian.ambient"), Sound.Source.BLOCK, 1.0F, 0.5F));

            block.getWorld().playSound(block.getLocation(), org.bukkit.Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 0.75F, 0.8F);
            block.setType(Material.AIR);

            new CataclysmAdvancement("the_beginning/open_book").grant(player);
        }

    }
}
