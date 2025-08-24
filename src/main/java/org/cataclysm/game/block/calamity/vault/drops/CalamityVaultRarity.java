package org.cataclysm.game.block.calamity.vault.drops;

import org.cataclysm.Cataclysm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum CalamityVaultRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY

    ;

    public static List<CalamityVaultRarity> getRandomizedRarities(int size) {
        var list = new ArrayList<CalamityVaultRarity>();
        var ragnarok = Cataclysm.getRagnarok();

        for (int i = 0; i < size; i++) {
            var random = ThreadLocalRandom.current().nextInt(0, 101);
            var rarity = COMMON;

            if (ragnarok != null && ragnarok.getData().getLevel() >= 5) {
                if (random > 50) rarity = RARE;
                if (random > 70) rarity = EPIC;
                if (random > 90) rarity = LEGENDARY;
            } else {
                if (random > 70) rarity = RARE; //30%
                if (random > 92) rarity = EPIC; //8%
                if (random > 98) rarity = LEGENDARY; //2%
            }

            list.add(rarity);
        }
        return list;
    }
}
