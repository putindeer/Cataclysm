package org.cataclysm.api.mob;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MobUtils {
    private static final Reflections reflections = new Reflections("org.cataclysm");

    public static boolean hasNearbyPlayer(LivingEntity entity, double xRadius, double yRadius, double zRadius) {
        for (var nearby : entity.getNearbyEntities(xRadius, yRadius, zRadius)) {
            if (nearby instanceof Player) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the simple names of all classes that extend CataclysmMob within the org.cataclysm package.
     *
     * @return A list of all CataclysmMob subclass names.
     */
    public static @NotNull List<String> getMobNames() {
        Set<Class<? extends CataclysmMob>> mobClasses = reflections.getSubTypesOf(CataclysmMob.class);

        List<String> mobNames = new ArrayList<>();

        for (Class<? extends CataclysmMob> clazz : mobClasses) {
            mobNames.add(clazz.getSimpleName());
        }

        return mobNames;
    }
}
