package org.cataclysm.api.boss;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.api.boss.ability.AbilityBooster;
import org.cataclysm.api.boss.ability.AbilityManager;
import org.cataclysm.api.boss.events.BossCastAbilityEvent;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.boss.events.BossFightStopEvent;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.sound.Soundtrack;
import org.cataclysm.game.raids.bosses.twisted_warden.keys.TwistedWardenKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public abstract class CataclysmBoss implements Cloneable {
    protected final @Getter BossThread thread = new BossThread(this);
    protected final @Getter AbilityManager abilityManager = new AbilityManager();
    protected final @Getter Soundtrack soundtrack = new Soundtrack();

    protected @Getter BossBar bossBar;
    protected @Getter BossBar healthBar;
    protected @Getter Player controller;
    protected @Getter CataclysmArea arena;

    protected final @Getter String name;
    public final int maxHealth;
    public int health;

    protected @Nullable Listener listener;

    public CataclysmBoss(String name, int health) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.bossBar = this.buildBossBar();
        this.healthBar = BossBar.bossBar(this.getHealthBarName(), 1.0F, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        this.registerTracks();
        this.registerAbilities();
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void registerTracks();

    public abstract void registerAbilities();

    public abstract void tick();

    public abstract BossBar buildBossBar();

    public void startFight() {
        this.setUpController(true);

        if (this.listener != null) Bukkit.getPluginManager().registerEvents(this.listener, Cataclysm.getInstance());
        this.thread.startTickTask();

        var arena = this.getArena();
        for (var player : arena.getPlayersInArena()) {
            this.bossBar.addViewer(player);
            this.healthBar.addViewer(player);
        }

        Cataclysm.setBossFight(this);

        this.onStart();
    }

    public void stopFight() {
        this.thread.service.shutdownNow();
        this.setUpController(false);

        var nearby = this.arena.center().getNearbyPlayers(this.arena.radius());
        for (var player : nearby) {
            var title = Title.title(
                    MiniMessage.miniMessage().deserialize("<#a18d60>¡Incursión Finalizada!"),
                    MiniMessage.miniMessage().deserialize("<#b0a897>" + this.name + " derrotado"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2))
            );
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 0.9F);
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1.50F);
            player.showTitle(title);
            this.bossBar.removeViewer(player);
            this.healthBar.removeViewer(player);
        }

        for (var player : arena.getPlayersInArena()) {
            this.bossBar.removeViewer(player);
            this.healthBar.removeViewer(player);
        }

        Cataclysm.setBossFight(null);

        new BossFightStopEvent().callEvent();

        this.soundtrack.stopAll();

        this.onStop();
    }

    public void castAbility(@NotNull Ability ability) {
        if (this.controller.hasCooldown(ability.getTrigger())) return;
        this.controller.setCooldown(ability.getTrigger(), (int) (ability.getCooldown() * 20));

        ability.setBoosted(this.isBoosted());

        new BossChannelAbilityEvent(ability, this.getFighters(), this).callEvent();
        ability.channel();

        double channelTime = ability.getChannelTime();

        TimeUnit timeUnit;
        if (channelTime % 1 == 0) timeUnit = TimeUnit.SECONDS;
        else {
            timeUnit = TimeUnit.MILLISECONDS;
            channelTime *= 1000;
        }

        this.thread.service.schedule(() -> Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            new BossCastAbilityEvent(ability, this.getFighters(), this).callEvent();
            ability.cast();
        }), (int) channelTime, timeUnit);
    }

    public void setController(@NotNull Player controller) {
        this.controller = controller;
        this.setUpInventory();
        setControllerData(controller, true);
    }

    public void setUpController(boolean fighting) {
        if (this.controller == null) return;

        double value;
        if (fighting) {
            this.controller.setGameMode(GameMode.ADVENTURE);
            this.controller.teleport(this.arena.center());
            this.setAbilityVisibility(true);
            this.setUpInventory();
            value = 1.2;
        } else {
            this.controller.setGameMode(GameMode.SPECTATOR);
            this.controller.getInventory().clear();
            value = 1;
        }

        var stepHeight = this.controller.getAttribute(Attribute.STEP_HEIGHT);
        if (stepHeight != null) stepHeight.setBaseValue(value);

        setControllerData(this.controller, fighting);
    }

    private void setUpInventory() {
        if (this.controller == null) return;

        this.controller.getInventory().clear();

        for (var ability : this.abilityManager.getAbilities()) {
            this.controller.getInventory().addItem(ability.getTrigger());
        }
    }

    public Collection<LivingEntity> getNearbyLivingEntities(@NotNull Location location, double radius) {
        var livingEntities = location.getNearbyLivingEntities(radius, radius, radius);
        livingEntities.removeIf(livingEntity -> this.controller.equals(livingEntity));
        return livingEntities;
    }

    public Collection<Player> getNearbyFighters(@NotNull Location location, double radius) {
        var fighters = location.getNearbyPlayers(radius, radius, radius);
        fighters.removeIf(fighter -> fighter.getGameMode() == GameMode.SPECTATOR || this.controller.equals(fighter));
        return fighters;
    }

    public Collection<Player> getFighters() {
        var arena = this.arena;
        return this.getNearbyFighters(arena.center(), arena.radius());
    }

    public void setBoosted(boolean boosted) {
        PersistentData.set(this.controller, TwistedWardenKeys.BOOSTED_KEY.getKey(), PersistentDataType.BOOLEAN, boosted);
    }

    public boolean isBoosted() {
        return Boolean.TRUE.equals(PersistentData.get(this.controller, TwistedWardenKeys.BOOSTED_KEY.getKey(), PersistentDataType.BOOLEAN));
    }

    public void setAbilityVisibility(boolean show) {
        PersistentData.set(this.controller, "SAM", PersistentDataType.BOOLEAN, show);
    }

    public boolean getAbilityVisibility() {
        return Boolean.TRUE.equals(PersistentData.get(this.controller, "SAM", PersistentDataType.BOOLEAN));
    }

    public void resetAbilityCooldown() {
        this.controller.resetCooldown();
        this.abilityManager.getAbilities().forEach(ability -> {
            if (ability instanceof AbilityBooster) return;
            this.controller.setCooldown(ability.getTrigger(), 0);
        });
    }

    public void updateBar() {
        float progress = ((float) this.health/this.maxHealth);
        this.bossBar.progress(progress);
        this.healthBar.name(this.getHealthBarName());
    }

    public static void setControllerData(@NotNull Player player, boolean controlling) {
        PersistentData.set(player, "CONTROLLER", PersistentDataType.BOOLEAN, controlling);
    }

    public static boolean isController(@NotNull Player player) {
        return Boolean.TRUE.equals(PersistentData.get(player, "CONTROLLER", PersistentDataType.BOOLEAN));
    }

    public void resetAttribute(Attribute attribute) {
        AttributeInstance instance = this.controller.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(instance.getDefaultValue());
    }

    public void setAttribute(Attribute attribute, double baseValue) {
        AttributeInstance instance = this.controller.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(baseValue);
    }

    public double getAttribute(Attribute attribute) {
        AttributeInstance instance = this.controller.getAttribute(attribute);
        if (instance != null) return instance.getValue();
        return 0.0;
    }

    private Component getHealthBarName() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return MiniMessage.miniMessage().deserialize("« " + formatter.format(this.health) + " »");
    }

    @Override
    public CataclysmBoss clone() {
        try {
            return (CataclysmBoss) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}