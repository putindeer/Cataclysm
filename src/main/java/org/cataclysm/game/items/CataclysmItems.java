package org.cataclysm.game.items;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.item.CataclysmItem;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public enum CataclysmItems {
    PARAGON_BLESSING(new CataclysmItem(Material.TURTLE_SCUTE, CataclysmColor.PARAGON).setDisplay("Paragon's Blessing").setDescription("Testamento del Paragon").setAbility("Escudo Absoluto", "Otorga inmunidad total al portador durante 15 segundos. No recibe daño ni efectos negativos.").setGlint(true)),
    PARAGON_QUARTZ(new ItemBuilder(Material.QUARTZ).setDisplay("<#c6a96e>Paragon's Quartz").setID("paragons_quartz")),

    LEMEGETON(new CataclysmItem(Material.CARROT_ON_A_STICK, CataclysmColor.LEMEGETON).setDisplay(TinyCaps.tinyCaps("Lemegeton")).setDescription("Deus sacer cataclysmatum").setAbility("Sapientia", "Otorga los efectos mejorados por el portador durante 30 segundos. Entra en enfriamiento por 5 minutos.").setNote("Shift + clic izquierdo para ver mejoras.").setGlint(true).setID("lemegeton")),
    TECTONITE(new ItemBuilder(Material.IRON_INGOT).setDisplay("<#82878c>Tectonite").setLore("Hierro fusionado de dureza inquebrantable", "<#93989c>").setID("tectonite")),
    ONYX(new ItemBuilder(Material.FLINT).setDisplay("<#505050>Onyx").setLore("Forjado en diamante y lágrimas de obsidiana", "<#616161>").setID("onyx")),
    VOID_STONE(new ItemBuilder(Material.FLINT).setDisplay("<#949494>Void Stone").setLore("Piedra mítica de la nada misma", "<#a1a1a1>").setGlint(true).setID("void_stone")),

    CATACLYSM_UPGRADE(new ItemBuilder(Material.NETHER_BRICK).setDisplay("<#935151>Cataclysm Upgrade").setLore("Útil para desbloquear una mejora si se abre el menú del Lemegeton con este en la off-hand.", "<#a1a1a1>").setGlint(true).setID("cataclysm_upgrade")),
    CATACLYSM_UPGRADE_TIER_2(new ItemBuilder(Material.NETHER_BRICK).setDisplay("<#935151>Cataclysm Upgrade").setLore("Útil para desbloquear una mejora si se abre el menú del Lemegeton con este en la off-hand.", "<#a1a1a1>").setGlint(true).setID("cataclysm_upgrade_tier_2")),
    CATACLYSM_UPGRADE_TIER_3(new ItemBuilder(Material.NETHER_BRICK).setDisplay("<#935151>Cataclysm Upgrade").setLore("Útil para desbloquear una mejora si se abre el menú del Lemegeton con este en la off-hand.", "<#a1a1a1>").setGlint(true).setID("cataclysm_upgrade_tier_3")),

    TWISTED_SWORD(new CataclysmItem(Material.NETHERITE_SWORD, ItemFamily.TWISTED_TOOLS).setDisplay("Twisted Sword").setDescription("Forjada con remanentes del abismo").setAttribute(Attribute.ATTACK_DAMAGE, 12, EquipmentSlotGroup.MAINHAND).setUnbreakable(true)),
    TWISTED_PICKAXE(new CataclysmItem(Material.NETHERITE_PICKAXE, ItemFamily.TWISTED_TOOLS).setDisplay("Twisted Pickaxe").setDescription("Forjada con remanentes del abismo").setAttribute(Attribute.ATTACK_DAMAGE, 8, EquipmentSlotGroup.MAINHAND).setUnbreakable(true)),
    TWISTED_AXE(new CataclysmItem(Material.NETHERITE_AXE, ItemFamily.TWISTED_TOOLS).setDisplay("Twisted Axe").setDescription("Forjada con remanentes del abismo").setAttribute(Attribute.ATTACK_DAMAGE, 16, EquipmentSlotGroup.MAINHAND).setUnbreakable(true)),
    TWISTED_SHOVEL(new CataclysmItem(Material.NETHERITE_SHOVEL, ItemFamily.TWISTED_TOOLS).setDisplay("Twisted Shovel").setDescription("Forjada con remanentes del abismo").setAttribute(Attribute.ATTACK_DAMAGE, 7.5, EquipmentSlotGroup.MAINHAND).setUnbreakable(true)),
    TWISTED_HOE(new CataclysmItem(Material.NETHERITE_HOE, ItemFamily.TWISTED_TOOLS).setDisplay("Twisted Hoe").setDescription("Forjada con remanentes del abismo").setAttribute(Attribute.ATTACK_DAMAGE, 2, EquipmentSlotGroup.MAINHAND).setUnbreakable(true)),
    TWISTED_FLESH(new ItemBuilder(Material.ROTTEN_FLESH).setDisplay("<#6C5B9A>Twisted Flesh").setID("twisted_flesh")),
    TWISTED_BONE(new ItemBuilder(Material.BONE).setDisplay("<#6C5B9A>Twisted Bone").setID("twisted_bone")),
    TWISTED_POWDER(new ItemBuilder(Material.GUNPOWDER).setDisplay("<#6C5B9A>Twisted Powder").setID("twisted_powder")),
    TWISTED_STRING(new ItemBuilder(Material.STRING).setDisplay("<#6C5B9A>Twisted String").setID("twisted_string")),
    TWISTED_PEARL(new ItemBuilder(Material.ENDER_PEARL).setDisplay("<#6C5B9A>Twisted Pearl").setID("twisted_pearl")),
    TWISTED_ROD(new ItemBuilder(Material.BLAZE_ROD).setDisplay("<#6C5B9A>Twisted Rod").setID("twisted_rod")),
    TWISTED_INGOT(new ItemBuilder(Material.NETHERITE_INGOT).setDisplay("<#6C5B9A>Twisted Ingot").setID("twisted_ingot")),

    ARCANE_MACE(new CataclysmItem(Material.MACE, ItemFamily.ARCANE_TOOLS).setDisplay("Arcane Mace").setDescription("Vibra con almas de tiempos arcanos", true).setAbility("Arcane Winds", "Lanza una Wind Charge desde la vista del jugador en línea recta. Entra en cooldown por 4 segundos.").setAttribute(Attribute.ATTACK_DAMAGE, 8, EquipmentSlotGroup.MAINHAND).setAttribute(Attribute.ATTACK_SPEED, 0.6, EquipmentSlotGroup.MAINHAND).addEnchant(Enchantment.DENSITY, 5).addEnchant(Enchantment.WIND_BURST, 3).setUnbreakable(true)),
    ARCANE_BOW(new CataclysmItem(Material.BOW, ItemFamily.ARCANE_TOOLS).setDisplay("Arcane Bow").setDescription("Vibra con almas de tiempos arcanos", true).setAbility("Arcane Power", "Ilumina a las entidades impactadas y ralentiza a las que tengan Glowing.").addEnchant(Enchantment.POWER, 10).addEnchant(Enchantment.INFINITY, 1).setUnbreakable(true)),
    ARCANE_SHIELD(new CataclysmItem(Material.SHIELD, ItemFamily.ARCANE_TOOLS).setDisplay("Arcane Shield").setDescription("Vibra con almas de tiempos arcanos", false).setAbility("Arcane Shield", "Arroja levemente y ralentiza a las entidades bloqueadas.").setGlint(true).setUnbreakable(true)),
    ARCANE_TRIDENT(new CataclysmItem(Material.TRIDENT, ItemFamily.ARCANE_TOOLS).setDisplay("Arcane Trident").setDescription("Vibra con almas de tiempos arcanos", true).setAbility("Arcane Riptide", "Tiene el encantamiento Riptide, pero en una versión potenciada.").setAttribute(Attribute.ATTACK_DAMAGE, 12, EquipmentSlotGroup.MAINHAND).setAttribute(Attribute.ATTACK_SPEED, 1.1, EquipmentSlotGroup.MAINHAND).setUnbreakable(true).addEnchant(Enchantment.RIPTIDE, 5)),
    ARCANE_TOTEM(new CataclysmItem(Material.TOTEM_OF_UNDYING, ItemFamily.ARCANE_TOOLS).setDisplay("Arcane Totem").setDescription("Vibra con almas de tiempos arcanos", false).setAbility("Arcane Rebirth", "Su consumo no impacta el nivel de mortalidad de su activador.").setGlint(true)),
    ARCANE_ROD(new ItemBuilder(Material.BREEZE_ROD).setDisplay("<#D19D4d>Arcane Rod").setID("arcane_rod").setGlint(true)),
    ARCANE_CORE(new ItemBuilder(Material.HEAVY_CORE).setDisplay("<#D19D4d>Arcane Core").setID("arcane_core")),
    ARCANE_NUGGET(new ItemBuilder(Material.GOLD_NUGGET).setDisplay("<#D19D4d>Arcane Nugget").setGlint(true).setID("arcane_nugget")),
    ARCANE_INGOT(new ItemBuilder(Material.GOLD_INGOT).setDisplay("<#D19D4d>Arcane Ingot").setGlint(true).setID("arcane_ingot")),
    ARCANE_SCUTE(new ItemBuilder(Material.TURTLE_SCUTE).setDisplay("<#D19D4d>Arcane Scute").setGlint(true).setID("arcane_scute")),

    VOID_HEART(new ItemBuilder(Material.HEART_OF_THE_SEA).setDisplay("<#46424A>Void Heart").addLore("<#77727A>Un fragmento <obf>v</obf>acío.").addLore("<#77727A>Parece <obf>p</obf>ulsar levemente.").setGlint(true).setID("void_heart")),
    TWISTED_RELIC(new CataclysmItem(Material.HEART_OF_THE_SEA, CataclysmColor.TWISTED).setDisplay("Twisted Relic").setDescription("Tesoro proveniente de las profundidades del abismo.").setPassive("Twisted Blessing", "Aumenta cuatro corazones al portador mientras esté en el inventario.").setGlint(true).setID("twisted_relic")),

    UR_TEAR(new ItemBuilder(Material.GHAST_TEAR).setDisplay("<#ada7a5>Ur-Tear").setLore("Remanente del lamento de un Ur-Ghast.", "<#919191>").setID("ur_tear")),
    TOXIC_MEMBRANE(new ItemBuilder(Material.PHANTOM_MEMBRANE).setDisplay("<#4d694f>Toxic Membrane").setLore("Remanente de algo hermoso.", "<#919191>").setID("toxic_membrane")),
    GOLDEN_CREAM(new ItemBuilder(Material.MAGMA_CREAM).setDisplay("<#c2b25f>Golden Cream").setLore("Remanente de algo odiable.", "<#bfb88f>").setID("golden_cream")),
    NIGHTMARE_BONE(new ItemBuilder(Material.BONE).setDisplay("<#363636>Nightmare Bone").setLore("Remanente de algo terrible.", "<#919191>").setGlint(true).setID("nightmare_bone")),
    CATACLYST_BONE(new ItemBuilder(Material.BONE).setDisplay("<#bababa>Cataclyst Bone").setLore("Remanente de un cataclismo.", "<#919191>").setID("cataclyst_bone")),
    GUARDIAN_HEART(new ItemBuilder(Material.HEART_OF_THE_SEA).setDisplay("<#828c8b>Guardian Heart").setLore("Remanente de un templo.", "<#919191>").setID("guardian_heart")),
    PALE_BALL(new ItemBuilder(Material.SLIME_BALL).setDisplay("<#c4c4c4>Pale Ball").setLore("Remanente de un bosque.", "<#919191>").setID("pale_ball")),
    LLAMA_FUR(new ItemBuilder(Material.LEATHER).setDisplay("<#c4c4b9>Llama Fur").setLore("Remanente de tiempos lejanos.", "<#919191>").setID("llama_fur")),
    MIDWAY_RELIC(new CataclysmItem(Material.SADDLE, CataclysmColor.CALAMITY).setDisplay("Midway Relic").setDescription("Reliquia ancestral forjada en tiempos de adversidad").setPassive("Postmortem", "Previene la maldición de la semana 4 con el efecto <obf>Mortem</obf>.").setGlint(true)),

    // Calamity Items
    CALAMITY_KEY(new CataclysmItem(Material.OMINOUS_TRIAL_KEY, CataclysmColor.CALAMITY).setDisplay("Calamity Key").setDescription("Desde las profundidades de las Calamity Chambers.")),
    CALAMITY_NUGGET(new CataclysmItem(Material.GOLD_NUGGET, CataclysmColor.CALAMITY).setDisplay("Calamity Nugget").setID("calamity_nugget")),
    CALAMITY_INGOT(new CataclysmItem(Material.NETHERITE_INGOT, CataclysmColor.CALAMITY).setDisplay("Calamity Ingot").setID("calamity_ingot")),
    CALAMITY_HELMET(new CataclysmItem(Material.NETHERITE_HELMET, ItemFamily.CALAMITY_ARMOR).setDisplay("Calamity Helmet").setDescription("Yelmo endurecido por la furia de un Ragnarök").setPassive("Calamitous Resilience", "Reduce el daño recibido por fuego en un 50% y otorga 4 corazones extra al portar el set completo de Calamity.").setAttributeLore(Attribute.ARMOR, 3, EquipmentSlotGroup.HEAD).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 3, EquipmentSlotGroup.HEAD).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.HEAD).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SPIRE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM).setUnbreakable(true)),
    CALAMITY_CHESTPLATE(new CataclysmItem(Material.NETHERITE_CHESTPLATE, ItemFamily.CALAMITY_ARMOR).setDisplay("Calamity Chestplate").setDescription("Peto endurecido por la furia de un Ragnarök").setPassive("Calamitous Resilience", "Reduce el daño recibido por fuego en un 50% y otorga 4 corazones extra al portar el set completo de Calamity.").setAttributeLore(Attribute.ARMOR, 8, EquipmentSlotGroup.CHEST).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 3, EquipmentSlotGroup.CHEST).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.CHEST).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SPIRE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM).setUnbreakable(true)),
    CALAMITY_LEGGINGS(new CataclysmItem(Material.NETHERITE_LEGGINGS, ItemFamily.CALAMITY_ARMOR).setDisplay("Calamity Leggings").setDescription("Perneras endurecidas por la furia de un Ragnarök").setPassive("Calamitous Resilience", "Reduce el daño recibido por fuego en un 50% y otorga 4 corazones extra al portar el set completo de Calamity.").setAttributeLore(Attribute.ARMOR, 6, EquipmentSlotGroup.LEGS).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 3, EquipmentSlotGroup.LEGS).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.LEGS).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SPIRE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM).setUnbreakable(true)),
    CALAMITY_BOOTS(new CataclysmItem(Material.NETHERITE_BOOTS, ItemFamily.CALAMITY_ARMOR).setDisplay("Calamity Boots").setDescription("Botas endurecidas por la furia de un Ragnarök").setPassive("Calamitous Resilience", "Reduce el daño recibido por fuego en un 50% y otorga 4 corazones extra al portar el set completo de Calamity.").setAttributeLore(Attribute.ARMOR, 3, EquipmentSlotGroup.FEET).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 3, EquipmentSlotGroup.FEET).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.FEET).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SPIRE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM).setUnbreakable(true)),
    CALAMITY_TOTEM(new CataclysmItem(Material.TOTEM_OF_UNDYING, CataclysmColor.CALAMITY).setDisplay("Calamity Totem").setDescription("Forjada del terror de los mortales").setAbility("Calamity Rebirth", "Su consumo no impacta el nivel de mortalidad de su activador.")),
    CALAMITY_APPLE(new CataclysmItem(Material.GOLDEN_APPLE, CataclysmColor.CALAMITY).setDisplay("Calamity Apple").setDescription("Infundida con la fuerza de mil tormentas").setAbility("Calamity Purge", "Otorga Regeneración II por 10s, Absorción IV por 2 minutos y Resistencia al Fuego por 2 minutos y 30 segundos.")),
    ENCHANTED_CALAMITY_APPLE(new CataclysmItem(Material.ENCHANTED_GOLDEN_APPLE, CataclysmColor.CALAMITY).setDisplay("Enchanted Calamity Apple").setDescription("Infundida con la fuerza de mil tormentas").setAbility("Calamity Purge II", "Otorga Regeneración III por 20s, Absorción V por 2 minutos, Resistencia al Fuego y Resistencia I por 5 minutos. Elimina efectos negativos.")),
    CALAMITY_CARROT(new CataclysmItem(Material.GOLDEN_CARROT, CataclysmColor.CALAMITY).setDisplay("Calamity Carrot").setDescription("Potenciada por la velocidad de un ciclón").setAbility("Calamity Agility", "Restaura ligeramente la saturación y otorga Velocidad IV por 1 minuto.")),

    ELDERS_EYE(new ItemBuilder(Material.SPIDER_EYE).setDisplay("<#5e3c2a>Elder's Eye").setLore("Remanente de un guardián anciano.", "<#919191>").setID("elders_eye")),
    GOLEM_HEAD(new ItemBuilder(Material.CARVED_PUMPKIN).setDisplay("<#9b8d75>Golem Head").setLore("Remanente de una leyenda.", "<#919191>").setID("golem_head")),
    DROWNED_CROWN(new ItemBuilder(Material.GOLD_NUGGET).setDisplay("<#6f8c8e>Drowned Crown").setLore("Remanente de un reino hundido.", "<#919191>").setID("drowned_crown")),
    WANDERING_HEART(new ItemBuilder(Material.HEART_OF_THE_SEA).setDisplay("<#7a9ca1>Wandering Heart").setLore("Remanente de un viajero eterno.", "<#919191>").setID("wandering_heart")),
    WANDERING_SOUL(new ItemBuilder(Material.NETHER_STAR).setDisplay("<#b5a6d3>Wandering Soul").setLore("Remanente de un espíritu errante.", "<#919191>").setID("wandering_soul")),

    MIRAGE_SCUTE(new CataclysmItem(Material.TURTLE_SCUTE, CataclysmColor.MIRAGE).setDisplay("Mirage Scute")),
    MIRAGE_HELMET(new CataclysmItem(Material.TURTLE_HELMET, CataclysmColor.MIRAGE).setDisplay("Mirage Helmet").setDescription("Forjado con la ilusión de un miraje").setPassive("Ilusión", "Te hace inmune a todos los efectos negativos, incluso congelamiento y ahogamiento.").setNote("Protege menos que un Calamity Helmet.").setAttribute(Attribute.ARMOR, 1, EquipmentSlotGroup.HEAD).setUnbreakable(true).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.DUNE))),

    MIRAGE_PEARL(new CataclysmItem(Material.ENDER_PEARL, CataclysmColor.MIRAGE).setDisplay("Mirage Pearl")),
    MIRAGE_FLESH(new CataclysmItem(Material.ROTTEN_FLESH, CataclysmColor.MIRAGE).setDisplay("Mirage Flesh")),
    MIRAGE_POWDER(new CataclysmItem(Material.GUNPOWDER, CataclysmColor.MIRAGE).setDisplay("Mirage Powder")),
    MIRAGE_BONE(new CataclysmItem(Material.BONE, CataclysmColor.MIRAGE).setDisplay("Mirage Bone")),
    MIRAGE_TEAR(new CataclysmItem(Material.GHAST_TEAR, CataclysmColor.MIRAGE).setDisplay("Mirage Tear")),
    MIRAGE_INGOT(new CataclysmItem(Material.NETHERITE_INGOT, CataclysmColor.MIRAGE).setDisplay("Mirage Ingot")),
    MIRAGE_EYEBALL(new CataclysmItem(Material.CLAY_BALL, CataclysmColor.MIRAGE).setDisplay("Mirage Eyeball")),
    MIRAGE_QUARTZ(new CataclysmItem(Material.QUARTZ, CataclysmColor.MIRAGE).setDisplay("Mirage Quartz")),
    MIRAGE_ESSENCE(new CataclysmItem(Material.REDSTONE, CataclysmColor.MIRAGE).setDisplay("Mirage Essence")),
    MIRAGE_AMETHYST(new CataclysmItem(Material.AMETHYST_SHARD, CataclysmColor.MIRAGE).setDisplay("Mirage Amethyst")),
    WHALE_WING(new CataclysmItem(Material.ARMADILLO_SCUTE, CataclysmColor.MIRAGE).setDisplay("Whale Wing")),

    MIRAGE_BLESSING(new CataclysmItem(Material.TURTLE_SCUTE, CataclysmColor.MIRAGE).setDisplay("Mirage's Blessing").setDescription("Eco de una eternidad").setAbility("Escudo Ilusorio", "Otorga Inmunidad y Velocidad III al portador y a los aliados cercanos durante 15 segundos.").setGlint(true)),
    PARAGON_PEARL(new CataclysmItem(Material.ENDER_PEARL, CataclysmColor.PARAGON).setDisplay("Paragon Pearl").setDescription("Fragmento de la gracia del Paragon").setAbility("Escudo Efímero", "Tras teletransportarte, otorga inmunidad total durante 7 segundos. No recibes daño ni efectos negativos.").setGlint(true)),
    ENDER_BAG(new CataclysmItem(Material.ARMADILLO_SCUTE, CataclysmColor.ENDER).setDisplay("Ender Bag").setAbility("Ender Chest", "Abre tu cofre de ender portátil sin necesidad de colocarlo.")),
    OBSCURE_ONYX(new CataclysmItem(Material.FLINT, CataclysmColor.ENDER).setDisplay("Obscure Onyx").setDescription("Forjado con la ilusión del abismo")),

    // Items Upgrades
    MACE_UPGRADE(new CataclysmItem(Material.HEART_OF_THE_SEA, CataclysmColor.MIRAGE).setDisplay("Mace Upgrade")),
    MIRAGE_UPGRADE(new CataclysmItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, CataclysmColor.MIRAGE).setDisplay("Mirage Upgrade")),

    BROKEN_ELYTRA(new CataclysmItem(Material.TURTLE_SCUTE, CataclysmColor.ENDER).setDisplay("Broken Elytra")),
    MIRAGE_ELYTRA(new CataclysmItem(Material.ELYTRA, ItemFamily.MIRAGE_TOOLS).setDisplay("Mirage Elytra").setDescription("Tejida con la ilusión de un miraje.").setUnbreakable(true)),
    MIRAGE_MACE(new CataclysmItem(Material.MACE, ItemFamily.MIRAGE_TOOLS).setDisplay("Mirage Mace").setDescription("Forjada con la ilusión de un miraje").setPassive("Dualidad", "Alterna entre dos modos: uno lanza Wind Charges y otro funciona como Grappling Hook.").setAttribute(Attribute.ATTACK_DAMAGE, 10, EquipmentSlotGroup.MAINHAND).setAttribute(Attribute.ATTACK_SPEED, 0.4, EquipmentSlotGroup.MAINHAND).setUnbreakable(true).addEnchant(Enchantment.DENSITY, 7).addEnchant(Enchantment.WIND_BURST, 3).setID("mirage_mace_normal")),
    MIRAGE_APPLE(new CataclysmItem(Material.CARROT_ON_A_STICK, CataclysmColor.MIRAGE).setDisplay("Mirage Apple").setDescription("Fruto de una ilusión").setAbility("Compensación", "Al consumirla, el portador recibe 2 corazones permanentes y un Mirage Quartz.").setNote("Solo para personas que NO asistieron a la segunda incursión.")),

    PARAGON_TOTEM(new CataclysmItem(Material.TOTEM_OF_UNDYING, CataclysmColor.PARAGON).setDisplay("Paragon Totem").setDescription("Superstes Numisma").setAbility("Postmortalis", "Se activa incluso con Mortem. No reduce la mortalidad y otorga inmunidad por 7 segundos.").setGlint(true)),
    PALE_TOKEN(new CataclysmItem(Material.PRISMARINE_CRYSTALS, CataclysmColor.PALE).setDisplay("Pale Token").setDescription("Collisio Mundorum", false).setGlint(true)),
    PALE_SWORD(new CataclysmItem(Material.NETHERITE_SWORD, ItemFamily.PALE_TOOLS).setDisplay("Pale Sword").setDescription("Ex umbra silvae confectum", true).setAbility("Sagitta Mortis", "Inflige daño verdadero que atraviesa toda defensa, aplica Wither de alto nivel durante 5 segundos, ralentiza, debilita e ilumina a la víctima.").setAttribute(Attribute.ATTACK_DAMAGE, 20, EquipmentSlotGroup.MAINHAND).addEnchant(Enchantment.BREACH, 7).addEnchant(Enchantment.SHARPNESS, 5).addEnchant(Enchantment.BANE_OF_ARTHROPODS, 5).addEnchant(Enchantment.SMITE, 5).addEnchant(Enchantment.LOOTING, 3).addEnchant(Enchantment.SWEEPING_EDGE, 3).setUnbreakable(true)),
    PALE_BOW(new CataclysmItem(Material.BOW, ItemFamily.PALE_TOOLS).setDisplay("Pale Bow").setDescription("Ex umbris silvae confectum", true).setAbility("Sagitta Mortis", "Dispara una flecha que inflige daño verdadero ignorando defensas, aplica Wither de alto nivel durante 5 segundos, ralentiza, debilita e ilumina a la víctima.").addEnchant(Enchantment.POWER, 20).addEnchant(Enchantment.BREACH, 7).addEnchant(Enchantment.INFINITY, 1).addEnchant(Enchantment.PUNCH, 2).addEnchant(Enchantment.FLAME, 2).setUnbreakable(true)),
    PALE_HELMET(new CataclysmItem(Material.NETHERITE_HELMET, ItemFamily.PALE_ARMOR).setDisplay("Pale Helmet").setDescription("Ex essentia silvae nullius confectum", true).setPassive("Aeternitas", "Al portar el set completo Pale otorga los efectos de la Calamity Armor, corazones adicionales e inmunidad a Pale Corrosion y Mortem").setAttributeLore(Attribute.ARMOR, 4, EquipmentSlotGroup.HEAD).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 3, EquipmentSlotGroup.HEAD).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.HEAD).setUnbreakable(true).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.TIDE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM)),
    PALE_CHESTPLATE(new CataclysmItem(Material.NETHERITE_CHESTPLATE, ItemFamily.PALE_ARMOR).setDisplay("Pale Chestplate").setDescription("Ex essentia silvae nullius confectum", true).setPassive("Aeternitas", "Al portar el set completo Pale otorga los efectos de la Calamity Armor, corazones adicionales e inmunidad a Pale Corrosion y Mortem").setAttributeLore(Attribute.ARMOR, 9, EquipmentSlotGroup.CHEST).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 9, EquipmentSlotGroup.CHEST).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.CHEST).setUnbreakable(true).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.TIDE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM)),
    PALE_LEGGINGS(new CataclysmItem(Material.NETHERITE_LEGGINGS, ItemFamily.PALE_ARMOR).setDisplay("Pale Leggings").setDescription("Ex essentia silvae nullius confectum", true).setPassive("Aeternitas", "Al portar el set completo Pale otorga los efectos de la Calamity Armor, corazones adicionales e inmunidad a Pale Corrosion y Mortem").setAttributeLore(Attribute.ARMOR, 7, EquipmentSlotGroup.LEGS).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 9, EquipmentSlotGroup.LEGS).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.LEGS).setUnbreakable(true).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.TIDE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM)),
    PALE_BOOTS(new CataclysmItem(Material.NETHERITE_BOOTS, ItemFamily.PALE_ARMOR).setDisplay("Pale Boots").setDescription("Ex essentia silvae nullius confectum", true).setPassive("Aeternitas", "Al portar el set completo Pale otorga los efectos de la Calamity Armor, corazones adicionales e inmunidad a Pale Corrosion y Mortem").setAttributeLore(Attribute.ARMOR, 4, EquipmentSlotGroup.FEET).setAttributeLore(Attribute.ARMOR_TOUGHNESS, 9, EquipmentSlotGroup.FEET).setAttributeLore(Attribute.KNOCKBACK_RESISTANCE, 0.1, EquipmentSlotGroup.FEET).setUnbreakable(true).addTrims(new ArmorTrim(TrimMaterial.IRON, TrimPattern.TIDE)).setFlag(ItemFlag.HIDE_ARMOR_TRIM)),

    REGENERATE_STRUCTURE(new CataclysmItem(Material.GOLD_NUGGET, ItemFamily.ARCANE_TOOLS).setDisplay("REGENERATE STRUCTURE").setUnbreakable(true))
    ;

    private final ItemBuilder itemBuilder;

    CataclysmItems(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    public static @NotNull List<CataclysmItems> getList() {return new ArrayList<>(List.of(CataclysmItems.values()));}

    public ItemBuilder getBuilder() {
        return this.itemBuilder;
    }

    public ItemBuilder cloneBuilder() {
        return this.itemBuilder.clone();
    }

    public ItemStack build() {
        return this.itemBuilder.build();
    }

    public net.minecraft.world.item.ItemStack buildAsNMS() {
        return this.itemBuilder.buildAsNMS();
    }
}
