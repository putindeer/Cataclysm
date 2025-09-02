package org.cataclysm.game.events.pantheon.boss.twisted_warden.abilities;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.twisted_warden.PantheonWarden;

public class BonfirePantheonAbility extends PantheonAbility {
    private final PantheonWarden warden;

    public BonfirePantheonAbility(PantheonWarden warden) {
        super(Material.CAMPFIRE, "Bonfire", 1);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = this.warden.getController();
        var location = controller.getLocation();
        controller.playSound(location, Sound.BLOCK_ANVIL_PLACE, 5F, 0.5F);
        controller.playSound(location, Sound.BLOCK_ANVIL_USE, 5F, 0.77F);
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, this.channelTime * 20, 2));
    }

    @Override
    public void cast() {
        var controller = this.warden.getController();
        var location = controller.getLocation();
        controller.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 5F, 1);
        controller.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 5F, 0.65F);
        this.warden.resetAbilityCooldown();
        this.warden.setBoosted(true);
    }
}
