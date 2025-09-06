package org.cataclysm.game.events.pantheon.bosses.twisted_warden.abilities;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.twisted_warden.PantheonWarden;
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
        double range = 30 * super.amplifier;
        double damage = 60 * super.amplifier;
        int radius = (int) (2 * super.amplifier);
        float pitch = (float) (0.75F + (0.2 * super.amplifier));

        var location = this.warden.getController().getLocation();
        var world = location.getWorld();

        world.playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 6F, pitch);
        world.playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 6F, pitch);

        this.warden.shriek(range, damage, radius);
    }
}
