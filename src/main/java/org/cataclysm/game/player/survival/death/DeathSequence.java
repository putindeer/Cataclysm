package org.cataclysm.game.player.survival.death;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.game.world.ragnarok.Ragnarok;
import org.cataclysm.game.world.ragnarok.RagnarokManager;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.cataclysm.global.utils.text.TextUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DeathSequence {
    private final PlayerDeathEvent event;

    private final Audience audience;

    private final ScheduledExecutorService service = Cataclysm.getScheduledExecutorService();

    private final List<ScheduledFuture<?>> futures = new ArrayList<>();

    public DeathSequence(PlayerDeathEvent event, Audience audience) {
        this.event = event;
        this.audience = audience;
    }

    public void paleVoid() {
        Player player = this.event.getPlayer();
        PersistentData.set(player, "PALE-VOID", PersistentDataType.BOOLEAN, true);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0)), 20);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            Location location = Dimensions.PALE_VOID.getWorld().getSpawnLocation().clone().add(0, 30, 0);
            player.teleport(location);
            player.setGameMode(GameMode.SURVIVAL);
            player.playSound(player, org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_DEATH, 5F, 0.55F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 300, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 300, 0, false, false));
        }, 40);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            player.ban("No pierdas la esperanza. Esta es tu última oportunidad. Bienvenido al Pale Void", (Date) null ,"", true);
            player.kick();
        }, 100);
    }

    public void cast(DeathTitleType type) {
        Player player = this.event.getPlayer();
        Location location = player.getLocation();

        Component deathMessage = this.event.deathMessage();
        if (deathMessage == null) return;
        var plainDeathMessage = PlainTextComponentSerializer.plainText().serialize(deathMessage);
        var bossFight = Cataclysm.getBossFight();
        if (bossFight != null && plainDeathMessage.contains(bossFight.getController().getName())) {
            var bossName = bossFight.getName();
            plainDeathMessage = plainDeathMessage.replace(bossFight.getController().getName(), bossName);
        }

        String finalPlainDeathMessage = plainDeathMessage;
        this.audience.forEachAudience(each -> {
            ChatMessenger.sendMessage((Player) each, "<b>Aeterna passio " + ChatMessenger.getCataclysmColor() + player.getName() + ChatMessenger.getTextColor() + " incepit...");
            each.sendActionBar(MiniMessage.miniMessage().deserialize(" <gradient:#d93939:#7e2020>" + player.getName() + " ha muerto</gradient>"));
            each.sendMessage(MiniMessage.miniMessage().deserialize("<#828282>[" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "; " + location.getWorld().getName() + "]"));
            each.sendMessage(CataclysmPlayer.getCataclysmPlayer(player).getDeathMessageManager().getFormattedChatMessage());
            each.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#d93939:#7e2020>" + finalPlainDeathMessage + "</gradient>"));
            each.stopSound(SoundStop.named(Key.key("cataclysm.ragnarok")));
        });

        switch (type) {
            case ANIMATION -> this.castAnimation();
            case SIMPLE -> {
                this.playAnimation();
                this.playAnimationSounds();
            }
            default -> throw new IllegalArgumentException("Unknown DeathTitleType: " + type);
        }

        this.futures.add(this.service.schedule(() -> {
            Cataclysm.setDeathSequence(null);
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> new RagnarokManager(new Ragnarok()).start());
        }, 7250, TimeUnit.MILLISECONDS));
    }

    private void castAnimation() {
        this.playAnimation();
        this.playAnimationSounds();

        this.futures.add(this.service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    onlinePlayers.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 155, 1, true, false));
                }
            });
        }, 500, TimeUnit.MILLISECONDS));
    }

    private ScheduledFuture<?> animationFuture;

    public void stop() {
        this.futures.forEach(e -> e.cancel(true));

        if (this.animationFuture != null) this.animationFuture.cancel(true);

        this.stopSounds();
    }

    private void playAnimation() {
        List<String> frames = new ArrayList<>();

        for (int i = 1; i < 61; i++) {
            String format = "000";
            if (i < 10) format = "00" + i;
            if (i >= 10) format = "0" + i;
            frames.add("\\" + "uE" + format);
        }

        int[] tick = {0};

        this.animationFuture = this.service.scheduleWithFixedDelay(() -> {
            String title = TextUtils.convertUnicode(frames.get(tick[0]));

            tick[0]++;

            this.audience.forEachAudience(audience1 -> {
                if (!(audience1 instanceof Player player)) return;
                Boolean paleVoid = PersistentData.get(player, "PALE-VOID", PersistentDataType.BOOLEAN);
                if (Boolean.TRUE.equals(paleVoid)) return;

                player.showTitle(Title.title(
                        MiniMessage.miniMessage().deserialize("<!shadow>" + title),
                        Component.text(""),
                        Title.Times.times(
                                Duration.ofMillis(0),
                                Duration.ofMillis(500),
                                Duration.ofMillis(2000)
                        )
                ));
            });

            if (tick[0] > 64) this.animationFuture.cancel(true);
        }, 0, 55, TimeUnit.MILLISECONDS);
    }

    private final Sound.Source source = Sound.Source.AMBIENT;

    private void stopSounds() {
        this.audience.forEachAudience(audience1 -> {
            if (!(audience1 instanceof Player player)) return;
            Boolean paleVoid = PersistentData.get(player, "PALE-VOID", PersistentDataType.BOOLEAN);
            if (Boolean.TRUE.equals(paleVoid)) return;

            player.stopSound(SoundStop.namedOnSource(Key.key("entity.elder_guardian.ambient"), this.source));
            player.stopSound(SoundStop.namedOnSource(Key.key("entity.lightning_bolt.thunder"), this.source));
            player.stopSound(SoundStop.namedOnSource(Key.key("entity.skeleton_horse.death"), this.source));
            player.stopSound(SoundStop.namedOnSource(Key.key("entity.elder_guardian.curse"), this.source));
            player.stopSound(SoundStop.namedOnSource(Key.key("entity.elder_guardian.death"), this.source));
            player.stopSound(SoundStop.namedOnSource(Key.key("entity.guardian.hurt"), this.source));
            player.stopSound(SoundStop.namedOnSource(Key.key("item.trident.thunder"), this.source));
        });
    }

    private void playAnimationSounds() {
        this.audience.forEachAudience(audience1 -> {
            if (!(audience1 instanceof Player player)) return;
            Boolean paleVoid = PersistentData.get(player, "PALE-VOID", PersistentDataType.BOOLEAN);
            if (Boolean.TRUE.equals(paleVoid)) return;

            player.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), this.source, 1.0F, 0.95F));
            player.playSound(Sound.sound(Key.key("entity.elder_guardian.death"), this.source, 1.0F, 1.35F));

            this.futures.add(this.service.schedule(() -> {
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), this.source, 1.0F, 1.15F));
                player.playSound(Sound.sound(Key.key("entity.lightning_bolt.thunder"), this.source, 1.0F, 1.13F));
            }, 500, TimeUnit.MILLISECONDS));

            this.futures.add(this.service.schedule(() -> {
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), this.source, 1.0F, 1.15F));
                player.playSound(Sound.sound(Key.key("entity.lightning_bolt.thunder"), this.source, 1.0F, 1.13F));
            }, 1750, TimeUnit.MILLISECONDS));

            this.futures.add(this.service.schedule(() -> {
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), this.source, 1.0F, 1.0F));
                player.playSound(Sound.sound(Key.key("entity.guardian.hurt"), this.source, 0.8F, 0.5F));
            }, 2250, TimeUnit.MILLISECONDS));

            this.futures.add(this.service.schedule(() -> {
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), this.source, 1.0F, 1.35F));
                player.playSound(Sound.sound(Key.key("entity.guardian.hurt"), this.source, 0.8F, 0.75F));
                player.playSound(Sound.sound(Key.key("entity.skeleton_horse.death"), this.source, 1.0F, 0.95F));
            }, 2750, TimeUnit.MILLISECONDS));

            this.futures.add(this.service.schedule(() -> {
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), this.source, 1.0F, 1.5F));
                player.playSound(Sound.sound(Key.key("entity.guardian.hurt"), this.source, 0.8F, 0.8F));
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.death"), this.source, 1.0F, 0.85F));
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.ambient"), this.source, 1.0F, 0.55F));
            }, 3250, TimeUnit.MILLISECONDS));
        });
    }
}