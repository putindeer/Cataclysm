package org.cataclysm.game.world.dungeons;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.block.Lectern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.data.StructureLevel;
import org.cataclysm.api.structure.StructureUtils;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.player.survival.advancement.CataclysmAdvancement;

public class ParagonTemple extends CataclysmStructure {
    public ParagonTemple(StructureLevel level) {
        super(level);
        super.listener = new ParagonTempleListener(this);
        super.setUp();
    }

    public ParagonTemple() {
        super("PARAGON_TEMPLE");
        super.listener = new ParagonTempleListener(this);
    }

    @Override
    public String getAdvancement() {
        return "the_beginning/stone_and_divinity";
    }

    static class ParagonTempleListener implements Listener {
        private final ParagonTemple temple;

        public ParagonTempleListener(ParagonTemple temple) {
            this.temple = temple;
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
