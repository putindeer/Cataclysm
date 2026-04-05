package org.cataclysm.global.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandAlias("podium")
@CommandPermission("admin.perms")
public class PodiumCommand extends BaseCommand {

    @Subcommand("playtime get")
    @CommandCompletion("@players")
    @Description("Muestra el tiempo jugado de un jugador.")
    public void onPlaytime(Player sender, @Name("jugador") OfflinePlayer target) {
        long ticksPlayed = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long secondsPlayed = ticksPlayed / 20;
        long hours = secondsPlayed / 3600;
        long minutes = (secondsPlayed % 3600) / 60;
        long seconds = secondsPlayed % 60;

        sender.sendMessage("§a" + target.getName() + " ha jugado: §e" + hours + "h " + minutes + "m " + seconds + "s");
    }

    @Subcommand("playtime top")
    @Description("Muestra el top 10 de jugadores por tiempo jugado.")
    public void onPlaytimeTop(Player sender) {
        var players = Arrays.asList(Bukkit.getOfflinePlayers());
        players.sort((p1, p2) -> {
            int time1 = p2.getStatistic(Statistic.PLAY_ONE_MINUTE);
            int time2 = p1.getStatistic(Statistic.PLAY_ONE_MINUTE);
            return Integer.compare(time1, time2);
        });

        sender.sendMessage("§6§lTop 10 Jugadores por Tiempo Jugado:");
        for (int i = 0; i < Math.min(10, players.size()); i++) {
            OfflinePlayer p = players.get(i);
            long ticksPlayed = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
            long secondsPlayed = ticksPlayed / 20;
            long hours = secondsPlayed / 3600;
            long minutes = (secondsPlayed % 3600) / 60;

            sender.sendMessage("§e#" + (i + 1) + " §f" + p.getName() + " §7- §a" + hours + "h " + minutes + "m");
        }
    }

    @Subcommand("totem no-logro")
    @Description("Muestra la lista de jugadores que no tienen el logro Postmortal y no estén baneados.")
    public void onTotemNoLogro(@NotNull Player sender) {
        var players = Bukkit.getOfflinePlayers();
        List<String> sinLogro = new ArrayList<>();

        Advancement postmortal = Bukkit.getAdvancement(NamespacedKey.minecraft("adventure/totem_of_undying"));
        if (postmortal == null) {
            sender.sendMessage("§cNo se encontró el logro 'Postmortal'. Verifica la versión del servidor.");
            return;
        }

        for (OfflinePlayer p : players) {
            // Saltar jugadores baneados
            if (p.isBanned()) {
                continue;
            }

            boolean completado = false;

            // Solo se puede verificar si está online
            if (p.isOnline()) {
                Player onlinePlayer = p.getPlayer();
                if (onlinePlayer != null) {
                    AdvancementProgress progress = onlinePlayer.getAdvancementProgress(postmortal);
                    completado = progress.isDone();
                }
            }

            if (!completado) {
                sinLogro.add(p.getName());
            }
        }

        if (sinLogro.isEmpty()) {
            sender.sendMessage("§aTodos los jugadores no baneados tienen el logro 'Postmortal'.");
        } else {
            sender.sendMessage("§6§lJugadores sin el logro 'Postmortal' (no baneados):");
            for (String nombre : sinLogro) {
                sender.sendMessage("§e- §f" + nombre);
            }
        }
    }

}
