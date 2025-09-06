package org.cataclysm.game.events.pantheon.bosses.the_cataclysm.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.Soundtrack;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;
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
        dispatcher.resetDelay();
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
