package org.cataclysm;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.Soundtrack;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.utils.PantheonWarper;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.time.Duration;

@CommandAlias("finale")
@CommandPermission("admin.perms")
public class FinaleCommand extends BaseCommand {

    @Subcommand("tpall")
    private void tpall(Player sender) {
        Location location = sender.getLocation();

        for (Player online : Bukkit.getOnlinePlayers()) {
            PantheonWarper.teleport(online, location);
        }
    }

    @Subcommand("win")
    @CommandCompletion("@players")
    private void win(Player player) {
        String title = "<gradient:#B89653:#B8A282>Cᴀᴛᴀᴄʟʏꜱᴍ Sᴜʀᴠɪᴠᴏʀ</gradient>";
        String subtitle = "<#C7BEA3>" + TinyCaps.tinyCaps(player.getName()) + " ʜᴀ ꜱᴏʙʀᴇᴠɪᴠɪᴅᴏ ᴀʟ ᴄᴀᴛᴀᴄʟɪꜱᴍᴏ";

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showTitle(
                    Title.title(
                            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(title),
                            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(subtitle),
                            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(1000))
                    )
            );
            online.playSound(online, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1F, .78F);
            online.playSound(online, Sound.ITEM_TRIDENT_THUNDER, 1F, .75F);
            online.playSound(online, Sound.ITEM_TRIDENT_THUNDER, 1F, .55F);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(Dimensions.OVERWORLD.getWorld().getSpawnLocation());
        player.playSound(player, Sound.MUSIC_DISC_CREATOR, 1F, 0.95F);
    }

    @Subcommand("loop")
    private void loop() {
        Soundtrack soundtrack =  new Soundtrack();
        soundtrack.addTrack("FINALE", Key.key("cataclysm.cataclysm.finale"));
        soundtrack.loop(soundtrack.getSound("FINALE"), 409);
    }

    @Subcommand("trophey")
    @CommandCompletion("over|nether|end @players")
    private void trophey(String action, Player player) {
        ItemStack itemStack = null;
        String chaliceName = "";

        switch (action) {
            case "over" -> {
                itemStack = CataclysmItems.OVERWORLDS_CHALICE.build();
                chaliceName = "<gradient:#FFD700:#FFB700>Overworld's Chalice</gradient>";
            }
            case "nether" -> {
                itemStack = CataclysmItems.NETHERS_CHALICE.build();
                chaliceName = "<gradient:#FFD700:#FFB700>Nether's Chalice</gradient>";
            }
            case "end" -> {
                itemStack = CataclysmItems.ENDS_CHALICE.build();
                chaliceName = "<gradient:#FFD700:#FFB700>End's Chalice</gradient>";
            }
            default -> {
                return;
            }
        }
        player.getInventory().addItem(itemStack);
        String title = chaliceName;
        String subtitle = "<gradient:#FFFFFF:#A0A0A0>Un trofeo ha sido entregado</gradient>";
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showTitle(
                    Title.title(
                            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(title),
                            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(subtitle),
                            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
                    )
            );
        }
    }
}
