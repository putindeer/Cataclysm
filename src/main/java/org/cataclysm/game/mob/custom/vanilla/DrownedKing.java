package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;

public class DrownedKing extends CataclysmMob {
    public DrownedKing(Level level) {
        super(new Drowned(EntityType.DROWNED, level), "Drowned King", "#ad8a5c", level);
        super.setAttribute(Attributes.SCALE, 1.5);
        super.setItem(EquipmentSlot.MAINHAND, CataclysmItems.ARCANE_TRIDENT.buildAsNMS());
        double rarity = Cataclysm.getDay() >= 28 ? 0.2 : 1;
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.DROWNED_CROWN.build(), 1, 1, rarity))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new DrownedKing(super.getLevel());
    }
}
