package org.cataclysm.game.pantheon.level.audience;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.data.PlayerData;
import org.cataclysm.game.player.tag.role.RoleManager;
import org.cataclysm.game.player.tag.role.RoleType;
import org.cataclysm.game.player.tag.team.TeamManager;
import org.cataclysm.game.player.tag.team.Teams;

import java.util.UUID;

public class AssistanceVerifier {
    private final PantheonOfCataclysm pantheon;

    public AssistanceVerifier(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    public void refuse(Player player) {
        if (hasConfirmed(player)) handleConfirmation(player, false);
    }

    public void check(Player player) {
        if (!hasConfirmed(player)) handleConfirmation(player, true);
        if (medalApply(player, .85)) handleReward(getPlayerData(player));
    }

    public void handleReward(PlayerData data) {
        giveCataclysmMedal(data);
    }

    public void handleConfirmation(Player player, boolean confirm) {
        if (confirm) {
            registerProfile(player);
            setConfirmationStatus(player, true);
            setPlayerRole(getPlayerData(player), RoleType.SURVIVOR);
        } else {
            unregisterProfile(player);
            setConfirmationStatus(player, false);
            setPlayerRole(getPlayerData(player), RoleType.MEMBER);
        }
        setPlayerTeam(getPlayerData(player), Teams.NONE);
    }

    private void giveCataclysmMedal(PlayerData data) {
        pantheon.schedule(() ->
                new TeamManager(data).setTeam(Teams.CATACLYSM_MEDAL), 60);
    }

    private void registerProfile(Player player) {
        Bukkit.getConsoleSender().sendMessage("Registering " + player.getName());
        Bukkit.getConsoleSender().sendMessage(pantheon.getAudience().getSurvivors().values().toString());
        var profile = new PantheonSurvivor(pantheon.getAudience(), player);
        profile.register();
    }

    private void unregisterProfile(Player player) {
        Bukkit.getConsoleSender().sendMessage("Unregistering " + player.getName());
        Bukkit.getConsoleSender().sendMessage(pantheon.getAudience().getSurvivors().values().toString());
        var profile = pantheon.getAudience().getProfile(player.getUniqueId());
        profile.unregister();
    }

    private void setConfirmationStatus(Player player, boolean confirm) {
        PersistentData.set(player, "CONFIRMED", PersistentDataType.BOOLEAN, confirm);
    }

    private boolean hasConfirmed(Player player) {
        return Boolean.TRUE.equals(PersistentData.get(player, "CONFIRMED", PersistentDataType.BOOLEAN));
    }

    private void setPlayerRole(PlayerData data, RoleType type) {
        new RoleManager(data).setRole(type);
    }

    private void setPlayerTeam(PlayerData data, Teams teams) {
        new TeamManager(data).setTeam(teams);
    }

    private PlayerData getPlayerData(Player player) {
        return CataclysmPlayer.getCataclysmPlayer(player).getData();
    }

    private boolean medalApply(Player player, double min) {
        return CataclysmPlayer.getCataclysmPlayer(player).getMortalityManager().getValue() >= min;
    }
}
