package org.cataclysm.game.player.survival.resurrect.totems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.jetbrains.annotations.NotNull;

public class TotemUtils {

    public static @NotNull Component formatDamageCause(@NotNull EntityDamageEvent event) {
        switch (event.getCause()) {
            case FALL -> {
                return MiniMessage.miniMessage().deserialize( "Caída");
            }
            case FIRE -> {
                return MiniMessage.miniMessage().deserialize("Fuego");
            }
            case LAVA -> {
                return MiniMessage.miniMessage().deserialize("Lava");
            }
            case VOID -> {
                return MiniMessage.miniMessage().deserialize("Vacío");
            }
            case MAGIC -> {
                return MiniMessage.miniMessage().deserialize("Magia");
            }
            case FREEZE -> {
                return MiniMessage.miniMessage().deserialize("Congelación");
            }
            case POISON -> {
                return MiniMessage.miniMessage().deserialize("Veneno");
            }
            case THORNS -> {
                return MiniMessage.miniMessage().deserialize("Espinas");
            }
            case WITHER -> {
                return MiniMessage.miniMessage().deserialize("Descomposición");
            }
            case CONTACT -> {
                return MiniMessage.miniMessage().deserialize("Contacto");
            }
            case MELTING -> {
                return MiniMessage.miniMessage().deserialize("Derretimiento");
            }
            case SUICIDE -> {
                return MiniMessage.miniMessage().deserialize("Suicidio");
            }
            case CRAMMING, SUFFOCATION -> {
                return MiniMessage.miniMessage().deserialize("Sofocación");
            }
            case FIRE_TICK -> {
                return MiniMessage.miniMessage().deserialize("Ticks de fuego");
            }
            case HOT_FLOOR -> {
                return MiniMessage.miniMessage().deserialize("Bloque de magma");
            }
            case LIGHTNING -> {
                return MiniMessage.miniMessage().deserialize("Rayo");
            }
            case STARVATION -> {
                return MiniMessage.miniMessage().deserialize("Hambre");
            }
            case SONIC_BOOM -> {
                return MiniMessage.miniMessage().deserialize("Onda sónica");
            }
            case FALLING_BLOCK -> {
                return MiniMessage.miniMessage().deserialize("Idiota");
            }
            case FLY_INTO_WALL -> {
                return MiniMessage.miniMessage().deserialize("Energía cinética");
            }
            case ENTITY_SWEEP_ATTACK -> {
                return MiniMessage.miniMessage().deserialize("Barrido de ataque");
            }
            case DRYOUT, DROWNING -> {
                return MiniMessage.miniMessage().deserialize("Ahogamiento");
            }
            case ENTITY_ATTACK -> {
                if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                    var name = entityDamageByEntityEvent.getDamager().name();
                    if (entityDamageByEntityEvent.getDamager() instanceof Player player && CataclysmBoss.isController(player)) {
                        if (Cataclysm.getBoss() != null) {
                            var stringName = Cataclysm.getBoss().getName();
                            if (stringName.contains("ch")) name = MiniMessage.miniMessage().deserialize("Calamity Hydra");
                            else name = MiniMessage.miniMessage().deserialize(stringName);
                        }
                    }
                    return MiniMessage.miniMessage().deserialize("Ataque de ").append(name);
                }
                else return MiniMessage.miniMessage().deserialize("Ataque de entidad");
            }
            case PROJECTILE -> {
                if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof Projectile projectile) {
                    if (projectile.getShooter() instanceof LivingEntity shooter) return MiniMessage.miniMessage().deserialize("Proyectil de ").append(shooter.name());
                }
                return MiniMessage.miniMessage().deserialize("Proyectil");
            }
            case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> {
                if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) return MiniMessage.miniMessage().deserialize("Explosión de ").append(entityDamageByEntityEvent.getDamager().name());
                else return MiniMessage.miniMessage().deserialize("Explosión");
            }
            default -> {
                return MiniMessage.miniMessage().deserialize("Desconocido");
            }
        }
    }
}
