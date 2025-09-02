package org.cataclysm.game.events.pantheon.boss.twisted_warden.abilities;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.twisted_warden.PantheonWarden;
import org.jetbrains.annotations.NotNull;

public class TwistedShriekPantheonAbility extends PantheonAbility {
    private final PantheonWarden warden;

    public TwistedShriekPantheonAbility(@NotNull PantheonWarden warden) {
        super(Material.ECHO_SHARD, "Twisted Shriek", 2);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = this.warden.getController();
        var location = controller.getLocation();
        controller.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 5.5F, 1);
        controller.playSound(location, Sound.ENTITY_WARDEN_SONIC_CHARGE, 5F, 0.88F);
        controller.playSound(location, Sound.ENTITY_WARDEN_SONIC_CHARGE, 5F, 0.5F);
    }

    @Override
    public void cast() {
        var controller = this.warden.getController();

        var range = 30;
        var damage = 60;
        var radius = 2;
        var pitch = 0.85F;

        if (super.isBoosted()) {
            range *= 2;
            damage *= 2;
            radius *= 2;
            pitch *= 1.5F;
        }

        var world = controller.getWorld();
        var location = controller.getLocation();

        world.playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 6F, pitch);
        world.playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 6F, pitch);

        this.warden.shriek(range, damage, radius);
    }
}
