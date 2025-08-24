package org.cataclysm.game.player.survival.resurrect.mortality;

import org.cataclysm.game.player.data.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public record MortalityManager(PlayerData playerData) {
    public void setValue(float percentage) {
        this.playerData.setMortalityPercentage(percentage);
    }

    public float getValue() {
        return this.playerData.getMortalityPercentage();
    }

    public void decreaseValue(float amount) {
        this.setValue(this.getValue() - amount);
    }

    public @NotNull String getPercentage() {
        DecimalFormat df = new DecimalFormat("#.####");
        float newValue = (this.getValue() * 100);
        newValue = Float.parseFloat(df.format(newValue));
        return (newValue) + "%";
    }
}