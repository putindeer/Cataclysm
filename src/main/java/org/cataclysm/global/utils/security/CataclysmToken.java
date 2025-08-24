package org.cataclysm.global.utils.security;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;

public record CataclysmToken(String key) implements Serializable {
    public static @NotNull CataclysmToken generate() {
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = uuid.toString().getBytes();
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);
        return new CataclysmToken(token);
    }
}
