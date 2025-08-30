package org.cataclysm.game.pantheon.level.levels.treehouse;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.levels.PantheonLevel;
import org.cataclysm.game.pantheon.level.levels.entrance.PantheonEntrance;
import org.cataclysm.game.pantheon.level.timer.PantheonTimer;
import org.cataclysm.game.pantheon.level.levels.PantheonStates;
import org.cataclysm.game.world.Dimensions;

public class TreeHouseWaiting extends PantheonLevel {
    private final PantheonTimer timer;

    public TreeHouseWaiting(PantheonOfCataclysm pantheon) {
        super(pantheon);
        this.state = PantheonStates.WAITING;
        this.fastStart = true;
        this.timer = new PantheonTimer(pantheon, 3600);
        this.timer.setDisplay("Las puertas del panteón se abriran en: ##:##");
        this.timer.setStopTask(this::onStop);
    }

    @Override
    public void onStart() {
        this.timer.start();
        this.dispatcher.playSounds(
                Sound.sound(Key.key("item.trident.return"), Sound.Source.MASTER, 5F, 0.5F)
        );
    }

    @Override
    public void onStop() {
        pantheon.startLevel(new PantheonEntrance(pantheon));
    }

    @Override
    public Location location() {return location;}

    private static @Getter Location location = Dimensions.PALE_VOID.getWorld().getSpawnLocation();
}
