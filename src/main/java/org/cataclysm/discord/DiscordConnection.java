package org.cataclysm.discord;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.cataclysm.Cataclysm;

@Getter
public class DiscordConnection {
    private JDA jda;

    public DiscordConnection() {
        var logger = Cataclysm.getInstance().getLogger();

        String token = Cataclysm.getInstance().getConfig().getString("discord_token");
        logger.info("Iniciando conexión con Discord...");

        if (token == null || token.isBlank()) {
            logger.warning("discord_token no está configurado o está vacío. El bot de Discord no se iniciará.");
            return;
        }

        try {
            this.jda = JDABuilder.createDefault(token,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.playing("Cataclysm SMP"))
                    .build();
            this.jda.awaitReady();
            logger.info("Bot de Discord conectado correctamente. Ping: " + jda.getGatewayPing() + "ms");
        } catch (Exception e) {
            logger.severe("Error inesperado al conectar con Discord: " + e.getMessage());
            e.printStackTrace();
        }
    }
}