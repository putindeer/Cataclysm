package org.cataclysm.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DiscordMessenger {

    public static void sendChatMessage(@NotNull Player sender, Component message) {
        var embedMessage = new EmbedBuilder()
                .setAuthor(sender.getName())
                .setDescription(PlainTextComponentSerializer.plainText().serialize(message));

        var channel = DiscordChannels.CHAT_LOG.getTextChannel();
        if (channel == null) return;
        channel.sendMessageEmbeds(embedMessage.build()).queue();
    }

    public static void sendDeathMessage(@NotNull Player player, Component deathMessage, @NotNull Location location) {
        var causeText = deathMessage != null
                ? PlainTextComponentSerializer.plainText().serialize(deathMessage)
                : "Muerte desconocida";

        var embedMessage = new EmbedBuilder()
                .setAuthor(player.getName())
                .setColor(Color.RED)
                .addField(":watch: Fecha:", "[ <t:" + System.currentTimeMillis() / 1000 + ":R> ]", true)
                .addField(":skull: Causa:", causeText, true)
                .addField(":compass: Coords:", "[" + location.getWorld().getName() + "] | X: " + location.getBlockX() + " | Y: " + location.getBlockY() + " | Z: " + location.getBlockZ(), true);

        var death = DiscordChannels.DEATH_LOG.getTextChannel();
        if (death != null) death.sendMessageEmbeds(embedMessage.build()).queue();

        var chat = DiscordChannels.CHAT_LOG.getTextChannel();
        if (chat != null) chat.sendMessageEmbeds(embedMessage.build()).queue();
    }

    public static void sendTotemMessage(@NotNull Player player, Component cause, @NotNull String totemID, int totemNumber, String mortality, @NotNull Location location) {
        var embedMessage = new EmbedBuilder()
                .setAuthor(player.getName() + " • #" + totemNumber + ". " + mortality)
                .setThumbnail(getTotemImage(totemID))
                .setColor(Color.YELLOW)
                .addField(":watch: Fecha:", "[ <t:" + System.currentTimeMillis() / 1000 + ":R> ]", true)
                .addField(":skull: Causa:", PlainTextComponentSerializer.plainText().serialize(cause), true)
                .addField(":compass: Coords:", "[" + location.getWorld().getName() + "] | X: " + location.getBlockX() + " | Y: " + location.getBlockY() + " | Z: " + location.getBlockZ(), true);

        var totem = DiscordChannels.TOTEM_LOG.getTextChannel();
        if (totem != null) totem.sendMessageEmbeds(embedMessage.build()).queue();

        var chat = DiscordChannels.CHAT_LOG.getTextChannel();
        if (chat != null) chat.sendMessageEmbeds(embedMessage.build()).queue();
    }

    private static @NotNull String getTotemImage(@NotNull String totemID) {
        return switch (totemID) {
            case "totem_of_undying" -> "https://cdn.discordapp.com/attachments/1367310545685970954/1394849105875832963/latest.png?ex=68784df5&is=6876fc75&hm=54366da110a422392f25059f5b67daaa5f2837ce873d0419a66e3c3ecda7dbd2&";
            case "arcane_totem" -> "https://cdn.discordapp.com/attachments/1367310545685970954/1394852638570450975/image.png?ex=68785140&is=6876ffc0&hm=83c2b02055a8eaaec7a2ebd0045797960646c3e4b22afc73535bc4780fed14b3&";
            case "calamity_totem" -> "https://media.discordapp.net/attachments/1389429067014475948/1401780593913626694/image.png?ex=689622a9&is=6894d129&hm=e4f98ce92280e4b7bec982a49a7a5987c87ecf8d78eee8d6b565b93b8357f5d1&=&width=14&height=14";
            case "paragon_totem" -> "https://cdn.discordapp.com/attachments/1366260594751311945/1410339363195453450/paragon_totem_hd.png?ex=68b0a863&is=68af56e3&hm=9f1f9fd4da046f57992aecb13128af0d82b4ac77136dc9a9324145249ecc3e4f&";
            default -> "";
        };
    }

}