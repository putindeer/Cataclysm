package org.cataclysm.game.player.mechanics.upgrade;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.player.mechanics.upgrade.event.PlayerUpgradeLemegetonEvent;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.cataclysm.global.utils.text.TextUtils;
import org.cataclysm.global.utils.text.font.TinyCaps;

@Registrable
public class UpgradeListener implements Listener {

    @EventHandler
    private void onPlayerUpgradeLemegeton(PlayerUpgradeLemegetonEvent event) {
        var player = event.getPlayer();
        var upgrade = event.getUpgrade();
        var level = event.getLevel();

        var cataclysmColor = ChatMessenger.getCataclysmColor();
        var textColor = ChatMessenger.getTextColor();

        var upgradeDisplay = TinyCaps.tinyCaps(TextUtils.formatKey(upgrade.name()));
        var message = " ha mejorado " + cataclysmColor + upgradeDisplay + textColor + " a nivel " + cataclysmColor + level + textColor + ".";
        if (level == 1) message = " ha desbloqueado la mejora " + cataclysmColor + upgradeDisplay + textColor + ".";

        ChatMessenger.broadcastMessage(cataclysmColor + player.getName() + textColor + message);
    }

}
