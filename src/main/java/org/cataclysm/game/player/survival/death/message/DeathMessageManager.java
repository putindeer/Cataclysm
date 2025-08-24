package org.cataclysm.game.player.survival.death.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.player.data.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Objects;

public record DeathMessageManager(PlayerData data) {
    public void setDeathMessage(String message) {
        this.data.setDeathMessage(message);
    }

    public @NotNull String getDeathMessage() {
        return Objects.requireNonNullElse(this.data.getDeathMessage(), "Al fin descanza en paz.");
    }

    public @NotNull Component getFormattedChatMessage() {
        String deathMessage = this.getDeathMessage().toLowerCase();
        switch (this.data.getNickname()) {
            case "xShanty" -> {
                LocalDate today = LocalDate.now();
                String day = new DecimalFormat("00").format(today.getDayOfMonth()) + "/" + new DecimalFormat("00").format(today.getMonthValue());
                deathMessage = "¿9/12 o " + day + "?";
            }
            case "LeitoMC" -> {
                deathMessage = "you were eliminated.\n" +
                        "   <gray>→ Survived for <white>" + Cataclysm.getDay() + "d<gray>.\n" +
                        "   → Outlived <white>" + Cataclysm.getGameManager().data().getDeathCount() + " <gray>players.\n" +
                        "   → Eliminated <white>0 <gray>players.";
            }
        }
        return MiniMessage.miniMessage().deserialize("<#696969>" + this.data.getNickname() + ", " + deathMessage);
    }
}
