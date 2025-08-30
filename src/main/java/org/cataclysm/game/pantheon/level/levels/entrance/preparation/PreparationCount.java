package org.cataclysm.game.pantheon.level.levels.entrance.preparation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.pantheon.level.audience.PantheonAudience;
import org.cataclysm.game.pantheon.level.audience.PantheonSurvivor;
import org.cataclysm.game.pantheon.level.audience.PlayerStatus;

import java.util.ArrayList;
import java.util.List;

public class PreparationCount {
    public int maxPlayers;
    public int ready;

    private final PantheonAudience audience;

    public PreparationCount(PantheonAudience audience) {
        this.audience = audience;
        this.update();
    }

    public void update() {
        this.maxPlayers = audience.getSurvivors().size();
        this.ready = getPreparedPlayers().size();
    }

    public void tick() {
        for (PantheonSurvivor survivor : audience.getSurvivors().values()) {
            Player player = survivor.getPlayer();
            if (player == null) continue;

            if (survivor.getStatus() == PlayerStatus.PREPARED) {
                player.sendActionBar(getDisplay("<#AD8757>"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false));
            }
            else {
                player.sendActionBar(getDisplay("<#7D7D7D>"));
            }
        }
    }

    public Component getDisplay(String initcolor) {
        String display = "<#CCCCCC>" + ready + initcolor + "/<#CCCCCC>" + maxPlayers + initcolor + " restantes para el ascenso";
        return MiniMessage.miniMessage().deserialize(display);
    }

    private List<Player> getPreparedPlayers() {
        List<Player> list = new ArrayList<>();
        for (PantheonSurvivor survivor : audience.getSurvivors().values()) {
            if (survivor.getStatus() == PlayerStatus.PREPARED) list.add(survivor.getPlayer());
        }
        return list;
    }
}
