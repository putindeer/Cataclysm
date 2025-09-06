package org.cataclysm.game.events.pantheon.bosses.void_lord.moves.attacks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAttack;

import java.util.List;

public class HeartTeleportAttack extends HeartAttack {
    public HeartTeleportAttack(VoidLord lord) {
        super(lord, Material.ENDER_PEARL, "Heart Teleport", 1, 3);
    }

    @Override
    public void channel() {
        Player controller = this.lord.getController();
        controller.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (super.channelTime * 20) + 10, 0, false, false));

        List<Ability> abilities = this.lord.getAbilityManager().getAbilities();
        abilities.forEach(ability -> controller.setCooldown(ability.getTrigger().getType(), (this.channelTime * 20) + 10));
        controller.setCooldown(this.lord.getSword(), this.channelTime * 20);

        this.lord.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, .6F);
        this.lord.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, 1.6F);

        CataclysmArea area = this.lord.getArena();
        Location location = area.getRandomLocations(1).getFirst();

        this.lord.createExplosion(controller.getLocation(), 7);
        this.playWarpSound();

        controller.teleport(location);
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, this.channelTime * 20, 9, false, false));
        this.lord.createExplosion(location, 8);
    }

    @Override
    public void cast() {
        this.playWarpSound();
    }

    private void playWarpSound() {
        this.lord.playSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 4F, 1.76F);
        this.lord.playSound(Sound.ITEM_TRIDENT_RIPTIDE_2, 4F, .76F);
        this.lord.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, .6F);
        this.lord.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, 1.6F);
    }
}
