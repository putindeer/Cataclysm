package org.cataclysm.game.events.pantheon.bosses.calamity_hydra.rage;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@Getter @Setter
public class PantheonRageBar {
    private final PantheonRage rage;
    private final BossBar bossBar;

    public PantheonRageBar(PantheonRage rage) {
        this.bossBar = BossBar.bossBar(Component.text(), 0, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        this.rage = rage;
    }

    public void setVisibility(boolean visibility) {
        Audience audience = Audience.audience(Bukkit.getOnlinePlayers());
        if (visibility) this.bossBar.addViewer(audience);
        else this.bossBar.removeViewer(audience);
    }

    public void tick(float percentage) {
        this.bossBar.name(this.getRageBarName(percentage));
        if (!this.rage.getHydra().isOutraged()) this.bossBar.progress(percentage);
    }

    private @NotNull Component getRageBarName(float percentage) {
        String display = !this.rage.getHydra().isOutraged()
                ? "<#50345e>Furia" : "<gradient:#581787:#8d55b5>OUTRAGED</gradient>";

        DecimalFormat df = new DecimalFormat("#.##");
        String format = df.format(percentage * 100);
        String fire = this.interpolateHex("#fcfcfc", "#581787", percentage) + "\uD83D\uDD25";

        return MiniMessage.miniMessage().deserialize(fire + " " + display + ": <#725182>" + format + "<#50345e>% " + fire);
    }

    private String interpolateHex(String hex1, String hex2, double t) {
        t = Math.max(0.0, Math.min(1.0, t));

        hex1 = hex1.replace("#", "");
        hex2 = hex2.replace("#", "");

        int r1 = Integer.parseInt(hex1.substring(0, 2), 16);
        int g1 = Integer.parseInt(hex1.substring(2, 4), 16);
        int b1 = Integer.parseInt(hex1.substring(4, 6), 16);

        int r2 = Integer.parseInt(hex2.substring(0, 2), 16);
        int g2 = Integer.parseInt(hex2.substring(2, 4), 16);
        int b2 = Integer.parseInt(hex2.substring(4, 6), 16);

        int r = (int) Math.round(r1 + (r2 - r1) * t);
        int g = (int) Math.round(g1 + (g2 - g1) * t);
        int b = (int) Math.round(b1 + (b2 - b1) * t);

        return "<" + String.format("#%02X%02X%02X", r, g, b) + ">";
    }
}
