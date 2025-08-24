package org.cataclysm.api.boss.ability;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AbilityManager {
    private final List<Ability> abilities = new ArrayList<>();

    public void addAbility(Ability ability) {
        this.abilities.add(ability);
    }
}