package org.cataclysm.game.pantheon.level.audience;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class PantheonAudience {
    private final HashMap<UUID, PantheonSurvivor> survivors;
    private final AssistanceVerifier assistanceVerifier;

    public PantheonAudience(PantheonOfCataclysm pantheon) {
        this.survivors = new HashMap<>();
        this.assistanceVerifier = new AssistanceVerifier(pantheon);
    }

    public void addSurvivor(PantheonSurvivor survivor) {survivors.put(survivor.getUuid(), survivor);}

    public void removeSurvivor(PantheonSurvivor survivor) {survivors.remove(survivor.getUuid());}

    public int currentPlace() {
        return survivors.size();
    }

    public PantheonSurvivor getProfile(UUID uuid) {return survivors.get(uuid);}

    public void globalAssistanceVerify() {
        for (Player player : Bukkit.getOnlinePlayers())
            assistanceVerifier.check(player);
    }
}
