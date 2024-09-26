package com.elikill58.negativity.minestom.impl.item;

import org.jetbrains.annotations.NotNull;

import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

public class MinestomEnchants {
	
	public static @NotNull DynamicRegistry.Key<Enchantment> getEnchant(com.elikill58.negativity.api.item.Enchantment enchant) {
		switch (enchant) {
		case AQUA_AFFINITY:
			return Enchantment.AQUA_AFFINITY;
		case BANE_OF_ARTHROPODS:
			return Enchantment.BANE_OF_ARTHROPODS;
		case BINDING_CURSE:
			return Enchantment.BINDING_CURSE;
		case BLAST_PROTECTION:
			return Enchantment.BLAST_PROTECTION;
		case CHANNELING:
			return Enchantment.CHANNELING;
		case DEPTH_STRIDER:
			return Enchantment.DEPTH_STRIDER;
		case EFFICIENCY:
			return Enchantment.EFFICIENCY;
		case FEATHER_FALLING:
			return Enchantment.FEATHER_FALLING;
		case FIRE_ASPECT:
			return Enchantment.FIRE_ASPECT;
		case FIRE_PROTECTION:
			return Enchantment.FIRE_PROTECTION;
		case FLAME:
			return Enchantment.FLAME;
		case FORTUNE:
			return Enchantment.FORTUNE;
		case FROST_WALKER:
			return Enchantment.FROST_WALKER;
		case IMPALING:
			return Enchantment.IMPALING;
		case INFINITY:
			return Enchantment.INFINITY;
		case KNOCKBACK:
			return Enchantment.KNOCKBACK;
		case LOOTING:
			return Enchantment.LOOTING;
		case LOYALTY:
			return Enchantment.LOYALTY;
		case LUCK_OF_THE_SEA:
			return Enchantment.LUCK_OF_THE_SEA;
		case LURE:
			return Enchantment.LURE;
		case MENDING:
			return Enchantment.MENDING;
		case MULTISHOT:
			return Enchantment.MULTISHOT;
		case PIERCING:
			return Enchantment.PIERCING;
		case POWER:
			return Enchantment.POWER;
		case PROJECTILE_PROTECTION:
			return Enchantment.PROJECTILE_PROTECTION;
		case PROTECTION:
			return Enchantment.PROTECTION;
		case PUNCH:
			return Enchantment.PUNCH;
		case QUICK_CHARGE:
			return Enchantment.QUICK_CHARGE;
		case RESPIRATION:
			return Enchantment.RESPIRATION;
		case RIPTIDE:
			return Enchantment.RIPTIDE;
		case SHARPNESS:
			return Enchantment.SHARPNESS;
		case SILK_TOUCH:
			return Enchantment.SILK_TOUCH;
		case SMITE:
			return Enchantment.SMITE;
		case SOUL_SPEED:
			return Enchantment.SOUL_SPEED;
		case SWEEPING:
			return Enchantment.SWEEPING_EDGE;
		case SWIFT_SNEAK:
			return Enchantment.SWIFT_SNEAK;
		case THORNS:
			return Enchantment.THORNS;
		case UNBREAKING:
			return Enchantment.UNBREAKING;
		case VANISHING_CURSE:
			return Enchantment.VANISHING_CURSE;
		}
		return null;
	}
}
