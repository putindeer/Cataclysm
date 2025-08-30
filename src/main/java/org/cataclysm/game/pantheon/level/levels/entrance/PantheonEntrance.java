package org.cataclysm.game.pantheon.level.levels.entrance;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.levels.PantheonLevel;
import org.cataclysm.game.pantheon.level.levels.entrance.preparation.PreparationCount;
import org.cataclysm.game.pantheon.level.levels.PantheonStates;
import org.cataclysm.game.world.Dimensions;

@Getter
public class PantheonEntrance extends PantheonLevel {
    private final PreparationCount startCount;

    public PantheonEntrance(PantheonOfCataclysm pantheon) {
        super(pantheon);
        this.state = PantheonStates.WAITING;
        this.fastStart = true;
        this.startCount = new PreparationCount(pantheon.getAudience());
        super.thread = new EntranceThread(pantheon);
    }

    private void open() {
        this.dispatcher.playSounds(Sound.sound(Key.key("item.trident.return"), Sound.Source.MASTER, 5F, 0.56F));
        this.thread.handle();
    }

    private void dialogue() {
        int coord = getLocation().getBlockX();
        this.dispatcher.sendActionBar("Las puertas del abismo se reabren al fin.");
        this.dispatcher.sendActionBar("En las lejanías...", 800, 600);
        this.dispatcher.sendActionBar("en tierras muertas...", 800, 600);
        this.dispatcher.sendActionBar("En " + coord + ", " + coord + ".");
        this.dispatcher.setAcumulatedMillis(0);
    }

    private void paralize(int duration) {
        this.dispatcher.playSounds(
                Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.MASTER, 5F, 0.56F),
                Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.MASTER, 5F, 0.5F)
        );
        this.dispatcher.addEffects(
                new PotionEffect(ImmunityEffect.EFFECT_TYPE, duration, 0, false, false),
                new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false),
                new PotionEffect(PotionEffectType.SLOWNESS, duration, 9, false, false)
        );
    }

    @Override
    public void onStart() {
        this.paralize(100);
        super.pantheon.schedule(this::dialogue, 60);
        super.pantheon.schedule(this::open, 250);
    }

    @Override
    public void onStop() {}

    @Override
    public Location location() {return getLocation();}

    private static @Getter Location location = new Location(Dimensions.PALE_VOID.getWorld(), 1000.5, 140, 1000.5);
}
