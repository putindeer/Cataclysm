package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;

import java.time.Duration;

public record PantheonHydraEvents(PantheonHydra hydra) {

    public void handlePhaseElapse(int currentPhase, int currentHealth) {
        if (currentPhase == 0) this.castPhase(1);
        if (currentPhase == 1 && currentHealth <= 0) this.castPhase(2);
        if (currentPhase == 2 && currentHealth < 18000) this.castPhase(3);
    }

    public void handleHeadDecapitation(int currentHeads, int currentHealth) {
        if (currentHeads <= 0) {
            this.castDeath();
            return;
        }
        int[] thresholds = {0, 5000, 10000, 15000, 20000};
        if (currentHeads <= thresholds.length && currentHealth <= thresholds[currentHeads - 1]) {
            this.castDecapitation(currentHeads - 1);
        }
    }


    private void castPhase(int phase) {
        this.hydra.getSoundtrack().stopAll();
        this.hydra.setElapsing(true);
        switch (phase) {
            case 1 -> this.castFirstPhase();
            case 2 -> this.castSecondPhase();
            case 3 -> this.castThirdPhase();
        }
    }

    private void castThirdPhase() {
        this.hydra.changeHeads(2, 3);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            this.changePhase(3);
            this.hydra.changeHeads(5, 5);
            this.hydra.setElapsing(false);
            this.hydra.setHealth(this.hydra.getMaxHealth());
            this.hydra.getSoundtrack().loop("PHASE_3", 383);
        }, 80);
    }

    private void castSecondPhase() {
        this.hydra.changeHeads(0, 1);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            this.changePhase(2);
            this.hydra.changeHeads(3, 3);
            this.hydra.setElapsing(false);
            this.hydra.getSoundtrack().loop("PHASE_2", 383); //TODO CHANGE DURATION
        }, 80);
    }

    private void castFirstPhase() {
        this.changePhase(1);
        this.hydra.changeHeads(1, 1);
        this.hydra.getSoundtrack().loop("PHASE_1", 251);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(
                        MiniMessage.miniMessage().deserialize("<#ab8559><obf>||<reset> <gradient:#a8512f:#b07c4c>Calamity Hydra <#ab8559><obf>||"),
                        MiniMessage.miniMessage().deserialize("<#d4a674>ᴍɪɢʜᴛʏ ɢᴏᴅ ᴏꜰ ᴄᴀʟᴀᴍɪᴛɪᴇꜱ"),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2))
                ));
                player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1.57F, 0.65F);
                player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1.57F, 0.5F);
            }
            this.hydra.setElapsing(false);
        }, 60);
    }


    private void castDecapitation(int headsLeft) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize("<#b0794c>¡Cabeza Decapitada!"),
                    MiniMessage.miniMessage().deserialize("<#949494>ǫᴜᴇᴅᴀɴ <#ffffff>" + headsLeft + "<#949494>/5 ᴄᴀʙᴇᴢᴀꜱ ᴘᴏʀ ᴅᴇᴄᴀᴘɪᴛᴀʀ"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2))));
            player.playSound(player, Sound.ITEM_TRIDENT_THUNDER, 2F, .55F);
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 3F, 0.75F);
        });
        this.hydra.changeHeads(headsLeft, 5);
    }

    private void castDeath() {
        this.changePhase(0);
        this.hydra.changeHeads(0, 5);
        this.hydra.getSoundtrack().stopAll();
    }

    private void changePhase(int phase) {
        double amplifier = (this.hydra.getPhase() * 0.3);
        double initScale = 5.0;
        double scale = initScale + (initScale * amplifier);
        this.hydra.setHealth(this.hydra.getMaxHealth());
        this.hydra.setAttribute(Attribute.SCALE, scale);
        this.hydra.setPhase(phase);
    }
}
