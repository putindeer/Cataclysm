package org.cataclysm.api.listener.registrable;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.cataclysm.Cataclysm;
import org.reflections.Reflections;

public class RegistrableUtils {

    public static void registerListeners() {
        Reflections reflections = new Reflections("org.cataclysm");

        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Registrable.class)) {
            try {
                if (!(clazz.getDeclaredConstructor().newInstance() instanceof Listener listener)) continue;
                Bukkit.getServer().getPluginManager().registerEvents(listener, Cataclysm.getInstance());
            } catch (Exception exception) {
                exception.fillInStackTrace();
            }
        }
    }

}