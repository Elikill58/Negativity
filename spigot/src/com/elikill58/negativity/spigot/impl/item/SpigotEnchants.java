package com.elikill58.negativity.spigot.impl.item;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class SpigotEnchants {

    @SuppressWarnings("deprecation")
    public static Enchantment getEnchant(com.elikill58.negativity.api.item.Enchantment en) {
        switch (en) {
            case EFFICIENCY:
                return Enchantment.DIG_SPEED;
            case SOUL_SPEED:
                return Enchantment.getByName("SOUL_SPEED");
            case THORNS:
                return Enchantment.THORNS;
            case UNBREAKING:
                return Enchantment.DURABILITY;
            case DEPTH_STRIDER:
                return Enchantment.DEPTH_STRIDER;
            case BANE_OF_ARTHROPODS:
                return Enchantment.DAMAGE_ARTHROPODS;
            case BINDING_CURSE:
                return Enchantment.BINDING_CURSE;
            case BLAST_PROTECTION:
                return Enchantment.PROTECTION_EXPLOSIONS;
            case CHANNELING:
                return Enchantment.CHANNELING;
            case FEATHER_FALLING:
                return Enchantment.PROTECTION_FALL;
            case FIRE_ASPECT:
                return Enchantment.FIRE_ASPECT;
            case FIRE_PROTECTION:
                return Enchantment.PROTECTION_FIRE;
            case FLAME:
                return Enchantment.ARROW_FIRE;
            case FORTUNE:
                return Enchantment.LOOT_BONUS_BLOCKS;
            case FROST_WALKER:
                return Enchantment.FROST_WALKER;
            case IMPALING:
                return Enchantment.IMPALING;
            case INFINITY:
                return Enchantment.ARROW_INFINITE;
            case KNOCKBACK:
                return Enchantment.KNOCKBACK;
            case LOOTING:
                return Enchantment.LOOT_BONUS_MOBS;
            case LOYALTY:
                return Enchantment.LOYALTY;
            case LUCK_OF_THE_SEA:
                return Enchantment.LUCK;
            case LURE:
                return Enchantment.LURE;
            case MENDING:
                return Enchantment.MENDING;
            case POWER:
                return Enchantment.ARROW_DAMAGE;
            case PROJECTILE_PROTECTION:
                return Enchantment.PROTECTION_PROJECTILE;
            case PROTECTION:
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case PUNCH:
                return Enchantment.ARROW_KNOCKBACK;
            case RIPTIDE:
                return Enchantment.RIPTIDE;
            case SHARPNESS:
                return Enchantment.DAMAGE_ALL;
            case SILK_TOUCH:
                return Enchantment.SILK_TOUCH;
            case VANISHING_CURSE:
                return Enchantment.VANISHING_CURSE;
            case AQUA_AFFINITY:
            case MULTISHOT:
            case PIERCING:
            case QUICK_CHARGE:
            case RESPIRATION:
            case SMITE:
            case SWEEPING:
            case SWIFT_SNEAK:
                // all unknow enchant
                return Enchantment.getByKey(NamespacedKey.minecraft(en.getId().split(":")[1]));
        }
        return null;
    }
}
