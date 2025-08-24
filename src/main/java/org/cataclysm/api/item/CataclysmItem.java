package org.cataclysm.api.item;

import lombok.Getter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.game.items.ItemFamily;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

@Getter
public class CataclysmItem extends ItemBuilder {
    private String color1;
    private String color2;
    private String color3;

    public CataclysmItem(Material material, @NotNull ItemFamily itemFamily) {
        this(material, itemFamily.getColor());
        super.setFamily(itemFamily);
    }

    public CataclysmItem(Material material, @NotNull CataclysmColor cataclysmColor) {
        this(material, cataclysmColor.getColor(), cataclysmColor.getColor2(), cataclysmColor.getColor3());
    }

    public CataclysmItem(Material material, String... colors) {
        super(material);
        if (colors[0] != null) this.color1 = "<" + colors[0] + ">";
        if (colors[1] != null) this.color2 = "<" + colors[1] + ">";
        if (colors[2] != null) this.color3 = "<" + colors[2] + ">";
    }

    public CataclysmItem setDisplay(String display) {
        super.setDisplay(this.color1 + display);
        super.setID(display.replace(" ", "_").replace("'", "").toLowerCase());
        return this;
    }

    public CataclysmItem setDescription(String description) {
        return this.setDescription(description, false);
    }

    public CataclysmItem setDescription(String description, boolean extraLine) {
        if (extraLine) super.addLore("");
        super.setLore("\"" + description + "\"", this.color3);
        super.addLore("");
        return this;
    }

    public CataclysmItem setAbility(String ability, String description) {
        super.addLore(this.color1 + TinyCaps.tinyCaps("Habilidad: ") + this.color2 + TinyCaps.tinyCaps(ability));
        super.setLore(description, this.color3);
        super.addLore("");
        return this;
    }

    public CataclysmItem setPassive(String ability, String description) {
        super.addLore(this.color1 + TinyCaps.tinyCaps("Pasiva: ") + this.color2 + TinyCaps.tinyCaps(ability));
        super.setLore(description, this.color3);
        super.addLore("");
        return this;
    }

    public CataclysmItem setNote(String note) {
        super.setLore(note, this.color3);
        super.addLore("");
        return this;
    }

    public CataclysmItem setStructure(String structure, String world) {
        super.addLore(this.color1 + TinyCaps.tinyCaps("Dungeon: ") + this.color2 + TinyCaps.tinyCaps(structure));
        super.addLore("");
        super.setCustomData("structure", structure.replace(" ", "_").toUpperCase());
        super.setCustomData("dimension", world.replace(" ", "_").toUpperCase());
        return this;
    }

    public CataclysmItem setAttribute(Attribute attribute, double modifier, EquipmentSlotGroup slot) {
        super.meta = super.item.getItemMeta();

        this.setAttributeLore(attribute, modifier, slot);
        super.meta.addAttributeModifier(attribute, new AttributeModifier(attribute.getKey(), modifier, AttributeModifier.Operation.ADD_NUMBER, slot));
        if (attribute != Attribute.ATTACK_SPEED) {
            if (this.item.getType().name().contains("SWORD") || this.item.getType().name().contains("AXE")) {
                float baseAttackSpeed = this.item.getType().name().contains("SWORD") ? -2.4F : -3.0F;
                super.meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(Attribute.ATTACK_SPEED.getKey(), baseAttackSpeed, AttributeModifier.Operation.ADD_NUMBER, slot));
            }
        }
        super.item.setItemMeta(super.meta);

        return this;
    }

    public CataclysmItem setAttributeLore(Attribute attribute, double modifier, EquipmentSlotGroup slot) {
        super.meta = super.item.getItemMeta();
        var lore = super.meta.lore();

        boolean[] hasAttributeTitle = {false};
        if (lore != null) lore.forEach(line -> {
            if (hasAttributeTitle[0]) return;
            hasAttributeTitle[0] = PlainTextComponentSerializer.plainText().serialize(line).contains("When");
        });
        if (!hasAttributeTitle[0]) {
            if (modifier != 0) super.addLore("<gray>When " + getSlotName(slot) + ":");
        }

        String formattedModifier = (modifier % 1 == 0) ? String.valueOf((int) modifier) : String.valueOf(modifier);
        if (attribute == Attribute.KNOCKBACK_RESISTANCE) formattedModifier = String.valueOf((int) (modifier * 10));
        if (modifier != 0) super.addLore(this.color1 + " +" + formattedModifier + getAttributeName(attribute));
        super.setFlag(ItemFlag.HIDE_ATTRIBUTES);
        super.item.setItemMeta(super.meta);

        return this;
    }

    public CataclysmItem setUnbreakable(boolean unbreakable) {
        this.meta = this.item.getItemMeta();
        super.setUnbreakable(unbreakable);
        super.setFlag(ItemFlag.HIDE_UNBREAKABLE);
        super.addLore(this.color2 + "Unbreakable");
        this.item.setItemMeta(this.meta);
        return this;
    }

    private String getSlotName(EquipmentSlotGroup slot) {
        return switch (slot.getExample()) {
            case HAND -> "in Main Hand";
            case OFF_HAND -> "in Off Hand";
            default -> "on " + slot.getExample().name().substring(0, 1).toUpperCase() + slot.getExample().name().substring(1).toLowerCase();
        };
    }

    private String getAttributeName(Attribute attribute) {
        String[] parts = attribute.key().value().split("_");
        if (parts.length == 0) return " " + attribute.key().value().substring(0, 1).toUpperCase() + attribute.key().value().substring(1).toLowerCase();
        else {
            StringBuilder name = new StringBuilder();
            for (String part : parts) {
                name.append(" ").append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
            }
            return name.toString();
        }
    }
}
