package org.cataclysm.game.events.pantheon.bosses.the_cataclysm;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.Soundtrack;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TheCataclysmEvents {
    private PantheonDispatcher dispatcher;
    private Soundtrack soundtrack;
    private ScheduledFuture<?> future;
    private final TheCataclysm cataclysm;

    public TheCataclysmEvents(TheCataclysm cataclysm) {
        this.cataclysm = cataclysm;
    }

    public void handleEvents(TheCataclysmPhases phase) {
        if (phase == TheCataclysmPhases.INTRO) this.castCataclysmDialogue();
        if (phase == TheCataclysmPhases.THUNDERCLAP) this.castThunderclap();
        if (phase == TheCataclysmPhases.PANDEMONIUM) this.castCataclysmPandemonium();
        if (phase == TheCataclysmPhases.DEFEATABLE) this.castDefeatablePhase();
        dispatcher.resetDelay();
    }

    public void castDefeatablePhase() {
        this.cataclysm.getPantheon().getExecutor().schedule(() -> {
            this.soundtrack.loop("DEFEATABLE", 127);
        }, 63, TimeUnit.SECONDS);

        this.cataclysm.setChat(false); //programar desabilitar chat
        this.soundtrack.stopAll();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0));
        }
        this.soundtrack.play(this.soundtrack.getSound("POWER_OF_FRIENDSHIP"));
        dispatcher.addDelay(3000);
        dispatcher.sendActionBar("Parece que vuestra aventura está llegando a un final");
        dispatcher.sendActionBar("Las opciones parecen acabarse");
        dispatcher.sendActionBar("Los jugadores van disminuyendo");
        dispatcher.sendActionBar("Los recursos se están agotando");
        dispatcher.sendActionBar("Definitivamente, no parece haber lugar para la esperanza");
        dispatcher.sendActionBar("No parece quedar nada...");
        dispatcher.addDelay(10000);
        dispatcher.sendActionBar("Aún así...");
        dispatcher.sendActionBar("Hay algo a lo que aun os podéis aferrar");
        dispatcher.sendActionBar("A lo que todos nos podemos aferrar");
        dispatcher.sendActionBar("¿Lo oís, supervivientes?");

        dispatcher.schedule(() -> cataclysm.setChat(true));
        dispatcher.schedule(() -> dispatcher.sendMessage("¡Ahora los espectadores pueden ayudar a los supervivientes pronunciando su nombre por el chat!"));

        dispatcher.addDelay(10000);
        dispatcher.sendActionBar("Ellos siguen ahí");
        dispatcher.sendActionBar("Todos ellos");
        dispatcher.sendActionBar("No siempre tiene que acabar mal");
        dispatcher.sendActionBar("Acabar con este Cataclismo de una vez por todas");
        dispatcher.schedule(() -> dispatcher.sendMessage("La defensa de Cataclysm ha caído a 0."));
        this.dispatcher.schedule(this::startVulnerable);
    }

    public void startVulnerable() {
        Bukkit.getOnlinePlayers().forEach(LivingEntity::clearActivePotionEffects);
        this.cataclysm.setVulnerable(true);
    }

    public void castCataclysmDialogue() {
        dispatcher.addDelay(4000);
        dispatcher.sendActionBar("Ha sido un viaje largo y agotador, ¿verdad?");
        dispatcher.sendActionBar("Tengo la sensación de que han pasado bastante más de treinta y cinco días.");
        dispatcher.playSounds(Sound.sound(Key.key("entity.allay.ambient_with_item"), SoundCategory.MASTER, 0.9F, 0.75F));
        dispatcher.sendActionBar("¡Ya no lo podía soportar!", 20.0);
        dispatcher.addDelay(4000);

        dispatcher.sendActionBar("Los cuatro dioses de este mundo han muerto.");
        dispatcher.sendActionBar("Sus almas descansan en esta tumba.");
        dispatcher.sendActionBar("«dios de las bestias»");
        dispatcher.sendActionBar("«dios de la furia»");
        dispatcher.sendActionBar("«dios del vacío insondable...");
        dispatcher.sendActionBar("...sellado en un rey muerto»");
        dispatcher.sendActionBar("Y por último...");
        dispatcher.addDelay(500);

        dispatcher.sendActionBar("...la última presencia de este mísero panteón.");
        dispatcher.sendActionBar("La única capaz de comunicarse...");
        dispatcher.sendActionBar("...e incapaz de manifestarse...");
        dispatcher.addDelay(80);

        dispatcher.playSounds(soundtrack.getSound("THUNDERCLAP"));
        dispatcher.sendActionBar("...sin esa calavera.");
        dispatcher.schedule(() -> cataclysm.getPantheon().getExecutor().schedule(() -> {
            cataclysm.health = 0;
            cataclysm.setUpBossBar(true);
            cataclysm.getBossBar().name(cataclysm.getBarName(true));
            future = cataclysm.getPantheon().getExecutor().scheduleAtFixedRate(() -> {
                cataclysm.health += 5;
                cataclysm.updateBar();
                if (cataclysm.health >= cataclysm.maxHealth) {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> cataclysm.changePhase(TheCataclysmPhases.THUNDERCLAP));
                    future.cancel(true);
                    future = null;
                }
            }, 0, 43, TimeUnit.MILLISECONDS);
        }, 2000, TimeUnit.MILLISECONDS));

        dispatcher.addDelay(3000);
        dispatcher.sendActionBar("Han transcurrido treinta y cinco días...", 25);
        dispatcher.sendActionBar("desde que fui condenado a esta maldita prisión.", 25);
        dispatcher.sendActionBar("desde que me arrebataron mi cuerpo...", 25);
        dispatcher.sendActionBar("...mas no mi legado.", 25);
        dispatcher.sendActionBar("ahora...", 20);
        dispatcher.sendActionBar("...con mi forma restaurada...", 22);
        dispatcher.sendActionBar("...y la fuerza de ser recordado...", 22);
        dispatcher.sendActionBar("...solo me resta una cosa para regresar a mi mundo.", 25);
        dispatcher.addDelay(1000);
        dispatcher.sendActionBar("Decidme, mortales...", 20);
        dispatcher.sendActionBar("...ahora que percibís mi presencia...", 20);
        dispatcher.sendActionBar("...y conocéis mi nombre...", 22);
        dispatcher.sendActionBar("...pues habéis profanado mi tumba...", 20);
        dispatcher.sendActionBar("¿estáis listos?");
    }

    public void castThunderclap() {
        dispatcher.schedule(()
                -> this.soundtrack.loop(this.soundtrack.getSound("PANDEMONIUM"), 290), 205000);

        cataclysm.getBossBar().name(cataclysm.getBarName(false));
        cataclysm.health = cataclysm.maxHealth;
        cataclysm.updateBar();
        dispatcher.playSounds(
                Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.MASTER, 1F, 0.65F),
                Sound.sound(Key.key("entity.elder_guardian.curse"), Sound.Source.MASTER, 1F, 0.8F),
                Sound.sound(Key.key("entity.elder_guardian.curse"), Sound.Source.MASTER, 1F, 0.5F),
                Sound.sound(Key.key("entity.skeleton_horse.death"), Sound.Source.MASTER, 3F, 0.5F)
        );
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize("<gradient:#8C3434:#752B2B:#8C3434>The Cataclysm</gradient>"),
                    MiniMessage.miniMessage().deserialize("<#F0F0F0>Mighty God of Destruction"),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofSeconds(2))
            ));
        }
    }

    public void castCataclysmPandemonium() {
        soundtrack.play(soundtrack.getSound("PANDEMONIUM"));
    }

    public void handleSetup() {
        if (this.dispatcher == null) this.dispatcher = cataclysm.getPantheon().getDispatcher();
        if (this.soundtrack == null) this.soundtrack = cataclysm.getSoundtrack();
    }
}