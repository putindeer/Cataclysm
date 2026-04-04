package org.cataclysm.discord;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.cataclysm.Cataclysm;

import javax.security.auth.login.LoginException;

@Getter
public class DiscordConnection {
    private JDA jda;

    public DiscordConnection() {
        String token = Cataclysm.getInstance().getConfig().getString("discord_token");
        try {
            this.jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing("Cataclysm SMP"))
                    .build();
        } catch (LoginException ignored) {
        }
    }
}
