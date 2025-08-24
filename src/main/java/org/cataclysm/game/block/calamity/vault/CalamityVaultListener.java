package org.cataclysm.game.block.calamity.vault;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.player.survival.advancement.CataclysmAdvancement;

@Registrable
public class CalamityVaultListener implements Listener {

    @EventHandler
    private void onPlayerAtInteract(PlayerInteractAtEntityEvent event) {
        var data = PersistentData.get(event.getRightClicked(), "CUSTOM", PersistentDataType.STRING);
        if (data == null || !data.equals("calamity_vault") || !(event.getRightClicked() instanceof LivingEntity livingEntity)) return;

        var player = event.getPlayer();
        CalamityVault vault = new CalamityVault(livingEntity);

        if (vault.isLocked()) return;

        var keyTry = player.getInventory().getItemInMainHand();
        var hasKey = vault.verify(keyTry);
        if (hasKey) {
            keyTry.setAmount(keyTry.getAmount() - 1);
            vault.open();
            new CataclysmAdvancement("the_nether/greed_is_good").grant(player);
        }
        else player.getWorld().playSound(livingEntity.getLocation(), Sound.BLOCK_VAULT_REJECT_REWARDED_PLAYER, SoundCategory.BLOCKS, 2, 0.825F);
    }

}
