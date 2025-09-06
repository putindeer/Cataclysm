package org.cataclysm.game.events.pantheon.orchestrator.fountain;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter @Setter
public class StatusNotifier {
    private ScheduledFuture<?> loop;

    private int maxPlayers;
    private int ready;

    private final PantheonOfCataclysm pantheon;
    private final PantheonFountain fountain;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public StatusNotifier(PantheonFountain fountain) {
        this.fountain = fountain;
        this.pantheon = fountain.getPantheon();
        update();
    }

    public void startLoop() {
        stopLoop();
        loop = pantheon.getExecutor().scheduleAtFixedRate(
                () -> Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::tick),
                0, 1, TimeUnit.SECONDS
        );
    }

    public void stopLoop() {
        if (loop != null) {
            loop.cancel(true);
            loop = null;
        }
    }

    public void update() {
        maxPlayers = Bukkit.getOnlinePlayers().size();
        ready = getPreparedPlayers().size();
        if (ready >= maxPlayers && maxPlayers > 0) {
            fountain.stop();
        }
        tick();
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonProfile profile = PantheonProfile.fromPlayer(pantheon, player);
            if (profile.isAlive()) {
                player.sendActionBar(getDisplay("<#AD8757>"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false));
            } else {
                player.sendActionBar(getDisplay("<#7D7D7D>"));
            }
        }
    }

    private Component getDisplay(String initColor) {
        String display = "<#CCCCCC>" + ready + initColor + "/" +
                "<#CCCCCC>" + maxPlayers + initColor + " restantes para el ascenso";
        return mm.deserialize(display);
    }

    private List<Player> getPreparedPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(p -> PantheonProfile.fromPlayer(pantheon, p))
                .filter(PantheonProfile::isReady)
                .map(PantheonProfile::getPlayer)
                .collect(Collectors.toList());
    }
}