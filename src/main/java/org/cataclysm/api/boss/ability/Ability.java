package org.cataclysm.api.boss.ability;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.global.utils.text.TextUtils;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.util.List;

@Getter
public abstract class Ability implements Cloneable {
    protected @Getter @Setter boolean boosted = false;

    protected @Setter int channelTime;
    protected final boolean broadcast;
    protected @Setter double cooldown;
    protected final ItemStack trigger;
    protected final String name;
    protected final String display;

    public Ability(Material triggerMaterial, String name, int channelTime, double cooldown) {
        this(triggerMaterial, name, channelTime, cooldown, true, "#f0f0f0");
    }

    public Ability(Material triggerMaterial, String name, int channelTime, double cooldown, boolean broadcast, String... colors) {
        this.name = name;
        this.cooldown = cooldown;
        this.channelTime = channelTime;
        this.broadcast = broadcast;

        List<String> affixes = TextUtils.formatAffixes(colors);
        if (affixes.size() == 1) this.display = affixes.getFirst() + TinyCaps.tinyCaps(name);
        else this.display = affixes.getFirst() + TinyCaps.tinyCaps(name) + affixes.getLast();

        this.trigger = new ItemBuilder(triggerMaterial).setGlint(true).setDisplay(this.display).setID(name).build();
    }

    public abstract void channel();

    public abstract void cast();

    @Override
    public Ability clone() {
        try {
            return (Ability) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
