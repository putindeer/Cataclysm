package org.cataclysm.global.utils.math;

import org.jetbrains.annotations.NotNull;

public class MathUtils {

    public static double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }

    public static @NotNull String formatSeconds(int time) {
        int hours = time/3600;
        int minutes = (time/60) % 60;
        int seconds = time % 60;

        boolean displayHours = hours != 0;
        boolean displayMinutes = minutes != 0;

        StringBuilder builder = new StringBuilder();

        if (displayHours) builder.append(hours).append("ʜ ");
        if (displayMinutes) builder.append(minutes).append("ᴍ ");
        builder.append(seconds).append("s");

        return builder.toString();
    }

}