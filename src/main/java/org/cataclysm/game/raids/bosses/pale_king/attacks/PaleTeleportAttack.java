package org.cataclysm.game.raids.bosses.pale_king.attacks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.raids.bosses.pale_king.PaleKing;

import java.util.List;

public class PaleTeleportAttack extends PaleAttack {
    public PaleTeleportAttack(PaleKing king) {
        super(king, Material.ENDER_PEARL, "Pale Teleport", 1, 3);
    }

    @Override
    public void channel() {
        Player controller = super.king.getController();
        controller.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (super.channelTime * 20) + 10, 0, false, false));

        assert Cataclysm.getBoss() != null;
        List<Ability> abilities = Cataclysm.getBoss().getAbilityManager().getAbilities();
        abilities.forEach(ability -> controller.setCooldown(ability.getTrigger().getType(), (this.channelTime * 20) + 10));
        controller.setCooldown(super.king.getSword(), this.channelTime * 20);

        super.king.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, .6F);
        super.king.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, 1.6F);

        CataclysmArea area = super.king.getArena();
        Location location = area.getRandomLocations(1).getFirst();

        super.king.castPaleExplosion(controller.getLocation(), 7);
        this.playWarpSound();

        controller.teleport(location);
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, this.channelTime * 20, 9, false, false));
        super.king.castPaleExplosion(location, 8);
    }

    @Override
    public void cast() {
        this.playWarpSound();
    }

    private void playWarpSound() {
        super.king.playSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 4F, 1.76F);
        super.king.playSound(Sound.ITEM_TRIDENT_RIPTIDE_2, 4F, .76F);
        super.king.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, .6F);
        super.king.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, 1.6F);
    }
}
