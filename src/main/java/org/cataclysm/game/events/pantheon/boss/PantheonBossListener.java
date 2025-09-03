package org.cataclysm.game.events.pantheon.boss;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.boss.events.BossCastAbilityEvent;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.boss.events.BossFightEndEvent;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.global.utils.text.TextUtils;

import java.time.Duration;
import java.util.List;

@Registrable
public class PantheonBossListener implements Listener {
    private static final List<EntityDamageEvent.DamageCause> immunities = List.of(
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
            EntityDamageEvent.DamageCause.LIGHTNING
    );

    @EventHandler
    public void onBossFightEnd(BossFightEndEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getBoss() instanceof PantheonBoss boss)) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize("<#a18d60>¡Sección Completada!"),
                    MiniMessage.miniMessage().deserialize("<#b0a897>" + boss.getName() + " liberado"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2))
            ));
            player.playSound(player, Sound.ITEM_TRIDENT_THUNDER, 1, .75F);
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, .9F);
        }

        boss.setUpBossBar(false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getEntity() instanceof Player controller)) return;

        PantheonBoss boss = pantheon.getBoss();
        if (boss == null || !CataclysmBoss.isController(controller)) return;

        Entity damager = event.getDamager();

        //MAKE A INVULNERABILITY TOGGLE TO ALL MOBS
        if (damager instanceof AreaEffectCloud || boss.isInvulnerable()) {
            event.setCancelled(true);
            return;
        }

        if (damager instanceof Player player) {
            ItemStack mace = player.getInventory().getItemInMainHand();
            if (mace.getType() == Material.MACE) {
                player.setCooldown(mace, 150);
                player.playSound(player, Sound.ITEM_SHIELD_BREAK, 2F, .95F);
                player.playSound(player, Sound.ITEM_SHIELD_BREAK, 2F, 1.15F);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getEntity() instanceof Player player)) return;

        PantheonBoss boss = pantheon.getBoss();
        if (boss == null || !CataclysmBoss.isController(player)) return;

        var cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.CUSTOM) event.setDamage(0);

        if (!immunities.contains(cause)) {
            boss.health -= (int) event.getDamage();
            boss.updateBar();
            event.setDamage(0);
            //if (bossFight.health <= 0) bossFight.stopFight();
        }
        else event.setCancelled(true);

        player.setFireTicks(0);
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getEntity() instanceof Player player)) return;

        PantheonBoss boss = pantheon.getBoss();
        if (boss == null || !CataclysmBoss.isController(player)) return;

        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    private void onBossCastAbility(BossCastAbilityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getBoss() instanceof PantheonBoss boss)) return;

        PantheonAbility ability = (PantheonAbility) event.getAbility().clone();
        if (ability.isBoosted()) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                ability.setBoosted(false);
                boss.setBoosted(false);
            }, 20);
        }
    }

    @EventHandler
    private void onBossChannelAbility(BossChannelAbilityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getBoss() instanceof PantheonBoss boss)) return;

        if (!boss.getAbilityVisibility()) return;

        PantheonAbility ability = (PantheonAbility) event.getAbility().clone();

        String display = ability.getHoverName();
        if (ability.isBoosted()) display = TextUtils.buildGlitchedNotification(display);

        for (Player player : Bukkit.getOnlinePlayers()) {
            pantheon.getDispatcher().sendMessage("El jefe usará la habilidad " + display);
            player.playSound(player, Sound.BLOCK_END_PORTAL_FRAME_FILL, 3.0F, 0.65F);
            player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 2.0F, 0.65F);
            if (ability.getTitle() != null) player.showTitle(ability.getTitle());
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        Action action = event.getAction();
        ItemStack itemStack = event.getItem();

        if (action.isLeftClick() || itemStack == null) return;

        String id = new ItemBuilder(itemStack).getID();
        if (id == null) return;

        Player player = event.getPlayer();
        boolean isController = CataclysmBoss.isController(player);
        if (!isController || player.hasCooldown(itemStack)) return;

        PantheonBoss boss = pantheon.getBoss();
        if (boss == null) return;

        var abilities = boss.getAbilityManager().getAbilities();
        abilities.forEach(ability -> {
            String triggerID = new ItemBuilder(ability.getTrigger()).getID();
            if (triggerID == null || !triggerID.equals(id)) return;
            boss.castAbility(ability);
            event.setCancelled(true);
        });
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        Player player = event.getPlayer();
        boolean isController = CataclysmBoss.isController(player);

        PantheonBoss boss = pantheon.getBoss();
        if (boss == null) {
            if (isController) CataclysmBoss.setControllerData(player, false);
            return;
        }

        if (isController) CataclysmBoss.setControllerData(player, true);

        boss.getBossBar().addViewer(player);
        boss.getHealthBar().addViewer(player);
    }

    @EventHandler
    private void onEntityTargetBoss(EntityTargetEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        PantheonBoss boss = pantheon.getBoss();
        if (boss == null) return;

        if (event.getTarget() != null && event.getTarget() == boss.getController()) {
            event.setCancelled(true);
        }
    }
}
