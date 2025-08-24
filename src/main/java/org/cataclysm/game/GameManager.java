package org.cataclysm.game;

import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.data.GameData;
import org.jetbrains.annotations.NotNull;

public record GameManager(GameData data) {

    public void save(@NotNull JsonConfig jsonConfig) {
        jsonConfig.setJsonObject(Cataclysm.getGson().toJsonTree(this.data).getAsJsonObject());
        try {
            jsonConfig.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void restore(@NotNull JsonConfig jsonConfig) {
        GameManager gameManager;

        if (!jsonConfig.getJsonObject().entrySet().isEmpty()) {
            gameManager = new GameManager(Cataclysm.getGson().fromJson(jsonConfig.getJsonObject(), GameData.class));
        } else {
            gameManager = new GameManager(new GameData());
        }

        Cataclysm.setGameManager(gameManager);
    }

}
