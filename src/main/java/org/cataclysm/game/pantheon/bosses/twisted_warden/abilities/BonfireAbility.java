package org.cataclysm.game.pantheon.bosses.twisted_warden.abilities;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.boss.ability.AbilityBooster;
import org.cataclysm.game.pantheon.bosses.twisted_warden.PantheonWarden;

public class BonfireAbility extends AbilityBooster {
    private final PantheonWarden warden;

    public BonfireAbility(PantheonWarden warden) {
        super(Material.CAMPFIRE, "Bonfire", 1, 100);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        controller.playSound(location, Sound.BLOCK_ANVIL_PLACE, 1, 0.5F);
        controller.playSound(location, Sound.BLOCK_ANVIL_USE, 1, 0.77F);

        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, this.channelTime * 20, 2));
    }

    @Override
    public void cast() {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        controller.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.5F, 1);
        controller.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.5F, 0.65F);

        this.warden.resetAbilityCooldown();
        this.warden.setBoosted(true);
    }
}
