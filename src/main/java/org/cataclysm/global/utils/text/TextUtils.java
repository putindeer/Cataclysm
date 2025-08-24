package org.cataclysm.global.utils.text;

import org.cataclysm.api.color.CataclysmColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static List<String> formatAffixes(String... colors) {
        if (colors == null || colors.length == 0) colors = new String[]{CataclysmColor.PALE.getColor()};

        List<String> affix = new ArrayList<>();
        if (colors.length == 1) affix.add("<" + colors[0] + ">");
        else {
            StringBuilder gradient = new StringBuilder("<gradient");
            for (String color : colors) gradient.append(":").append(color);
            gradient.append(">");
            affix.add(gradient.toString());
            affix.add("</gradient>");
        }

        return affix;
    }

    public static String buildGlitchedNotification(@NotNull String notification) {
        var glitchedNotification = new StringBuilder();
        boolean inTag = false;

        for (int i = 0; i < notification.length(); i++) {
            char c = notification.charAt(i);

            // Detect start of MiniMessage tag
            if (c == '<') {
                inTag = true;
            }

            if (inTag) {
                glitchedNotification.append(c);
                if (c == '>') {
                    inTag = false;
                }
                continue; // Skip obfuscation inside tags
            }

            if (Character.isWhitespace(c)) {
                glitchedNotification.append(c);
                continue;
            }

            // Random obfuscate chance (50% here)
            if (ThreadLocalRandom.current().nextDouble() < 0.5) {
                glitchedNotification.append("<obf>").append(c).append("</obf>");
            } else {
                glitchedNotification.append(c);
            }
        }

        return glitchedNotification.toString();
    }

    public static @NotNull String convertUnicode(@NotNull String unicodeInput) {
        if (unicodeInput.startsWith("\\u")) unicodeInput = unicodeInput.substring(2);

        int unicodeValue = Integer.parseInt(unicodeInput, 16);

        char[] charArray = Character.toChars(unicodeValue);

        return new String(charArray);
    }

    /**
     * Convierte un número entero positivo a su representación en números romanos.
     *
     * La función soporta valores del 1 al 3999, que es el rango estándar para la notación
     * romana clásica.
     *
     * Ejemplo:
     * <pre>
     *   2025 -> "MMXXV"
     * </pre>
     *
     * @param number el entero positivo a convertir; debe estar en el rango [1, 3999].
     * @return una cadena con la representación en números romanos.
     * @throws IllegalArgumentException si el número está fuera del rango permitido.
     */
    public static @NotNull String toRoman(int number) {
        if (number == 0) return "";

        int[] values =    {1000, 900, 500, 400, 100, 90,  50,  40,  10,  9,   5,   4,   1};
        String[] romans = {"M",  "CM","D", "CD","C", "XC","L", "XL","X", "IX","V", "IV","I"};

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                result.append(romans[i]);
            }
        }

        return result.toString();
    }

    /**
     * Convierte una cadena en formato CONSTANT_CASE (mayúsculas con guiones bajos)
     * a un formato legible con palabras capitalizadas separadas por espacios.
     *
     * Ejemplo:
     * <pre>
     *   "PARAGON_KEY" -> "Paragon Key"
     * </pre>
     *
     * Esta función es útil para transformar identificadores técnicos en etiquetas
     * amigables para interfaces de usuario, reportes o presentaciones.
     *
     * @param input la cadena en formato CONSTANT_CASE; puede ser null o vacía.
     * @return una cadena con palabras capitalizadas y espacios, o el valor original
     *         si es null o vacío.
     */
    public static String formatKey(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].toLowerCase();
            // Capitaliza la primera letra
            String capitalized = part.substring(0, 1).toUpperCase() + part.substring(1);
            result.append(capitalized);

            if (i < parts.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    public static String wrapHexCodes(String text) {
        Pattern pattern = Pattern.compile("<gradient:[^>]+>[^<]+</gradient>|#[0-9a-fA-F]{6}");
        Matcher matcher = pattern.matcher(text);

        HashSet<Object> wrappedHexCodes = new HashSet<>();
        while (matcher.find()) {
            String hexCode = matcher.group();

            if (hexCode.startsWith("<gradient:")) continue;
            if (wrappedHexCodes.contains(hexCode)) continue;

            String wrappedHex = "<" + hexCode + ">";
            text = text.replace(hexCode, wrappedHex);

            wrappedHexCodes.add(hexCode);
        }

        return text;
    }

}
