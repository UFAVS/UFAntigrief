package com.example.ufantigrief;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class ValuableItems {
    private static final Set<Material> valuableItems = new HashSet<>();

    static {
        // Добавьте все ценные предметы здесь
        valuableItems.add(Material.DIAMOND_BLOCK);
        valuableItems.add(Material.DIAMOND_AXE);
        valuableItems.add(Material.DIAMOND_HOE);
        valuableItems.add(Material.DIAMOND_PICKAXE);
        valuableItems.add(Material.DIAMOND_SHOVEL);
        valuableItems.add(Material.DIAMOND_SWORD);
        valuableItems.add(Material.NETHERITE_AXE);
        valuableItems.add(Material.NETHERITE_HOE);
        valuableItems.add(Material.NETHERITE_PICKAXE);
        valuableItems.add(Material.NETHERITE_SHOVEL);
        valuableItems.add(Material.NETHERITE_SWORD);
        valuableItems.add(Material.DIAMOND_HELMET);
        valuableItems.add(Material.DIAMOND_CHESTPLATE);
        valuableItems.add(Material.DIAMOND_LEGGINGS);
        valuableItems.add(Material.DIAMOND_BOOTS);
        valuableItems.add(Material.NETHERITE_HELMET);
        valuableItems.add(Material.NETHERITE_CHESTPLATE);
        valuableItems.add(Material.NETHERITE_LEGGINGS);
        valuableItems.add(Material.NETHERITE_BOOTS);
        valuableItems.add(Material.ELYTRA);
        valuableItems.add(Material.DIAMOND);
        valuableItems.add(Material.NETHERITE_INGOT);
        valuableItems.add(Material.TNT);
        valuableItems.add(Material.END_CRYSTAL);
        valuableItems.add(Material.BEACON);
        valuableItems.add(Material.GOLDEN_CARROT);
        valuableItems.add(Material.TRIDENT);
        valuableItems.add(Material.SHULKER_BOX);
        valuableItems.add(Material.NETHERITE_SCRAP);
        valuableItems.add(Material.NETHER_STAR);
        valuableItems.add(Material.TOTEM_OF_UNDYING);
        valuableItems.add(Material.SHULKER_SHELL);
        valuableItems.add(Material.ENCHANTED_GOLDEN_APPLE);
        valuableItems.add(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        valuableItems.add(Material.DIAMOND_ORE);
        valuableItems.add(Material.DEEPSLATE_DIAMOND_ORE);
        valuableItems.add(Material.WITHER_SKELETON_SKULL);
        valuableItems.add(Material.CHEST);
        valuableItems.add(Material.BARREL);
        valuableItems.add(Material.WHITE_SHULKER_BOX);
        valuableItems.add(Material.ORANGE_SHULKER_BOX);
        valuableItems.add(Material.MAGENTA_SHULKER_BOX);
        valuableItems.add(Material.LIGHT_BLUE_SHULKER_BOX);
        valuableItems.add(Material.YELLOW_SHULKER_BOX);
        valuableItems.add(Material.LIME_SHULKER_BOX);
        valuableItems.add(Material.PINK_SHULKER_BOX);
        valuableItems.add(Material.GRAY_SHULKER_BOX);
        valuableItems.add(Material.LIGHT_GRAY_SHULKER_BOX);
        valuableItems.add(Material.CYAN_SHULKER_BOX);
        valuableItems.add(Material.PURPLE_SHULKER_BOX);
        valuableItems.add(Material.BLUE_SHULKER_BOX);
        valuableItems.add(Material.BROWN_SHULKER_BOX);
        valuableItems.add(Material.GREEN_SHULKER_BOX);
        valuableItems.add(Material.RED_SHULKER_BOX);
        valuableItems.add(Material.BLACK_SHULKER_BOX);
        valuableItems.add(Material.TRAPPED_CHEST);
        valuableItems.add(Material.RAIL);
        valuableItems.add(Material.POWERED_RAIL);
        valuableItems.add(Material.DETECTOR_RAIL);
        valuableItems.add(Material.ACTIVATOR_RAIL);
        valuableItems.add(Material.RESPAWN_ANCHOR);
        valuableItems.add(Material.MACE);
        valuableItems.add(Material.CREEPER_HEAD);
        valuableItems.add(Material.ZOMBIE_HEAD);
        valuableItems.add(Material.SKELETON_SKULL);
        valuableItems.add(Material.PIGLIN_HEAD);
        valuableItems.add(Material.DRAGON_HEAD);
        valuableItems.add(Material.OMINOUS_BOTTLE);
        valuableItems.add(Material.OMINOUS_TRIAL_KEY);
        valuableItems.add(Material.TRIAL_KEY);
        valuableItems.add(Material.NETHERITE_BLOCK);
        valuableItems.add(Material.TURTLE_HELMET);
        valuableItems.add(Material.BOW);
        valuableItems.add(Material.CROSSBOW);
        valuableItems.add(Material.SUSPICIOUS_STEW);
        valuableItems.add(Material.ENCHANTED_BOOK);
        valuableItems.add(Material.HEAVY_CORE);
        valuableItems.add(Material.DRAGON_BREATH);
        valuableItems.add(Material.EXPERIENCE_BOTTLE);
        valuableItems.add(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
        valuableItems.add(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);

        // Добавьте любые другие ценные предметы
    }

    public static boolean isValuable(Material material) {
        return valuableItems.contains(material);
    }

    public static Set<Material> getValuableItems() {
        return valuableItems;
    }
}
