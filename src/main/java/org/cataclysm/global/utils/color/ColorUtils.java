package org.cataclysm.global.utils.color;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public class ColorUtils {

    /**
     * Calcula un color interpolado entre dos colores HEX y lo devuelve en formato HEX.
     *
     * @param hex1 Color inicial en formato #RRGGBB
     * @param hex2 Color final en formato #RRGGBB
     * @param t    Valor entre 0.0 y 1.0 que indica el punto en el gradiente
     * @return Cadena HEX en formato #RRGGBB
     */
    public static String interpolateColorHex(String hex1, String hex2, double t) {
        t = Math.max(0.0, Math.min(1.0, t));

        hex1 = hex1.replace("#", "");
        hex2 = hex2.replace("#", "");

        int r1 = Integer.parseInt(hex1.substring(0, 2), 16);
        int g1 = Integer.parseInt(hex1.substring(2, 4), 16);
        int b1 = Integer.parseInt(hex1.substring(4, 6), 16);

        int r2 = Integer.parseInt(hex2.substring(0, 2), 16);
        int g2 = Integer.parseInt(hex2.substring(2, 4), 16);
        int b2 = Integer.parseInt(hex2.substring(4, 6), 16);

        int r = (int) Math.round(r1 + (r2 - r1) * t);
        int g = (int) Math.round(g1 + (g2 - g1) * t);
        int b = (int) Math.round(b1 + (b2 - b1) * t);

        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static @NotNull Color hexToColor(@NotNull String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.length() != 6) {
            throw new IllegalArgumentException("Invalid HEX color: " + hex);
        }

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return Color.fromRGB(r, g, b);
    }
}
